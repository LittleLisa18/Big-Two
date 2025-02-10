import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.DefaultCaret;

/**
 * The BigTwoGUI class constructs the graphical user interface for the Big Two card game.
 * It displays the current card condition of each players, messages of the game, and chat messages.
 * 
 * @author Liu Yantong
 */
public class BigTwoGUI implements CardGameUI{
	// private instances
	/**
	 * Big Two card game associated with this GUI
	 */
	private BigTwo game;
		
	/**
	 * A boolean array indicating which cards are being selected
	 */
	private boolean[] selected = new boolean[13];
		
	/**
	 * An integer specifying the index of the active player
	 */
	private int activePlayer = -1;
		
	/**
	 * The main window of the application
	 */
	private JFrame frame;
		
	/**
	 * A panel for showing the cards of each player and the cards played on the table
	 */
	private JPanel bigTwoPanel;
		
	/**
	 * A “Play” button for the active player to play the selected cards
	 */
	private JButton playButton;
		
	/**
	 * A “Pass” button for the active player to pass his/her turn to the next player
	 */
	private JButton passButton;
		
	/**
	 * A text area for showing the current game status as well as end of game messages
	 */
	private JTextArea msgArea;
		
	/**
	 * A text area for showing chat messages sent by the players
	 */
	private JTextArea chatArea;
		
	/**
	 * A text field for players to input chat messages
	 */
	private JTextField chatInput;
	
	/**
	 * An array to store the players' images
	 */
	private Image[] players;
	
	/**
	 * An 2D array to store the cards' images
	 */
	private Image[][] cardImages;
	
	/**
	 * An image variable to store the card's back image
	 */
	private Image cardBackImage;
	
	// Private methods
	
	/**
	 * Load the images from the folders
	 */
	private void loadImages() {
		players = new Image[4];
		players[0] = new ImageIcon("src/players/Ariel_38.png").getImage();
		players[1] = new ImageIcon("src/players/Aurora_38.png").getImage();
		players[2] = new ImageIcon("src/players/Belle_38.png").getImage();
		players[3] = new ImageIcon("src/players/Elsa_38.png").getImage();
		
		char[] rank = {'a', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k'};
		
		char[] suit = {'d', 'c', 'h', 's'};
		
		cardImages = new Image[4][13];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				cardImages[i][j] = new ImageIcon("src/cards/" + rank[j] + suit[i] + ".gif").getImage();
			}
		}
		
		cardBackImage = new ImageIcon("src/cards/b.gif").getImage();
	}
	
	/**
	 * Set the GUI.
	 */
	private void setGUI() {
		frame = new JFrame("Big Two");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setSize(1200,900);
		frame.setMinimumSize(new Dimension(1100, 800));
		frame.setBackground(new Color(255, 255, 224));
		
		// Create a menu bar
		JMenuBar menu = new JMenuBar();
		
		
		// Create game menu
		JMenu gameMenu = new JMenu("Game");
		
		// Add restart menu item
		JMenuItem connect = new JMenuItem("Connect");
		connect.addActionListener(new ConnectMenuItemListener());
		gameMenu.add(connect);
		
		// Add quit menu item
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new QuitMenuItemListener());
		gameMenu.add(quit);
		
		// Create a message menu
		JMenu messageMenu = new JMenu("Message");
		
		// Add menus to menu bar
		menu.add(gameMenu);
		menu.add(messageMenu);
		
		// Add menu bar to the frame
		frame.setJMenuBar(menu); 
		
		// Set bigTwoPanel
		bigTwoPanel= new BigTwoPanel();
		bigTwoPanel.setPreferredSize(new Dimension(700, 800));
		
		// Add bigTwoPanel to frame
		frame.add(bigTwoPanel, BorderLayout.CENTER);
		
		// Set messagePanel
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		
		// Set message area
		msgArea = new JTextArea(35, 40);
		msgArea.setEnabled(false);
		DefaultCaret msgAreaCaret = (DefaultCaret) msgArea.getCaret();
        msgAreaCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPaneMsg = new JScrollPane(msgArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
        // Set chat area
        chatArea = new JTextArea(35, 40);
        chatArea.setEnabled(false);
        DefaultCaret chatAreaCaret = (DefaultCaret) msgArea.getCaret();
        chatAreaCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scrollPaneChat = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Add two area to messagePanel
        messagePanel.add(scrollPaneMsg);
        messagePanel.add(scrollPaneChat);
        
        // Add messagePanel to frame
        frame.add(messagePanel, BorderLayout.EAST);
        
        // Create bottomPanel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        
        // ButtonPanel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setPreferredSize(new Dimension(700, 35));
        
        // playButton
        playButton = new JButton("Play");
        playButton.addActionListener(new PlayButtonListener());
        
        // passButton
        passButton = new JButton("Pass");
        passButton.addActionListener(new PassButtonListener());
        
        // Add buttons to buttonPanel
        buttonPanel.add(playButton);
        buttonPanel.add(passButton);
        
        // Enable / disable buttons
        if (activePlayer == game.getCurrentPlayerIdx()) {
        	buttonPanel.setEnabled(true);
			enable();
        }
        else {
        	buttonPanel.setEnabled(false);
			disable();
        }
        
        // ChatSentPanel
        JPanel chatSentPanel = new JPanel();
        chatSentPanel.setLayout(new FlowLayout());
        chatSentPanel.setPreferredSize(new Dimension(500, 35));
        JLabel chatSentLabel = new JLabel("Message: ");
        
        chatInput = new chatJTextField(30);
        
        chatSentPanel.add(chatSentLabel);
        chatSentPanel.add(chatInput);
        
        
        // Add buttonPanel to bottomPanel
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(chatSentPanel, BorderLayout.EAST);
        
        // Add button Panel to frame
        frame.add(bottomPanel, BorderLayout.SOUTH);
        
		
	    frame.setVisible(true);	
		
	}
	
	/**
	 * Resets the list of selected cards to an empty list.
	 */
	private void resetSelected() {
		for (int j = 0; j < selected.length; j++) {
			selected[j] = false;
		}
	}
	
	/**
	 * Returns an array of indices of the cards selected through the UI.
	 * 
	 * @return an array of indices of the cards selected, or null if no valid cards
	 *         have been selected
	 */
	private int[] getSelected() {
		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			int count1 = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count1] = j;
					count1++;
				}
			}
		}
		return cardIdx;
	}
	
	
	// Constructor
	/**
     * Constructs a BigTwoGUI instance with the specified game.
     * 
     * @param game the BigTwo game instance to be associated with the GUI
     */
	public BigTwoGUI(BigTwo game) {
		this.game = game;
		activePlayer = game.getClientIdx();
		setGUI();
		loadImages();
		resetSelected();
	}
	
	// public methods
	/**
     * Sets the active player of the game.
     * 
     * @param activePlayer the index of the active player
     */
	public void setActivePlayer(int activePlayer) {
		this.activePlayer = activePlayer;
	}
	
	/**
     * Repaints the GUI frame to reflect changes in the game.
     */
	public void repaint() {
		frame.repaint();
	}
	
	/**
	 * Clears the messages in the message area and the chat area.
	 */
	public void clearMsgArea() {
		msgArea.setText("");
		chatArea.setText("");
	}
	
	/**
     * Prints a message to the message area.
     * 
     * @param msg the message to be printed
     */
	public void printMsg(String msg) {
		msgArea.append(msg + "\n");
	}
	
	/**
     * Prints a chat message to the chat area.
     * 
     * @param msg the chat message to be printed
     */
	public void printChatMsg(String msg) {
		chatArea.append(msg + "\n");
	}
	
	/**
     * Resets the GUI to its initial state, 
     * including clearing selections, messages, enabling buttons, and removing all cards from the players.
     */
	public void reset() {
		resetSelected();
		clearMsgArea();
		enable();
		for (int i = 0; i < 4; i++) {
			game.getPlayerList().get(i).removeAllCards();
		}
	}
	
	/**
	 * Let the player check the game over message and get ready for the next game.
	 */
	public void gameEndConfirm() {
		JOptionPane.showMessageDialog(frame, "Player " + game.getPlayerList().get(game.getCurrentPlayerIdx()).getName() + 
									  " wins!", "Game Over!", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
     * Enables the play and pass buttons, as well as the big two panel.
     */
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		bigTwoPanel.setEnabled(true);
	}
	
	/**
     * Disables the play and pass buttons, as well as the big two panel.
     */
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		bigTwoPanel.setEnabled(false);
	}
	
	/**
     * Prompts the active player by printing their name and resetting the selection.
     */
	public void promptActivePlayer() {
		printMsg(game.getPlayerList().get(activePlayer).getName() + "'s turn: ");
		resetSelected();
	}
	
	// inner class
	
	/**
	 * The BigTwoPanel class represents the graphical panel for the Big Two card game.
	 * It handles the drawing of players, cards, and the game state.
	 */
	public class BigTwoPanel extends JPanel implements MouseListener{
		
		// private instance
		/**
		 * The start of x-coordinate for displaying players' names.
		 */
		private int nameXco = 5;
		
		/**
		 * The start of y-coordinate for displaying players' names.
		 */
		private int nameYco = 20;
		
		/**
		 * The start of x-coordinate for displaying players' photos.
		 */
		private int photoXco = 5;
		
		/**
		 * The start of y-coordinate for displaying players' photos.
		 */
		private int photoYco = 30;
		
		/**
		 * The start of x-coordinate for displaying players' cards.
		 */
		private int cardXco = 130;
		
		/**
		 * The space between exposed cards.
		 */
		private int cardExpose = 30;
		
		/**
		 * The start of y-coordinate for displaying a selected card.
		 */
		private int cardUpYco = 23;
		
		/**
		 * The start of y-coordinate for displaying a not-selected card.
		 */
		private int cardDownYco = 43;
		
		/**
		 * The vertical gap between player regions.
		 */
		private int regionGap = 160;
		
		/**
		 * The x-coordinate for drawing the separating line.
		 */
		private int separateLineXco = 2000;
		
		/**
		 * The y-coordinate for drawing the separating line.
		 */
		private int separateLineYco = 160;
		
		
		/**
	     * Constructs a BigTwoPanel instance and adds a mouse listener.
	     */
		public BigTwoPanel(){
			this.addMouseListener(this);
		}
		
		/**
		 * Paints the components of the panel, including player names, photos, and cards.
	     * This method is called whenever the panel needs to be redrawn.
	     * 
	     * @param g the Graphics object used for drawing
		 */
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D) g;
			// Set the background color
			this.setBackground(new Color(255, 255, 224));
			g.setColor(Color.BLACK);
			
			for (int i = 0; i < 4; i++) {
				if (i == game.getClientIdx()) {
					// Show player's name
					if (i == game.getCurrentPlayerIdx()) {
						g.setColor(new Color(245, 190, 220)); // LightPink
						if (game.getPlayerList().get(i).getName() == null || game.getPlayerList().get(i).getName().isEmpty()) {
				            game.getPlayerList().get(i).setName("Waiting...");
				        }
						g.drawString(game.getPlayerList().get(i).getName() + " (You)", nameXco , nameYco + regionGap * i); 
						g.setColor(Color.BLACK);
					}
					else {
						g.setColor(Color.BLACK);
						if (game.getPlayerList().get(i).getName() == null || game.getPlayerList().get(i).getName().isEmpty()) {
				            game.getPlayerList().get(i).setName("Waiting...");
				        }
						g.drawString(game.getPlayerList().get(i).getName() + " (You)", nameXco , nameYco + regionGap * i); 
					}
					
					// Draw the photo of the player
					g.drawImage(players[i], photoXco, photoYco + regionGap * i, this);
					
					// Draw cards of the player
					for (int j = 0; j < game.getPlayerList().get(i).getNumOfCards(); j++) {
						int suit = game.getPlayerList().get(i).getCardsInHand().getCard(j).getSuit();
						int rank = game.getPlayerList().get(i).getCardsInHand().getCard(j).getRank();
						if (selected[j]) {
							g.drawImage(cardImages[suit][rank], cardXco + cardExpose * j, cardUpYco + regionGap * i, this);
						}
						else {
							g.drawImage(cardImages[suit][rank], cardXco + cardExpose * j, cardDownYco + regionGap * i, this);
						}
					}
				}
				
				// The player is not the current player
				else {
					if (i == game.getCurrentPlayerIdx()) {
						g.setColor(new Color(245, 190, 220)); // LightPink
						if (game.getPlayerList().get(i).getName() == null || game.getPlayerList().get(i).getName().isEmpty()) {
				            game.getPlayerList().get(i).setName("Waiting...");
				        }
						g.drawString(game.getPlayerList().get(i).getName(), nameXco , nameYco + regionGap * i);
						g.setColor(Color.BLACK);
					}
					else {
						g.setColor(Color.BLACK);
						if (game.getPlayerList().get(i).getName() == null || game.getPlayerList().get(i).getName().isEmpty()) {
				            game.getPlayerList().get(i).setName("Waiting...");
				        }
						g.drawString(game.getPlayerList().get(i).getName(), nameXco , nameYco + regionGap * i); 
					}
					
					// Draw the photo of the player
					g.drawImage(players[i], photoXco, photoYco + regionGap * i, this);
					// Draw cards of the player
					for (int j = 0; j < game.getPlayerList().get(i).getNumOfCards(); j++) {
						g.drawImage(cardBackImage, cardXco + cardExpose * j, cardDownYco + regionGap * i, this);
					}	
				}
				
				// Draw the separate line
				g2D.drawLine(0, separateLineYco * (i + 1), separateLineXco, separateLineYco * (i + 1));
			}
			
			// Draw hands on table
			if (!game.getHandsOnTable().isEmpty()) {
				Hand lastHandOnTable = game.getHandsOnTable().get(game.getHandsOnTable().size()-1);
				// Last hand on table message
				g.drawString("Last Hand on Table [Played by " + lastHandOnTable.getPlayer().getName() + "]", nameXco, nameYco + regionGap * 4);
				// Back image
				g.drawImage(cardBackImage, 25, photoYco + regionGap * 4, this);
				// Last hand on table image
				for (int m = 0; m < lastHandOnTable.size(); m++) {
					int suit = lastHandOnTable.getCard(m).getSuit();
					int rank = lastHandOnTable.getCard(m).getRank();
					g.drawImage(cardImages[suit][rank], cardXco + cardExpose * m, photoYco + regionGap * 4, this);
				}
			}
			else {
				g.drawString("Last Hand on Table", nameXco, nameYco + regionGap * 4);
			}
			
			this.repaint();
		}
		
		/**
		 * This method handles mouse click events on the Big Two panel.
		 * It determines if a card was clicked, toggling its selection state.
		 * It updates the selected cards based on the mouse click position.
		 * 
		 * @param e the MouseEvent containing information about the click
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			if (!bigTwoPanel.isEnabled()) { 
	            return; 
	        }
			boolean haveSelected = false;
			int numOfCardsInHand = game.getPlayerList().get(activePlayer).getNumOfCards();
			
			if (activePlayer == game.getClientIdx()) {
				// Last card of the active player
				// Within the down card range
				if (!selected[numOfCardsInHand - 1] &&
					e.getX() >= cardXco + cardExpose * (numOfCardsInHand - 1) && e.getX() <= cardXco + cardExpose * (numOfCardsInHand - 1) + 73 &&
					e.getY() >= cardDownYco + regionGap * activePlayer && e.getY() <= cardDownYco + regionGap * activePlayer + 97) {
					selected[numOfCardsInHand - 1] = true;
					haveSelected = true;
				}
				// Within the up card range
				else if (selected[numOfCardsInHand - 1] &&
						 e.getX() >= cardXco + cardExpose * (numOfCardsInHand - 1) && e.getX() <= cardXco + cardExpose * (numOfCardsInHand - 1) + 73 &&
						 e.getY() >= cardUpYco + regionGap * activePlayer && e.getY() <= cardUpYco + regionGap * activePlayer + 97) {
					selected[numOfCardsInHand - 1] = false;
					haveSelected = true;
				}
				
				int i = numOfCardsInHand - 2;
				while (!haveSelected && i >= 0) {
					// Normal area
					// Judge first
					if (!selected[i] &&
						e.getX() >= cardXco + cardExpose * (i) && e.getX() <= cardXco + cardExpose * (i + 1) &&
						e.getY() >= cardDownYco + regionGap * activePlayer && e.getY() <= cardDownYco + regionGap * activePlayer + 97) {
						selected[i] = true;
						haveSelected = true;
					}
					else if (selected[i] &&
							 e.getX() >= cardXco + cardExpose * (i) && e.getX() <= cardXco + cardExpose * (i + 1) &&
							 e.getY() >= cardUpYco + regionGap * activePlayer && e.getY() <= cardUpYco + regionGap * activePlayer + 97) {
						selected[i] = false;
						haveSelected = true;
					}
					// Special area exposed by cards after it
					else if (!selected[i] &&
							 e.getX() >= cardXco + cardExpose * (i + 1) && e.getX() <= cardXco + cardExpose * (i) + 73 &&
							 e.getY() >= cardDownYco + regionGap * activePlayer + 97 - (cardDownYco - cardUpYco) && e.getY() <= cardDownYco + regionGap * activePlayer + 97) {
						selected[i] = true;
						haveSelected = true;
					}
					else if (selected[i] &&
							 e.getX() >= cardXco + cardExpose * (i) && e.getX() <= cardXco + cardExpose * (i) + 73 &&
							 e.getY() >= cardUpYco + regionGap * activePlayer && e.getY() <= cardDownYco + regionGap * activePlayer) {
						selected[i] = false;
						haveSelected = true;
					}
			
					i--;
					repaint();
				}
			}
		}
		
		/**
		 * Invoked when the mouse button has been pressed on a component. (Not modified)
		 * 
		 * @param e the MouseEvent containing information about the press
		 */
		@Override
		public void mousePressed(MouseEvent e) {
		}

		/**
		 * Invoked when the mouse button has been released on a component. (Not modified)
		 * 
		 * @param e the MouseEvent containing information about the release
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
		}

		/**
		 * Invoked when the mouse enters a component. (Not modified)
		 * 
		 * @param e the MouseEvent containing information about the entry
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
		}

		/**
		 * Invoked when the mouse exits a component.(Not modified)
		 * 
		 * @param e the MouseEvent containing information about the exit
		 */
		@Override
		public void mouseExited(MouseEvent e) {
		}
			
	}
	
	/**
	 * This class handles the action performed when the play button is clicked.
	 * It checks if the current player is the active player and if any cards are selected to make a move.
	 */
	public class PlayButtonListener implements ActionListener{
		/**
		 * This method handles the event when the user want to play the cards he/she selected.
		 * 
		 * @param e the ActionEvent containing information of "play the selected cards"
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (game.getCurrentPlayerIdx() == activePlayer){
				if (getSelected() != null) {
					game.makeMove(activePlayer, getSelected());
					resetSelected();
				}
				else {
					printMsg("Please select card(s) to play.");
				}
			}
			repaint();
		}	
	}
	
	/**
	 * This class represents a text field for entering chat messages.
	 * It automatically adds an action listener to handle the enter key event.
	 */
	public class chatJTextField extends JTextField implements ActionListener {
		/**
	     * Constructs a chatJTextField with the specified number of columns.
	     * 
	     * @param c the number of columns to display in the text field
	     */
		public chatJTextField(int c) {
			super(c);
			addActionListener(this);
		}
		
		/**
	     * Handles the action event when the user presses enter and want to send the chat message.
	     * Sends the chat message to the chat area.
	     * 
	     * @param e the ActionEvent containing information about "send"
	     */
		@Override
		public void actionPerformed(ActionEvent e) {
			String chatMsg = getText();
			CardGameMessage message = new CardGameMessage(CardGameMessage.MSG, game.getClientIdx(), chatMsg);
			game.getClient().sendMessage(message);
			this.setText("");
		}
	}
	
	/**
	 * This class handles the action performed when the pass button is clicked.
	 * It allows the current player to pass their turn.
	 */
	public class PassButtonListener implements ActionListener{
		/**
		 * Handles the action event when the user presses enter and want to pass his/her turn.
		 * 
		 * @param e the ActionEvent containing information about "pass"
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (game.getCurrentPlayerIdx() == activePlayer) {
				game.makeMove(activePlayer, null);
				resetSelected();
			}
			repaint();
		}
	}
	
	/**
	 * This class handles the action performed when the connect menu item is selected.
	 * It connects the current player's client with the server.
	 */
	public class ConnectMenuItemListener implements ActionListener{
		/**
		 * This method handles the event when the user want to connect with the server.
		 * 
		 * @param e the ActionEvent containing information of "connect"
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (game.getClient().getConnectionStatus()) {
				printMsg("You have connected with the server!");
			}
			else {
				game.getClient().connect();
				printMsg("Player " + game.getPlayerList().get(game.getClientIdx()).getName() + " successfully connect with the server!");
			}
		}
	}
	
	/**
	 * This class handles the action performed when the quit menu item is selected.
	 * It terminates the application.
	 */
	public class QuitMenuItemListener implements ActionListener{
		/**
		 * This method handles the event when the user want to terminate the application and quit the game.
		 * 
		 * @param e the ActionEvent containing information of "quit"
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			CardGameMessage message = new CardGameMessage(CardGameMessage.QUIT, game.getClientIdx(), null);
			game.getClient().sendMessage(message);
			System.exit(0);
		}
	}
}
