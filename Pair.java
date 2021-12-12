/**
 * Models a Pair hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class Pair extends Hand {
    /**
     * Constructor to create a Pair object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public Pair(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if size is two and the ranks are equal
     * @return true if size is two and the ranks are equal, false otherwise
     */
    @Override
    public boolean isValid() {
        return this.size() == 2 && getCard(0).rank == getCard(1).rank;
    }

    /**
     * Returns the name "Pair"
     * @return "Pair"
     */
    @Override
    public String getType() {
        return "Pair";
    }

}
