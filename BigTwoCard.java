/**
 * The BigTwoCard class represents the cards used in the Big Two card game.
 * It extends the Card class and implements a specific comparison logic based on the rules of the Big Two game.
 * 
 * @author Liu Yantong
 */
public class BigTwoCard extends Card{
	/**
	 * Constructs a BigTwoCard with the specified suit and rank.
	 * 
	 * @param suit the suit of the card
	 * @param rank the rank of the card
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	
	/**
	 * Compares this card with the specified card for order based on the rule of BigTwo Game.
	 * <p>The comparison is based on the rules of the Big Two game:
	 * Ranks in ascending order: 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A, 2.
     * If ranks are equal, they are then compared by suit.</p>
     * 
     * @param card the card to be compared
     * 
     * @return 1, 0 or -1 as this card is greater than, equal to, or less than the specified card
	 */
	public int compareTo(Card card) {
		if ((this.rank >= 2 && card.rank >= 2) || (this.rank < 2 && card.rank < 2)) {
			if (this.rank > card.rank) {
				return 1;
			}
			else if (this.rank < card.rank) {
				return -1;
			}
			else if (this.suit > card.suit) {
				return 1;
			}
			else if(this.suit < card.suit){
				return -1;
			}
		}
		else if (this.rank < 2 && card.rank >= 2) {
			return 1;
		}
		else if (this.rank >= 2 && card.rank < 2) {
			return -1;
		}
		return 0;
	}
}
