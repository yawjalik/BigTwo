/**
 * An abstract class to model a hand (a group of one or more cards)
 * A subclass of the CardList class
 * @author Yaw Jalik
 */
public abstract class Hand extends CardList{
    private CardGamePlayer player;

    /**
     * Constructor for building a hand by setting the player and distributing cards
     * Cannot be used to create a Hand object
     * @param player the player who holds the hand of cards
     * @param cards a list of cards
     */
    public Hand(CardGamePlayer player, CardList cards) {
        this.player = player;
        for (int i = 0; i < cards.size(); i++) {
            this.addCard(cards.getCard(i));
        }
    }

    /**
     * Returns the player associated with the hand
     * @return player associated with the hand
     */
    public CardGamePlayer getPlayer() {
        return player;
    }

    /**
     * Overriden method for sorting cards based on BigTwo rules
     * Uses selection sort algorithm
     */
    @Override
    public void sort() {
        // Selection sort to sort cards according to BigTwo rules
        for (int i = 0; i < size(); i++) {
            Card min = getCard(i);
            int minIdx = i;
            for (int j = i; j < size(); j++) {
                if (min.compareTo(getCard(j)) > 0) {
                    min = getCard(j);
                    minIdx = j;
                }
            }
            // Swap
            Card temp = getCard(i);
            setCard(i, min);
            setCard(minIdx, temp);
        }
    }

    /**
     * Returns the top card in the hand
     * Overridden in FullHouse and Quad classes
     * @return top card
     */
    public Card getTopCard() {
        // Overridden in FullHouse and Quad
        Card topCard = getCard(0);
        for (int i = 1; i < size(); i++) {
            Card currentCard = getCard(i);

            if (currentCard.compareTo(topCard) > 0) {
                topCard = currentCard;
            }
        }
        return topCard;
    }

    /**
     * Check if this hand beats another hand
     * Compares top cards by default, will be overidden if necessary
     * @param hand the hand to be checked
     * @return true if this beats the hand, false otherwise
     */
    public boolean beats(Hand hand) {
        // Overridden
        this.sort();
        hand.sort();

        return this.getTopCard().compareTo(hand.getTopCard()) > 0;
    }

    /**
     * Abstract method for checking hand validity
     * @return true if hand is valid, false otherwise
     */
    public abstract boolean isValid();

    /**
     * Abstract method for returning the name of the hand
     * @return Name of the hand
     */
    public abstract String getType();
}
