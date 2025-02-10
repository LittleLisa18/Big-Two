import java.util.ArrayList;

/**
 * The Straight class is used to check the validity of the cards, get the top of the cards and check if the cards beat another hand.
 * It extends the Hand class and overrides some functions based on the rule of Straight.
 * 
 * @author Liu Yantong
 */
public class Straight extends Hand{
	/**
	 * Constructs a Straight object with the specified player and cards.
	 * 
	 * @param player the player who may hold the Straight
	 * @param cards the list of cards that may make up the Straight
	 */
	public Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Determines if the Straight beats the specified hand.
     *
     * @param hand the hand to compare against
     * @return true if this Straight beats the specified hand; false otherwise
	 */
	@Override
	public boolean beats(Hand hand) {
		if (hand == null || this == null || !this.isValid() || !hand.isValid() || this.size() != hand.size() || hand.size() != 5) {
			return false;
		}
		else {
			if (this.getType() == hand.getType()) {
				Card this_tc = this.getTopCard();
				Card hand_tc = hand.getTopCard();
				int this_tc_rank = this_tc.getRank();
				int hand_tc_rank = hand_tc.getRank();
				// A or 2
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
			else {
				ArrayList<String> Size5 = new ArrayList<String>();
				Size5.add("Straight");
				Size5.add("Flush");
				Size5.add("FullHouse");
				Size5.add("Quad");
				Size5.add("StraightFlush");
				return Size5.indexOf(this.getType()) > Size5.indexOf(hand.getType());	
			}
		}
	}
	
	/**
	 * Checks if the hand is a valid Straight.
	 * 
	 * @return true if the hand is a valid Straight; false otherwise
	 */
	public boolean isValid(){
		if (this.size() != 5) {
			return false;
		}
		else {
			this.sort();
			// after sort, A and 2 will appear later
			Card card1 = this.getCard(0);
			Card card2 = this.getCard(1);
			Card card3 = this.getCard(2);
			Card card4 = this.getCard(3);
			Card card5 = this.getCard(4);
			// Special case
			if (card1.getRank() == 10 && card2.getRank() == 11 && card3.getRank() == 12 && card4.getRank() == 0 && card5.getRank() == 1 || 
				card1.getRank() == 9 && card2.getRank() == 10 && card3.getRank() == 11 && card4.getRank() == 12 && card5.getRank() == 0) {
				return true;
			}
			else if (card1.getRank() == card2.getRank()-1 && card2.getRank() == card3.getRank()-1 && card3.getRank() == card4.getRank()-1 &&
					 card4.getRank() == card5.getRank()-1) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * Get the type of this hand.
     * 
     * @return the type of the hand of cards: Straight
	 */
	public String getType(){
		return "Straight";
	}
}
