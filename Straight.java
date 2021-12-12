/**
 * Models a Straight hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class Straight extends Hand {
    /**
     * Constructor to create a Straight object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public Straight(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if size is five and the ranks are consecutive
     * @return true if size is five and the ranks are consecutive, false otherwise
     */
    @Override
    public boolean isValid() {
        if (this.size() != 5) {
            return false;
        }

        // Check for consecutive values
        for (int i = 0; i < 4; i++) {
            int previousRank = getCard(i).rank < 2 ? getCard(i).rank + 13 : getCard(i).rank;
            int currentRank = getCard(i+1).rank < 2 ? getCard(i+1).rank + 13 : getCard(i+1).rank;
            if (previousRank != currentRank - 1) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the name "Straight"
     * @return "Straight"
     */
    @Override
    public String getType() {
        return "Straight";
    }

    /**
     * Overriding default beats method
     * If hand is has five cards and is a Straight, use default beats method
     * Else, it loses (to all other five-card hands)
     * @param hand the hand to be checked
     * @return true if this beats the hand, false otherwise
     */
    @Override
    public boolean beats(Hand hand) {
        if (hand.size() < 5) {
            return true;
        }
        else if (hand.getType().equals("Straight")) {
            return super.beats(hand);
        }
        else {
            return false;
        }
    }
}
