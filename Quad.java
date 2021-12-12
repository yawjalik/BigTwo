/**
 * Models a Quad hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class Quad extends Hand {
    /**
     * Constructor to create a Quad object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public Quad(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if size is five and the there are four cards of the same rank
     * @return true if size is five and the there are four cards of the same rank, false otherwise
     */
    @Override
    public boolean isValid() {
        if (this.size() != 5) {
            return false;
        }

        this.sort();

        int rank = getCard(0).rank;

        if (rank != getCard(1).rank) {
            rank = getCard(1).rank;
            for (int i = 2; i < 5; i++) {
                if (getCard(i).rank != rank) {
                    return false;
                }
            }
        }
        else {
            for (int i = 2; i < 4; i++) {
                if (getCard(i).rank != rank) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns the name "Quad"
     * @return "Quad"
     */
    @Override
    public String getType() {
        return "Quad";
    }

    /**
     * Override topCard definition: get the highest of the quad cards
     * @return highest of the quad
     */
    @Override
    public Card getTopCard() {
        Card topCard;
        if (this.getCard(1).rank == this.getCard(2).rank) {
            topCard = this.getCard(1);
        }
        else {
            topCard = this.getCard(0);
        }

        return topCard;
    }

    /**
     * Overriding default beats method
     * If hand is a StraightFlush, this hand loses
     * Else if hand is a Quad, use default beats method
     * Else, it wins
     * @param hand the hand to be checked
     * @return true if this beats the hand, false otherwise
     */
    @Override
    public boolean beats(Hand hand) {
        if (hand.getType().equals("StraightFlush")) {
            return false;
        }
        else if (hand.getType().equals("Quad")) {
            return super.beats(hand);
        }
        else {
            return true;
        }
    }
}
