
/**
 * The Single class is used to check the validity of the cards, get the top of the cards and check if the cards beat another hand.
 * It extends the Hand class and overrides some functions based on the rule of Single.
 * 
 * @author Liu Yantong
 */
public class Single extends Hand{
	/**
	 * Constructs a Single object with the specified player and cards.
	 * 
	 * @param player the player who may hold the Single
	 * @param cards the list of cards that may make up the Single
	 */
	public Single(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Checks if the hand is a valid Single.
	 * 
	 * @return true if the hand is a valid Single; false otherwise
	 */
	public boolean isValid() {
		if (this.size() == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Get the type of this hand.
     * 
     * @return the type of the hand of cards: Single
	 */
	public String getType() {
		return "Single";
	}
}
