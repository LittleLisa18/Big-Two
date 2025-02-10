
/**
 * The Triple class is used to check the validity of the cards, get the top of the cards and check if the cards beat another hand.
 * It extends the Hand class and overrides some functions based on the rule of Triple.
 * 
 * @author Liu Yantong
 */
public class Triple extends Hand{
	/**
	 * Constructs a Triple object with the specified player and cards.
	 * 
	 * @param player the player who may hold the Triple
	 * @param cards the list of cards that may make up the Triple
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * Checks if the hand is a valid Triple.
	 * 
	 * @return true if the hand is a valid Triple; false otherwise
	 */
	public boolean isValid(){
		if (this.size() != 3) {
			return false;
		}
		else {
			Card card1 = this.getCard(0);
			Card card2 = this.getCard(1);
			Card card3 = this.getCard(2);
			if (card1.getRank() == card2.getRank() && card2.getRank() == card3.getRank()) {
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
     * @return the type of the hand of cards: Triple
	 */
	public String getType(){
		return "Triple";
	}
}
