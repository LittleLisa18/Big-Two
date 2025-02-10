
/**
 * The Pair class is used to check the validity of the cards, get the top of the cards and check if the cards beat another hand.
 * It extends the Hand class and overrides some functions based on the rule of Pair.
 * 
 * @author Liu Yantong
 */
public class Pair extends Hand{
	/**
	 * Constructs a Pair object with the specified player and cards.
	 * 
	 * @param player the player who may hold the Pair
	 * @param cards the list of cards that may make up the Pair
	 */
	public Pair(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Checks if the hand is a valid Pair.
	 * 
	 * @return true if the hand is a valid Pair; false otherwise
	 */
	public boolean isValid(){
		if (this.size() != 2) {
			return false;
		}
		else {
			Card card1 = this.getCard(0);
			Card card2 = this.getCard(1);
			if (card1.getRank() != card2.getRank()) {
				return false;
			}
			else {
				return true;
			}
		}
	}
	
	/**
	 * Get the type of this hand.
     * 
     * @return the type of the hand of cards: Pair
	 */
	public String getType(){
		return "Pair";
	}
}
