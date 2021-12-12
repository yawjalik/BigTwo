/**
 * Models a Triple hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class Triple extends Hand {
    /**
     * Constructor to create a Triple object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public Triple(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if size is three and the ranks are equal
     * @return true if size is three and the ranks are equal, false otherwise
     */
    @Override
    public boolean isValid() {
        if (this.size() != 3) {
            return false;
        }

        int rank = getCard(0).rank;
        for (int i = 1; i < 3; i++) {
            if (getCard(i).rank != rank) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the name "Triple"
     * @return "Triple"
     */
    @Override
    public String getType() {
        return "Triple";
    }

}
