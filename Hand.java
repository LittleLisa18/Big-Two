import java.util.ArrayList;

/**
 * The Hand class represents a collection of cards held by a player in the Big Two card game.
 * It extends the CardList class and provides specific functions to evaluating and comparing hands.
 * 
 * @author Liu Yantong
 */
abstract class Hand extends CardList {
	private CardGamePlayer player;
	
	/**
	 * Constructs a Hand object with the specified player and cards.
     *
     * @param player the player who may holds this hand
     * @param cards  the list of cards that may make up the hand
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		for (int i = 0; i < cards.size(); i++) {
			Card card = cards.getCard(i);
			if (card != null) {
                this.addCard(card);
            }
		}
	}
	
	/**
	 * Get the player who holds this hand.
     *
     * @return the player who holds this hand
	 */
	public CardGamePlayer getPlayer() {
		return player;
		
	}
	
	/**
	 * Returns the top card of the hand.
     *
     * @return the top card of the hand, or null if the hand is empty
	 */
	public Card getTopCard() {
		if (this.size() > 0) {
			this.sort();
			return this.getCard(this.size()-1);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Determines if this hand beats the specified hand.
     *
     * @param hand the hand to compare against
     * @return true if this hand beats the specified hand; false otherwise
	 */
	public boolean beats(Hand hand) {
		if (hand == null || this == null || !this.isValid() || !hand.isValid() || this.getType() != hand.getType()) {
			return false;
		}
		else {
			Card this_tc = this.getTopCard();
			Card hand_tc = hand.getTopCard();
			int this_tc_rank = this_tc.getRank();
			int hand_tc_rank = hand_tc.getRank();
			if (this_tc_rank == 0 || this_tc_rank == 1) {
				this_tc_rank += 13;
			}
			
			if (hand_tc_rank == 0 || hand_tc_rank == 1) {
				hand_tc_rank += 13;
			}
			
			if (this_tc_rank > hand_tc_rank) {
				return true;
			}
			else if (this_tc_rank < hand_tc_rank) {
				return false;
			}
			else {
				if (this_tc.getSuit() > hand_tc.getSuit()) {
					return true;
				}
				else {
					return false;
				}
			}
		}
	}
	
	/**
	 * Checks if the hand is valid.
     *
     * @return true if the hand is valid; false otherwise
	 */
	public abstract boolean isValid();
	
	/**
	 * Returns the type of this hand.
     *
     * @return a string representing the type of the hand (e.g., "Flush", "Straight")
	 */
	public abstract String getType();
}
