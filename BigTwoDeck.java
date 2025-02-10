/**
 * The BigTwoDeck class represents a deck of cards specifically designed for the Big Two card game. 
 * It extends the Deck class and provides an implementation to initialize the deck with Big Two cards.
 * 
 * @author Liu Yantong
 */
public class BigTwoDeck extends Deck {
	/**
	 * Initializes the deck by removing all existing cards and adding a set of Big Two cards.
     * 
     * <p>This method creates 52 cards, with 4 suits and 13 ranks.</p>
	 */
	@Override
	public void initialize() {
		removeAllCards();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 13; j++) {
				BigTwoCard card = new BigTwoCard(i, j);
				addCard(card);
			}
		}
	}
}
