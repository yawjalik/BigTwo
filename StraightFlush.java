/**
 * Models a StraightFlush hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class StraightFlush extends Hand {
    /**
     * Constructor to create a StraightFlush object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public StraightFlush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if size is five and there are consecutive ranks with the same suit
     * @return true if size is five and there are consecutive ranks with the same suit, false otherwise
     */
    @Override
    public boolean isValid() {
        if (this.size() != 5) {
            return false;
        }

        // Check for consecutive ranks and suits
        int suit = getCard(0).suit;
        for (int i = 0; i < 4; i++) {
            int previousRank = getCard(i).rank < 2 ? getCard(i).rank + 13 : getCard(i).rank;
            int currentRank = getCard(i+1).rank < 2 ? getCard(i+1).rank + 13 : getCard(i+1).rank;
            if (previousRank != currentRank - 1 || suit != getCard(i).suit) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the name "StraightFlush"
     * @return "StraightFlush"
     */
    @Override
    public String getType() {
        return "StraightFlush";
    }

    /**
     * Overriding default beats method
     * If hand is a StraightFlush, use default beats method
     * Else, it wins
     * @param hand the hand to be checked
     * @return true if this beats the hand, false otherwise
     */
    @Override
    public boolean beats(Hand hand) {
        if (hand.getType().equals("StraightFlush")) {
            return super.beats(hand);
        }
        else {
            return true;
        }
    }
}
