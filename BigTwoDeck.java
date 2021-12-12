/**
 * Models a deck of cards for a BigTwo game
 * A subclass of the Deck class
 * @author Yaw Jalik
 */
public class BigTwoDeck extends Deck {
    /**
     * Initializes the deck by removing all cards and adding all 52 cards into the deck
     */
    @Override
    public void initialize() {
        this.removeAllCards();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
                BigTwoCard card = new BigTwoCard(i, j);
                this.addCard(card);
            }
        }
    }

}
