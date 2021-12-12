/**
 * Models a FullHouse hand in a BigTwo game
 * Subclass of the Hand class
 * @author Yaw Jalik
 */
public class FullHouse extends Hand {
    /**
     * Constructor to create a FullHouse object
     * @param player player holding the hand
     * @param cards a list of cards of the hand
     */
    public FullHouse(CardGamePlayer player, CardList cards) {
        super(player, cards);
    }

    /**
     * Check if size is five and the there is a triple and a pair
     * @return true if size is five and the there is a triple and a pair, false otherwise
     */
    @Override
    public boolean isValid() {
        if (this.size() != 5) {
            return false;
        }

        int count1 = 1;
        int count2 = 0;
        int rank1 = getCard(0).rank;
        int rank2 = -1;

        for (int i = 1; i < 5; i++) {
            int rank = getCard(i).rank;
            if (rank == rank1) {
                count1++;
            }
            else if (rank2 == -1) {
                rank2 = getCard(i).rank;
                count2++;
            }
            else if (rank == rank2) {
                count2++;
            }
        }
        return count1 == 3 && count2 == 2 || count1 == 2 && count2 == 3;
    }

    /**
     * Returns the name "FullHouse"
     * @return "FullHouse"
     */
    @Override
    public String getType() {
        return "FullHouse";
    }

    /**
     * Override topCard definition: get the highest of the triple
     * @return highest of the triple
     */
    @Override
    public Card getTopCard() {
        int count1 = 1;
        int count2 = 0;
        Card card1 = getCard(0);
        Card card2 = null;

        for (int i = 1; i < 5; i++) {
            Card card = getCard(i);
            if (card.rank == card1.rank) {
                count1++;
            }
            else if (card2 == null) {
                card2 = getCard(i);
                count2++;
            }
            else if (card.rank == card2.rank) {
                count2++;
            }
        }

        if (count1 == 3) {
            return card1;
        }
        return card2;
    }

    /**
     * Overriding default beats method
     * If hand is a Quad or StraightFlush, this hand loses
     * Else if hand is a FullHouse, use default beats method
     * Else, it wins
     * @param hand the hand to be checked
     * @return true if this beats the hand, false otherwise
     */
    @Override
    public boolean beats(Hand hand) {
        if (hand.getType().equals("StraightFlush") || hand.getType().equals("Quad")) {
            return false;
        }
        else if (hand.getType().equals("FullHouse")) {
            return super.beats(hand);
        }
        else {
            return true;
        }
    }
}
