import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * The BigTwo class implements the game of Big Two. It manages players, the deck of cards, and the game logic.
 * 
 * <p>This class handles the starting of the game, player moves, and 
 * determining the end of the game. It also integrates with a UI
 * class to provide a interface for the game.</p>
 * 
 * @author Liu Yantong
 */
public class BigTwo {
	private int numOfPlayers;
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private int currentPlayerIdx;
	private BigTwoGUI ui;
	private BigTwoClient gameClient;

	/**
	 * Construct a BigTwo game, initialing the playerList, handsOnTable, deck and UI.
	 */
	public BigTwo(){
		this.playerList = new ArrayList<CardGamePlayer>();
		this.handsOnTable = new ArrayList<Hand>();
		this.deck = new BigTwoDeck();
		CardGamePlayer p0 = new CardGamePlayer();
		CardGamePlayer p1 = new CardGamePlayer();
		CardGamePlayer p2 = new CardGamePlayer();
		CardGamePlayer p3 = new CardGamePlayer();
		this.playerList.add(p0);
		this.playerList.add(p1);
		this.playerList.add(p2);
		this.playerList.add(p3);
		this.numOfPlayers = 4;
		
		this.ui = new BigTwoGUI(this);
		gameClient = new BigTwoClient(this, this.ui);
	}
	
	/**
	 * Get the GUI of the game.
	 * 
	 * @return GUI of the game
	 */
	public BigTwoGUI getGUI() {
		return this.ui;
	}
	
	/**
	 * Get the Client of the player
	 * 
	 * @return client of the player
	 */
	public BigTwoClient getClient() {
		return this.gameClient;
	}
	
	/**
	 * Get the number of players in the game.
	 * 
	 * @return the number of players in the game
	 */
	public int getNumOfPlayers(){
		return this.numOfPlayers;
	}
	
	/**
	 * Get the deck of cards used in the game.
	 * 
	 * @return the deck of cards
	 */
	public Deck getDeck(){
		return deck;
	}
	
	/**
	 * Get the list of players in the game currently.
	 * 
	 * @return the list of players
	 */
	public ArrayList<CardGamePlayer> getPlayerList(){
		return this.playerList;
	}
	
	/**
	 * Get the hands currently on the table.
	 * 
	 * @return the hands currently on the table
	 */
	public ArrayList<Hand> getHandsOnTable(){
		return this.handsOnTable;
	}
	
	/**
	 * Get the index of the current player.
	 * 
	 * @return the index of the current player
	 */
	public int getCurrentPlayerIdx(){
		return this.currentPlayerIdx;
	}
	
	/**
	 * Set the index of the current player.
	 * 
	 * @param playerIdx index of the current player
	 */
	public void setCurrentPlayerIdx(int playerIdx) {
		this.currentPlayerIdx = playerIdx;
	}
	
	/**
	 * Set the hands on table
	 * 
	 * @param handsOnTable hands on table
	 */
	public void setHandsOnTable(ArrayList<Hand> handsOnTable) {
		this.handsOnTable = handsOnTable;
	}
	
	
	/**
	 * Get the client of the player
	 * 
	 * @return client of the player
	 */
	public int getClientIdx() {
		if (gameClient == null) {
			return -1;
		}
		else {
			return gameClient.getPlayerID();
		}
	}
	
	/**
	 * Start the game with the provided deck of cards. Randomly distribute the deck of cards to players.
	 * 
	 * @param deck the deck to be used for the game
	 */
	public void start(Deck deck){
		// remove all the cards from the players
		for (int i = 0; i < numOfPlayers; i++) {
			playerList.get(i).removeAllCards();
		}
		
		// remove all the cards from the table
		handsOnTable = new ArrayList<Hand>();
		
		// distribute the cards to the players
//		deck.initialize();
//		deck.shuffle();
		int d3_playerIndex = 0;
		for (int i = 0; i < 52; i++) {
			playerList.get(i % 4).addCard(deck.getCard(i));
			if (deck.getCard(i).getSuit() == 0 && deck.getCard(i).getRank() == 2) {
				d3_playerIndex = i % 4;
			}
		}
		
		// sort cards in hand
		for (int i = 0; i < 4; i++) {
			playerList.get(i).sortCardsInHand();
		}
		
		// set both the currentPlayerIdx of the BigTwo object and the activePlayer of the BigTwoUI object
		// to the index of the player who holds the Three of Diamonds
		currentPlayerIdx = d3_playerIndex;
		ui.setActivePlayer(d3_playerIndex);
		ui.printMsg(getPlayerList().get(currentPlayerIdx).getName() + "'s turn: ");
		
		// show the cards on the table
		ui.repaint();
	}
	
	/**
	 * Makes a move for the specified player using the given card indexes.
	 * 
	 * @param playerIdx the index of the player making the move
	 * @param cardIdx the indexes of the cards to be played
	 */
	public void makeMove(int playerIdx, int[] cardIdx) {
//		checkMove(playerIdx, cardIdx);
		CardGameMessage message = new CardGameMessage(CardGameMessage.MOVE, playerIdx, cardIdx);
		gameClient.sendMessage(message);
	}
	
	/**
	 * Checks the validity of the move made by the specified player and make the move.
	 * 
	 * @param playerIdx the index of the player making the move
	 * @param cardIdx the indexes of the cards to be played
	 */
	public void checkMove(int playerIdx, int[] cardIdx) {
		CardGamePlayer player = playerList.get(playerIdx);
		CardList cl_play = player.play(cardIdx);
		Hand currenthand = BigTwo.composeHand(playerList.get(playerIdx), cl_play);
		if (handsOnTable.isEmpty()) {
			Card d3 = new Card(0, 2);
			if (currenthand != null && currenthand.contains(d3)) {
				// add hands on table
				handsOnTable.add(currenthand);
				
				// remove current hand from the player
				playerList.get(playerIdx).removeCards(currenthand);
				
				// print current hand
				String string = "{"+currenthand.getType()+"} ";
				for (int i = 0; i < currenthand.size(); i++) {
					string = string + "[" + currenthand.getCard(i).toString() + "]";
				}
				ui.printMsg(string);
				
				// next player
				if (!endOfGame()) {
					currentPlayerIdx = (currentPlayerIdx + 1) % 4;
					ui.setActivePlayer(currentPlayerIdx);
					ui.promptActivePlayer();
				}
			}
			else {
				ui.printMsg("Not a legal move!!!");
				ui.promptActivePlayer();
			}
		}
		else {
			Hand lastHandOnTable = (handsOnTable.isEmpty()) ? null : handsOnTable.get(handsOnTable.size() - 1);
			if (lastHandOnTable.getPlayer().getName() == playerList.get(currentPlayerIdx).getName()) {
				if (currenthand != null) {
					// add hands on table
					handsOnTable.add(currenthand);
					
					// remove current hand from the player
					playerList.get(playerIdx).removeCards(currenthand);
					
					// print current hand
					String string = "{"+currenthand.getType()+"} ";
					for (int i = 0; i < currenthand.size(); i++) {
						string = string + "[" + currenthand.getCard(i).toString() + "]";
					}
					ui.printMsg(string);
					
					// next player
					if (!endOfGame()) {
						currentPlayerIdx = (currentPlayerIdx + 1) % 4;
						ui.setActivePlayer(currentPlayerIdx);
						ui.promptActivePlayer();
					}
				}
				else {
					ui.printMsg("Not a legal move!!!");
					ui.promptActivePlayer();
				}
			}
			else {
				if (cl_play == null) {
					// print pass message
					ui.printMsg("{Pass}");
					
					// next player
					if (!endOfGame()) {
						currentPlayerIdx = (currentPlayerIdx + 1) % 4;
						ui.setActivePlayer(currentPlayerIdx);
						ui.promptActivePlayer();
					}
				}
				else if (currenthand != null && currenthand.beats(handsOnTable.get(handsOnTable.size()-1))) {
					// add hands on table
					handsOnTable.add(currenthand);
					
					// remove current hand from the player
					playerList.get(playerIdx).removeCards(currenthand);
					
					// print current hand
					String string = "{"+currenthand.getType()+"} ";
					for (int i = 0; i < currenthand.size(); i++) {
						string = string + "[" + currenthand.getCard(i).toString() + "]";
					}
					ui.printMsg(string);
					
					// next player
					if (!endOfGame()) {
						currentPlayerIdx = (currentPlayerIdx + 1) % 4;
						ui.setActivePlayer(currentPlayerIdx);
						ui.promptActivePlayer();
					}
				}
				else {
					ui.printMsg("Not a legal move!!!");
					ui.promptActivePlayer();
				}
			}
		}
		
		// checking for end of game
		if (endOfGame()) {
			ui.setActivePlayer(-1);	
			if (playerList.get((currentPlayerIdx + 2) % 4).getNumOfCards() != 0){
				ui.printMsg("Game ends");
				for (int i = 0; i < 4; i++) {
					if (i != currentPlayerIdx) {
						ui.printMsg(playerList.get(i).getName() + " has " + 
									playerList.get(i).getNumOfCards() + " cards in hand.");
					}
					else {
						ui.printMsg(playerList.get(i).getName() + " wins the game.");
					}
				}
				ui.disable();
				ui.gameEndConfirm();
				CardGameMessage newMsg = new CardGameMessage(CardGameMessage.READY, -1, null);
				gameClient.sendMessage(newMsg);
			}
		}
		
		// disable the other players
		if (this.getClientIdx() != currentPlayerIdx) {
			ui.disable();
		}
		else {
			ui.enable();
		}
		
		ui.repaint();
	}
	
	/**
	 * Checks if the game has ended.
	 * 
	 * @return true if the game has ended, false otherwise
	 */
	public boolean endOfGame() {
		CardGamePlayer player = playerList.get(currentPlayerIdx);
		if (player.getNumOfCards() == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Main method to start the Big Two game.
	 * 
	 * @param args not being used in this application
	 */
	public static void main(String[] args) {
		BigTwo Big2 = new BigTwo();
//		BigTwoDeck B2_deck = new BigTwoDeck();
//		B2_deck.initialize();
//		Big2.start(B2_deck);
	}

	/**
	 * Composes a hand based on the specified player and the cards chosen.
	 * 
	 * @param player the player who is making the move
	 * @param cards the cards chosen
	 * 
	 * @return the composed hand, null if the hand is invalid or the player chose nothing
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		Hand hand;
		// input nothing
		if (cards == null) {
			return null;
		}
		
		// return valid Single
		else if (cards.size() == 1) {
			hand = new Single(player, cards);
			if (hand.isValid()) {
				return (Single)hand;
			}
		}
		
		// return valid Pair
		else if (cards.size() == 2) {
			hand = new Pair(player, cards);
			if (hand.isValid()) {
				return (Pair)hand;
			}
		}
		
		// return valid Triple
		else if (cards.size() == 3) {
			hand = new Triple(player, cards);
			if (hand.isValid()) {
				return (Triple)hand;
			}
		}
		else if (cards.size() == 5) {
			// return valid StrightFlush
			hand = new StraightFlush(player, cards);
			if (hand.isValid()) {
				return (StraightFlush)hand;
			}
			
			// return valid Quad
			hand = new Quad(player, cards);
			if (hand.isValid()) {
				return (Quad)hand;
			}
			
			// return valid FullHouse
			hand = new FullHouse(player, cards);
			if (hand.isValid()) {
				return (FullHouse)hand;
			}
			
			// return valid Flush
			hand = new Flush(player, cards);
			if (hand.isValid()) {
				return (Flush)hand;
			}
			
			// return valid Straight
			hand = new Straight(player, cards);
			if (hand.isValid()) {
				return (Straight)hand;
			}
		}
		return null;
	}	
}
