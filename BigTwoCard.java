/**
 * Models a card used in BigTwo game
 * A subclass of the Card class
 * Overrides the compareTo method
 * @author Yaw Jalik
 */
public class BigTwoCard extends Card {

    /**
     * Constructor that merely calls its superclass' constructor
     * @param suit integer representation of a card suit
     * @param rank integer representation of a card rank
     */
    public BigTwoCard(int suit, int rank) {
        super(suit, rank);
    }

    /**
     * Compares this card to another card by rank and suit according to BigTwo rules
     * @param card the card to be compared
     * @return 1 if it is larger, -1 if smaller, 0 otherwise (equal)
     */
    @Override
    public int compareTo(Card card) {
        int thisRank = this.rank < 2 ? this.rank + 13 : this.rank;
        int cardRank = card.rank < 2 ? card.rank + 13 : card.rank;

        if (thisRank > cardRank) {
            return 1;
        } else if (thisRank < cardRank) {
            return -1;
        } else if (this.suit > card.suit) {
            return 1;
        } else if (this.suit < card.suit) {
            return -1;
        } else {
            return 0;
        }
    }

}
