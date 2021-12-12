/**
 * Models a Flush hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class Flush extends Hand {
    /**
     * Constructor to create a Flush object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public Flush(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if size is five and the suits are equal
     * @return true if size is five and the suits are equal, false otherwise
     */
    @Override
    public boolean isValid() {
        if (size() != 5) {
            return false;
        }

        int suit = getCard(0).suit;
        for (int i = 1; i < 5; i++) {
            if (getCard(i).suit != suit) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the name "Flush"
     * @return "Flush"
     */
    @Override
    public String getType() {
        return "Flush";
    }

    /**
     * Overriding default beats method
     * If hand is a Straight, this hand wins
     * Else if hand is a Flush, use default beats method
     * Else, it loses
     * @param hand the hand to be checked
     * @return true if this beats the hand, false otherwise
     */
    @Override
    public boolean beats(Hand hand) {
        if (hand.size() < 5 || hand.getType().equals("Straight")) {
            return true;
        }
        else if (hand.getType().equals("Flush")) {
            return super.beats(hand);
        }
        else {
            return false;
        }
    }
}
