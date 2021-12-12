import javax.swing.*;
import java.util.ArrayList;

/**
 * Used for checking BigTwo logic and starting a BigTwo game
 * Implements the CardGame interface
 * @author Yaw Jalik
 */
public class BigTwo implements CardGame {
    private final int numOfPlayers;
    private Deck deck;
    private ArrayList<CardGamePlayer> playerList;
    private ArrayList<Hand> handsOnTable;
    private int currentPlayerIdx;
    private BigTwoGUI ui;
    private int winner = -1;
    private boolean running = false;
    private BigTwoClient client;

    /**
     * Constructor for BigTwo game:
     * Initialize playerList
     * Creates 4 players and adds them to playerList
     * Initialize handsOnTable
     * Create BigTwoUI object
     */
    public BigTwo() {
        // Create 4 players and add to player list
        numOfPlayers = 4;
        playerList = new ArrayList<>();
        for (int i = 0; i < numOfPlayers; i++) {
            CardGamePlayer player = new CardGamePlayer();
            player.setName("");
            playerList.add(player);
        }

        currentPlayerIdx = -1;

        // Initialize handsOnTable
        handsOnTable = new ArrayList<>();

        // Create BigTwoUI
        ui = new BigTwoGUI(this);

        client = ui.getClient();
    }

    /**
     * Returns number of players
     * @return number of players
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * Returns the deck
     * @return deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * Returns the list of players
     * @return list of players
     */
    public ArrayList<CardGamePlayer> getPlayerList() {
        return playerList;
    }

    /**
     * Returns hands on table
     * @return hands on table
     */
    public ArrayList<Hand> getHandsOnTable() {
        return handsOnTable;
    }

    /**
     * Returns the current player's Id
     * @return current player's Id
     */
    public int getCurrentPlayerIdx() {
        return currentPlayerIdx;
    }

    /**
     * Method for starting the game:
     * Removes all cards from players and table
     * Distribute cards to each player and set the active player (the one with 3 of Diamonds)
     * Prompt active player
     * @param deck a shuffled deck of [BigTwo] cards
     */
    public void start(Deck deck) {
        running = true;
        ui.enable();

        // Remove all cards from all players
        for (int i = 0; i < numOfPlayers; i++) {
            playerList.get(i).removeAllCards();
        }

        // Remove all cards for table
        handsOnTable.clear();

        // Distribute cards and set active player
        for (int i = 0; i < numOfPlayers; i++) {
            for (int j = i * 13; j < (i+1) * 13; j++) {
                Card card = deck.getCard(j);
                playerList.get(i).addCard(card);

                if (card.suit == 0 && card.rank == 2) {
                    ui.setActivePlayer(i);
                }
            }
            // sort hand
            playerList.get(i).sortCardsInHand();
        }

        currentPlayerIdx = client.getPlayerID();

        // Reset winner
        winner = -1;

        ui.promptActivePlayer();
    }

    /**
     * Making a move
     * @param playerIdx player Id
     * @param cardIdx array of card Ids
     */
    public void makeMove(int playerIdx, int[] cardIdx) {
        client.sendMessage(new CardGameMessage(CardGameMessage.MOVE, -1, cardIdx));
    }

    /**
     * Checks if a move madeby a player is valid:
     * Create a CardGamePlayer and CardList object from the input parameters
     * Create a Hand using the composeHand method
     * Check each move carefully
     * @param playerIdx player Id passed in from makeMove
     * @param cardIdx array of card Ids passed in from makeMove
     */
    public void checkMove(int playerIdx, int[] cardIdx) {
        CardGamePlayer player = playerList.get(playerIdx);
        CardList cards = player.play(cardIdx);

        Hand hand = composeHand(player, cards);

        // First move
        if (handsOnTable.isEmpty()) {
            if (hand == null || !hand.contains(new Card(0, 2))) {
                ui.printMsg("Not a legal move!!!\n");
                return;
            }
        }
        // Pass
        else if (cards == null) {
            // Check if last hand was played by the same player
            if (handsOnTable.get(handsOnTable.size()-1).getPlayer() == player) {
                ui.printMsg("Not a legal move!!!\n");
                return;
            }
            else {
                ui.printMsg("{Pass}\n\n");
                ui.setActivePlayer((ui.getActivePlayer() + 1) % 4);
                ui.promptActivePlayer();
            }
            return;
        }
        // Illegal hand
        else if (hand == null) {
            ui.printMsg("Not a legal move!!!\n");
            return;
        }
        // Check if last hand was played by the same player
        else if (handsOnTable.get(handsOnTable.size()-1).getPlayer() == player) {
            // Do nothing
        }
        // Not the same number of cards
        else if (handsOnTable.get(handsOnTable.size()-1).size() != hand.size()) {
            // If not the same player as last hand
            if (handsOnTable.get(handsOnTable.size()-1).getPlayer() != player) {
                ui.printMsg("Not a legal move!!!\n");
                return;
            }
        }
        // If loses
        else if (handsOnTable.get(handsOnTable.size()-1).beats(hand)){
            ui.printMsg("Not a legal move!!!\n");
            return;
        }

        handsOnTable.add(hand);
        player.removeCards(hand);
        ui.setActivePlayer((ui.getActivePlayer() + 1) % 4);

        ui.printMsg("{" + hand.getType() + "}" + " " + hand + "\n\n");
        ui.promptActivePlayer();
    }

    /**
     * Check if game has ended: if one player has no cards left
     * @return true if one player has no cards, false otherwise
     */
    public boolean endOfGame() {
        for (int i = 0; i < numOfPlayers; i++) {
            if (playerList.get(i).getCardsInHand().isEmpty())
                return true;
        }
        return false;
    }

    /**
     * Ends the game
     */
    public void endGame() {
        String winningMessage = "";
        ui.printMsg("Game ends\n");
        for (int i = 0; i < numOfPlayers; i++) {
            CardGamePlayer player = playerList.get(i);
            winningMessage += player.getName();

            // If player still has cards
            if (player.getNumOfCards() > 0) {
                winningMessage += " has " + player.getCardsInHand() + " cards in hand.\n";
            }
            else {
                winner = i;
                winningMessage += " wins the game!\n";
            }
            ui.disable();
        }
        running = false;

        ui.printMsg(winningMessage);

        winningMessage += "Would you like to restart?";
        int restart = JOptionPane.showConfirmDialog(
                null, winningMessage, "Results", JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon("icons/icon-small.jpg"));
        if (restart == JOptionPane.YES_OPTION) {
            winner = -1;
            client.sendMessage(new CardGameMessage(CardGameMessage.READY, currentPlayerIdx, -1));
        }
        else
            System.exit(0);
    }

    /**
     * Return the id of the winner
     * @return id of winner
     */
    public int getWinner(){
        return winner;
    }

    /**
     * Start a BigTwo game:
     * Create a new game
     * Create and shuffle a BigTwoDeck
     * Start the game
     * @param args command line args
     */
    public static void main(String[] args) {
        BigTwo game = new BigTwo();

        BigTwoDeck deck = new BigTwoDeck();
        deck.shuffle();

        // Game loop
        // Added Thread sleep to the while loops because the loops can get too fast and becomes weird
        while (true) {
            try {
                Thread.sleep(100);
            } catch (Exception ex) { ex.printStackTrace(); }
            if (game.running) {

                // While game hasn't ended
                while (!game.endOfGame()) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) { ex.printStackTrace(); }
                    game.ui.repaint();
                }

                // Ending
                game.endGame();
            }
        }
    }

    /**
     * Returns a valid hand from a player and a list of cards
     * Otherwise returns null
     * @param player a player object
     * @param cards a list of cards
     * @return valid hand, null otherwise
     */
    public static Hand composeHand(CardGamePlayer player, CardList cards) {
        if (cards == null)
            return null;

        Hand h;

        if (cards.size() == 5) {
            h = new StraightFlush(player, cards);
            if (h.isValid())
                return h;

            h = new Quad(player, cards);
            if (h.isValid())
                return h;

            h = new FullHouse(player, cards);
            if (h.isValid())
                return h;

            h = new Flush(player, cards);
            if (h.isValid())
                return h;

            h = new Straight(player, cards);
            if (h.isValid())
                return h;
        }
        else if (cards.size() == 3) {
            h = new Triple(player, cards);
            if (h.isValid())
                return h;
        }
        else if (cards.size() == 2) {
            h = new Pair(player, cards);
            if (h.isValid())
                return h;
        }
        else {
            h = new Single(player, cards);
            if (h.isValid())
                return h;
        }

        return null;
    }
}
