/**
 * Models a Single hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class Single extends Hand {
    /**
     * Constructor to create a Single object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public Single(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if valid (if hand size is exactly one)
     * @return true if hand size is exactly one, false otherwise
     */
    @Override
    public boolean isValid() {
        return this.size() == 1;
    }

    /**
     * Returns the name "Single"
     * @return "Single"
     */
    @Override
    public String getType() {
        return "Single";
    }

}
