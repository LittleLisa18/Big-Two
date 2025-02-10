import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * The BigTwoClient class is responsible for managing the client-side logic of the Big Two game.
 * It handles the connection to the game server, message parsing, and communication with the GUI and game logic.
 * 
 * @author Liu Yantong
 */
public class BigTwoClient implements NetworkGame{
	// private instance variables
	
	private BigTwo game; // The game logic object
	
	private BigTwoGUI gui; // The graphical user interface
	
	private Socket sock; // The socket for communication with the server
	
	private ObjectOutputStream oos; // Output stream for sending messages to the server
	
	private int playerID; // The player's ID
	
	private String playerName; // The player's name
	
	private String serverIP; // The IP address of the game server
	
	private int serverPort; // The port number of the game server
	
	private boolean isConnected = false; // Connection status
	
	// public constructor
	
	/**
	 * Constructs a BigTwoClient object with the given game logic and GUI.
     * Initializes the client, connects to the server, and prepares for communication.
     *
     * @param game the game logic object
     * @param gui the graphical user interface
	 */
	public BigTwoClient(BigTwo game, BigTwoGUI gui) {
		this.game = game;
		this.gui = gui;
		
		// Enter name
		String name_enter = (String)JOptionPane.showInputDialog("Enter your name: ");
		while (name_enter == null || name_enter == "") {
			name_enter = (String)JOptionPane.showInputDialog("Enter your name again: ");
		}
		playerName = name_enter;
		
		serverIP = "127.0.0.1";
		setServerPort(2396);
		connect();
		this.game.setCurrentPlayerIdx(-1);
		gui.disable();
		gui.repaint();
	}
	
	// public methods
	
	/**
     * Returns the player's ID.
     *
     * @return the player's ID
     */
	@Override
	public int getPlayerID() {
		return playerID;
	}
	
	/**
     * Sets the player's ID.
     *
     * @param playerID the player's ID
     */
	@Override
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	
	/**
     * Returns the player's name.
     *
     * @return the player's name
     */
	@Override
	public String getPlayerName() {
		return playerName;
	}
	
	/**
     * Sets the player's name.
     *
     * @param playerName the player's name
     */
	@Override
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	
	/**
     * Returns the server's IP address.
     *
     * @return the server's IP address
     */
	@Override
	public String getServerIP() {
		return serverIP;
	}
	
	/**
     * Sets the server's IP address.
     *
     * @param serverIP the server's IP address
     */
	@Override
	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	
	/**
     * Returns the server's port number.
     *
     * @return the server's port number
     */
	@Override
	public int getServerPort() {
		return serverPort;
	}
	
	/**
     * Sets the server's port number.
     *
     * @param serverPort the server's port number
     */
	@Override
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * Get the connection status
	 * 
	 * @return connection status of the player
	 */
	public boolean getConnectionStatus() {
		return isConnected;
	}
	
	/**
     * Connects to the game server, starts a thread for handling messages.
     */
	@Override
	public synchronized void connect() {
		try {
			sock = new Socket(getServerIP(), getServerPort());
			oos = new ObjectOutputStream(sock.getOutputStream());
			isConnected = true;
			Runnable connection = new ServerHandler();
			Thread playerThread = new Thread(connection);
			playerThread.start();
			
			gui.repaint();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}	
	}
	
	/**
     * Parses incoming messages from the server and updates the game state and GUI accordingly.
     *
     * @param message the message received from the server
     */
	@Override
	public synchronized void parseMessage(GameMessage message){
		if (message.getType() == CardGameMessage.FULL) {
			gui.printMsg("The server is full and the player cannot be joined!");
			try {
				sock.close();
			}
			catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		else if (message.getType() == CardGameMessage.PLAYER_LIST) {
			int id = message.getPlayerID();
			playerID = id;
			game.setCurrentPlayerIdx(id);
			gui.setActivePlayer(id);
			// Set the names
			for(int i = 0; i < 4; i++) {
				String[] pl_names = (String[]) message.getData();
				if (pl_names[i] != null) {
					game.getPlayerList().get(i).setName(pl_names[i]);
				}
			}
			
			CardGameMessage playerJoin = new CardGameMessage(CardGameMessage.JOIN, -1, this.getPlayerName());
			sendMessage(playerJoin);
		}
		else if(message.getType() == CardGameMessage.JOIN) {
			int id = message.getPlayerID();
			String pl_name = (String)message.getData();
			// Set the name
			game.getPlayerList().get(id).setName(pl_name);
			gui.printMsg("Player " + pl_name + " joined the game!");
			
			if (this.getPlayerID() == id) {
				CardGameMessage playerReady = new CardGameMessage(CardGameMessage.READY, -1, null);
				sendMessage(playerReady);
			}
		}
		else if (message.getType() == CardGameMessage.QUIT) {
			int id = message.getPlayerID();
			String pl_name = game.getPlayerList().get(id).getName();
			gui.printMsg("Player " + pl_name + " left the game.");
			game.getPlayerList().get(id).setName(null);
			// If game is in progress
			if (!game.endOfGame()) {
				// Send ready message
				CardGameMessage newMsg = new CardGameMessage(CardGameMessage.READY, -1, null);
				sendMessage(newMsg);
				// Remove all the cards of other players
				for(int i = 0; i < 4; i++) {
					game.getPlayerList().get(i).removeAllCards();
				}
				// Remove hands on table
				game.setHandsOnTable(new ArrayList<Hand>());
				// repaint
				gui.repaint();
				// Disable the panel
				gui.disable();
			}
		}
		else if (message.getType() == CardGameMessage.READY) {
			int id = message.getPlayerID();
			gui.printMsg("Player " + id + " is ready.");
		}
		else if (message.getType() == CardGameMessage.START) {
			BigTwoDeck deck = (BigTwoDeck) message.getData();
			// Start the game
			game.start(deck);
			gui.enable();
		}
		else if (message.getType() == CardGameMessage.MOVE) {
			int id = message.getPlayerID();
			int[] cardsSelected = (int[]) message.getData();
			// Player's move
			game.checkMove(id, cardsSelected);
		}
		else if (message.getType() == CardGameMessage.MSG) {
			String chat_Msg = (String) message.getData();
			gui.printChatMsg(chat_Msg);
		}
		gui.repaint();
	}
	
	/**
     * Sends a message to the server.
     *
     * @param message the message to be sent to the server
     */
	@Override
	public synchronized void sendMessage(GameMessage message) {
		try {
			oos.writeObject(message);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	// inner class
	
	/**
     * The ServerHandler inner class handles receiving messages from the server.
     * in a separate thread and invokes the parseMessage() method to process them.
     */
	public class ServerHandler implements Runnable{
		/**
         * Runs the message handling thread, continuously listening for messages from the server.
         * Upon receiving a message, it calls the parseMessage() method for further processing.
         */
		@Override
		public void run() {	
			CardGameMessage message = null;
			try{
				// Set an input stream
				ObjectInputStream ois = new ObjectInputStream(sock.getInputStream());
				while(!sock.isClosed()) {
					try {
						message = (CardGameMessage) ois.readObject();
						if(message != null){
							parseMessage(message);
						}
					}
					catch (IOException ex){
						System.out.println("Error in networking: " + ex.getMessage());
	                    break;
					}
				}
				ois.close();
			} 
			catch (Exception ex) {
				ex.printStackTrace();
			}
			gui.repaint();
		}
	}
}
