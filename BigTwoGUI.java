import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Graphical user interface (GUI) for a BigTwo game
 *
 * @author Yaw Jalik
 */
public class BigTwoGUI implements CardGameUI{
    private BigTwo game;
    private boolean[] selected;
    private int activePlayer;
    private final JFrame frame;
    private final JPanel bigTwoPanel;
    private JButton playButton, passButton, submitButton;
    private JScrollPane msgPane, chatPane;
    private JTextArea msgArea, chatArea;
    private JTextField chatInput;
    private final int width = 700, height = 610;
    private final Color
            bgPrimary = new Color(65, 10, 125),
            textLight = new Color(200, 200, 200),
            bgLight = new Color(170, 130, 255, 127),
            bgDark = new Color(40, 40, 40);
    private final Font
            fontSmall = new Font("Sans Serif", Font.BOLD, 14),
            fontMedium = new Font("Sans Serif", Font.BOLD, 16),
            fontLarge = new Font("Sans Serif", Font.BOLD, 32);
    private BigTwoClient client;

    /**
     * Constructor for the GUI
     * Initializes variables, creates UI frame, add widgets and panels to the frame
     * @param game a BigTwo game object that contains game information
     */
    public BigTwoGUI(BigTwo game) {
        // Initialize
        this.game = game;
        frame = new JFrame("BigTwo with Omori and Friends");
        frame.setIconImage(new ImageIcon("icons/icon.jpg").getImage());
        initButtons();
        initText();
        setActivePlayer(game.getCurrentPlayerIdx());

        // Client
        client = new BigTwoClient(game, this);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(bgPrimary);

        JMenu gameMenu = new JMenu("Game Menu");
        gameMenu.setForeground(textLight);
        gameMenu.setFont(fontMedium);
        JMenu messageMenu = new JMenu("Message Menu");
        messageMenu.setForeground(textLight);
        messageMenu.setFont(fontMedium);

        JMenuItem connectItem = new MyMenuItem("Connect");
        connectItem.addActionListener(new ConnectMenuItemListener());
        JMenuItem quitItem = new MyMenuItem("Quit");
        quitItem.addActionListener(new QuitMenuItemListener());
        gameMenu.add(connectItem);
        gameMenu.add(quitItem);

        JMenuItem clearChatItem = new MyMenuItem("Clear Chat");
        clearChatItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearChatArea();
            }
        });
        messageMenu.add(clearChatItem);

        menuBar.add(gameMenu);
        menuBar.add(messageMenu);

        // BigTwoPanel
        bigTwoPanel = new BigTwoPanel();
        bigTwoPanel.setLayout(new GridBagLayout());
        bigTwoPanel.setBorder(BorderFactory.createLineBorder(bgPrimary));
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(""); // Dummy label
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, height, width));

        bigTwoPanel.add(label, c);

        // Text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(msgPane);
        textPanel.add(chatPane);

        // Bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        bottomPanel.setBackground(bgPrimary);
        bottomPanel.add(playButton);
        bottomPanel.add(passButton);
        bottomPanel.add(chatInput);
        bottomPanel.add(submitButton);

        // Display
        frame.add(menuBar, BorderLayout.NORTH);
        frame.add(bigTwoPanel, BorderLayout.CENTER);
        frame.add(textPanel, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        disable();
        disableChat();
    }

    /**
     * Returns the id of the active player
     * @return id of active player
     */
    public int getActivePlayer() {
        return activePlayer;
    }

    /**
     * Sets the active player's id
     * @param activePlayer active player id
     */
    @Override
    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    /**
     * Method to repaint the UI
     * Calls the repaint() function of bigTwoPanel
     */
    @Override
    public void repaint() {
        bigTwoPanel.repaint();
    }

    /**
     * Prints a message on to the message area and sets caret position (autoscroll to the bottom)
     * @param msg message to be printed
     */
    @Override
    public void printMsg(String msg) {
        msgArea.append(msg);
        msgArea.setCaretPosition(msgArea.getDocument().getLength());
    }

    /**
     * Prints a message on to the chat area and sets caret position (autoscroll to the bottom)
     * @param msg message to be printed
     */
    public void printChat(String msg) {
        if (msg != null) {
            chatArea.append(msg);
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
    }

    /**
     * Clears the message area
     */
    @Override
    public void clearMsgArea() {
        msgArea.setText("");
    }

    /**
     * Clears the chat area
     */
    public void clearChatArea() {
        chatArea.setText("");
    }

    /**
     * Method to reset the UI
     * Resets the selected list of cards, clears message and chat area, and enables the UI
     */
    @Override
    public void reset() {
        Arrays.fill(selected, false);
        clearMsgArea();
        clearChatArea();
        enable();
        enableChat();
    }

    /**
     * Method to enable the UI
     * Sets all buttons and the bigTwoPanel to the enabled state
     */
    @Override
    public void enable() {
        playButton.setEnabled(true);
        passButton.setEnabled(true);
        bigTwoPanel.setEnabled(true);
    }

    /**
     * Method to disable the UI
     * Sets all buttons and the bigTwoPanel to the disabled state
     */
    @Override
    public void disable() {
        playButton.setEnabled(false);
        passButton.setEnabled(false);
        bigTwoPanel.setEnabled(false);
    }

    /**
     * Enables the chat input and submit button
     */
    public void enableChat() {
        submitButton.setEnabled(true);
        chatInput.setEnabled(true);
    }

    /**
     * Disables the chat input and submit button
     */
    public void disableChat() {
        submitButton.setEnabled(false);
        chatInput.setEnabled(false);
    }

    /**
     * Method to prompt the active player
     * Prints message on to the screen, indicating the player's turn to make a move
     */
    @Override
    public void promptActivePlayer() {
        printMsg(game.getPlayerList().get(activePlayer).getName() + "'s turn: \n");
        if (activePlayer == game.getCurrentPlayerIdx())
            enable();
        else
            disable();
    }

    /**
     * Returns the game's client
     * @return game client
     */
    public BigTwoClient getClient() {
        return client;
    }

    /**
     * Initialize button widgets
     */
    private void initButtons() {
        playButton = new MyButton("Play");
        passButton = new MyButton("Pass");
        submitButton = new MyButton("Submit");

        // Attach listeners
        playButton.addActionListener(new PlayButtonListener());
        passButton.addActionListener(new PassButtonListener());
        submitButton.addActionListener(new TextFieldListener());
    }

    /**
     * Initializes text widgets (message, chat, inputs)
     */
    private void initText() {
        msgArea = new MyTextArea("", 15, 30);
        chatArea = new MyTextArea("", 15, 30);
        chatInput = new JTextField(20);
        chatInput.setPreferredSize(new Dimension(300, 30));
        chatInput.setMinimumSize(new Dimension(200, 30));
        chatInput.setMargin(new Insets(0, 5, 0, 5));

        chatInput.setBackground(bgDark);
        chatInput.setForeground(textLight);
        chatInput.setCaretColor(textLight);
        chatInput.setFont(fontSmall);
        chatInput.addActionListener(new TextFieldListener());

        // Add scroll pane
        msgPane = new JScrollPane(msgArea);
        msgPane.setMinimumSize(new Dimension(300, 305));
        msgPane.setPreferredSize(new Dimension(400, 305));
        chatPane = new JScrollPane(chatArea);
        chatPane.setMinimumSize(new Dimension(300, 305));
        chatPane.setPreferredSize(new Dimension(400, 305));
    }

    /**
     * Get selected (clicked) cards
     * @return array of the id of selected cards
     */
    private int[] getSelected() {
        ArrayList<Integer> selectedCards = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i]) {
                selectedCards.add(i);
            }
        }
        int[] cardIdx = new int[selectedCards.size()];
        for (int i = 0; i < selectedCards.size(); i++) {
            cardIdx[i] = selectedCards.get(i);
        }
        return cardIdx;
    }

    /**
     * Resets the selected cards by setting the 'selected' array to false
     */
    private void resetSelected() {
        Arrays.fill(selected, false);
    }

    /**
     * Parse the int rank to a char
     * Used to access card image file name
     * @param cardRank card rank
     * @return char equivalent of card rank
     */
    private char parseRank(int cardRank) {
        char imgRank;
        switch (cardRank) {
            case 0:
                imgRank = 'a';
                break;
            case 9:
                imgRank = 't';
                break;
            case 10:
                imgRank = 'j';
                break;
            case 11:
                imgRank = 'q';
                break;
            case 12:
                imgRank = 'k';
                break;
            default:
                imgRank = (char) (cardRank + 1 + '0');
        }
        return imgRank;
    }

    /**
     * Parse the int suit to a char
     * Used to access card image file name
     * @param cardSuit card suit
     * @return char equivalent of card suit
     */
    private char parseSuit(int cardSuit) {
        char imgSuit;
        switch (cardSuit) {
            case 0:
                imgSuit = 'd';
                break;
            case 1:
                imgSuit = 'c';
                break;
            case 2:
                imgSuit = 'h';
                break;
            default:
                imgSuit = 's';
        }
        return imgSuit;
    }

    /**
     * A subclass of JPanel that models the playing area of a BigTwo game
     * Handles mouse clicks to select cards
     * @author Yaw Jalik
     */
    class BigTwoPanel extends JPanel implements MouseListener {
        private final Image backCard = new ImageIcon("cards/b.gif").getImage();

        /**
         * Constructor for the panel
         * Adds mouselistener and initializes the selected array
         */
        public BigTwoPanel() {
            this.addMouseListener(this);
            selected = new boolean[13];
        }

        /**
         * Paints the playing area
         * Displays avatars and their names and their cards
         * @param g graphics
         */
        @Override
        protected void paintComponent(Graphics g) {
            Image background = new ImageIcon("backgrounds/background.png").getImage();
            g.drawImage(background, 0, 0, this);

            g.setColor(Color.WHITE);
            for (int i = height / 5; i <= height; i += height / 5) {
                g.drawLine(0, i, width * 2, i);
            }

            // Draw avatars and names
            ArrayList<CardGamePlayer> players = game.getPlayerList();
            g.setFont(fontMedium);
            for (int i = 0; i < 4; i++) {
                CardGamePlayer player = players.get(i);
                String playerName = player.getName();
                if (i == activePlayer) {
                    g.setColor(bgLight);
                    g.fillRect(0, i*height/5, width * 2, height/5);
                    g.setColor(bgPrimary);
                }

                if (i == game.getCurrentPlayerIdx()) {
                    g.setColor(bgPrimary);
                    g.drawString("You:", 9, 20 + i * height / 5);
                    g.setColor(Color.YELLOW);
                    g.drawString("You:", 10, 18 + i * height / 5);
                    g.setColor(Color.ORANGE);
                }
                else {
                    g.setColor(bgPrimary);
                    g.drawString(playerName + ":", 9, 20 + i * height / 5);
                    g.setColor(Color.ORANGE);
                    g.drawString(playerName+ ":", 10, 18 + i * height / 5);
                }

                Image avatar;
                if (game.getWinner() == i) {
                    avatar = new ImageIcon("avatars/0" + (i+1) + "_win.gif").getImage();
                    g.setFont(fontLarge);
                    g.setColor(Color.BLACK);
                    g.drawString(playerName + " Wins!", width/3-2, i*height/5 + height/10 + 18);
                    g.setColor(Color.YELLOW);
                    g.drawString(playerName + " Wins!", width/3, i*height/5 + height/10 + 16);
                    g.setFont(fontMedium);
                }
                else if (game.getWinner() != -1){
                    avatar = new ImageIcon("avatars/0" + (i+1) + "_lose.gif").getImage();
                }
                else {
                    avatar = new ImageIcon("avatars/0" + (i+1) + ".gif").getImage();
                }

                int avatarHeight = avatar.getHeight(this);

                if (!player.getName().equals(""))
                    g.drawImage(avatar, 0, (i+1) * height/5 - avatarHeight, this);
            }

            // Draw cards
            for (int i = 0; i < 4; i++) {
                CardList cardList = players.get(i).getCardsInHand();
                if (i == game.getCurrentPlayerIdx()) {
                    for (int j = 0; j < cardList.size(); j++) {
                        Card card = cardList.getCard(j);
                        int cardRank = card.getRank();
                        int cardSuit = card.getSuit();
                        char imgRank, imgSuit;

                        // Parse rank and suit
                        imgRank = parseRank(cardRank);
                        imgSuit = parseSuit(cardSuit);

                        Image cardImg = new ImageIcon("cards/" + imgRank + imgSuit + ".gif").getImage();
                        int cardHeight = cardImg.getHeight(this);
                        int x = 130+(j*15);
                        int y = selected[j] ? i*height/5 : (i+1)*height/5-cardHeight;
                        g.drawImage(cardImg, x, y, this);
                    }
                }
                else {
                    for (int j = 0; j < cardList.size(); j++) {
                        int cardHeight = backCard.getHeight(this);
                        int x = 130+(j*15);
                        int y = (i+1)*height/5-cardHeight;
                        g.drawImage(backCard, x, y, this);
                    }
                }
            }

            // Draw the table
            ArrayList<Hand> handsOnTable = game.getHandsOnTable();
            if (handsOnTable != null && handsOnTable.size() > 0) {
                Hand lastHand = handsOnTable.get(handsOnTable.size()-1);
                for (int i = 0; i < lastHand.size(); i++) {
                    Card card = lastHand.getCard(i);
                    Image cardImg = new ImageIcon("cards/" + parseRank(card.getRank()) + parseSuit(card.getSuit()) + ".gif").getImage();
                    g.drawImage(cardImg, 130+(i*15), height-cardImg.getHeight(this), this);
                }
                g.drawString("Played by " + lastHand.getPlayer().getName(), 10, 20 + 4*height/5);
            }

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        /**
         * Listens for mouse release event and selects the clicked cards
         * @param e mouse event
         */
        @Override
        public void mouseReleased(MouseEvent e) {
            if (activePlayer != game.getCurrentPlayerIdx() || !this.isEnabled()) {
                return;
            }

            // Get mouse coordinates
            int selectedX = e.getX();
            int selectedY = e.getY();

            CardList cardList = game.getPlayerList().get(activePlayer).getCardsInHand();
            int size = cardList.size();
            int cardHeight = backCard.getHeight(this);

            // check overlapped cards
            for (int j = 0; j < size-1; j++) {
                if (selected[j]) {
                    if (selectedX >= 130+j*15 && selectedX < 130+(j+1)*15 &&
                            selectedY >= (activePlayer)*height/5 && selectedY < (activePlayer+1)*height/5) {
                        selected[j] = !selected[j];
                    }
                }
                else {
                    if (selectedX >= 130+j*15 && selectedX < 130+(j+1)*15 &&
                            selectedY >= (activePlayer+1)*height/5-cardHeight && selectedY < (activePlayer+1)*height/5) {
                        selected[j] = !selected[j];
                    }
                }
            }

            // check last card
            if (selected[size-1]) {
                if (selectedX >= 130+(size-1)*15 && selectedX < 130+(size-1)*15+backCard.getWidth(this) &&
                        selectedY >= (activePlayer)*height/5 && selectedY < (activePlayer+1)*height/5) {
                    selected[size-1] = !selected[size-1];
                }
            }
            else {
                if (selectedX >= 130+(size-1)*15 && selectedX < 130+(size-1)*15+backCard.getWidth(this) &&
                        selectedY >= (activePlayer+1)*height/5-cardHeight && selectedY < (activePlayer+1)*height/5) {
                    selected[size-1] = !selected[size-1];
                }
            }

            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    /**
     * Listener for the playButton
     * Calls the game's makeMove() function when cards are selected
     * @author Yaw Jalik
     */
    class PlayButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int[] cardIdx = getSelected();
            if (cardIdx.length > 0) {
                game.makeMove(activePlayer, cardIdx);
            }
            resetSelected();
        }
    }

    /**
     * Listener for the passButton
     * Calls the game's makeMove() function with no cards
     * @author Yaw Jalik
     */
    class PassButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            game.makeMove(activePlayer, null);
            resetSelected();
        }
    }

    /**
     *
     * @author Yaw Jalik
     */
    class ConnectMenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            client.connect();
        }
    }

    /**
     * Listener for the quit menu item
     * Exits the game
     * @author Yaw Jalik
     */
    class QuitMenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Listener for the text field (input)
     * Prints message to the chat area, autoscrolls down, and clears chat input
     * @author Yaw Jalik
     */
    class TextFieldListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Objects.equals(chatInput.getText(), "")) {
                try {
                    client.sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, chatInput.getText()));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                chatInput.setText("");
            }
        }
    }

    /**
     * Custom button with styling
     * @author Yaw Jalik
     */
    class MyButton extends JButton {
        /**
         * Constructor
         * @param text text to be set
         */
        public MyButton(String text) {
            super(text);
            this.setFont(fontMedium);
            this.setFocusable(false);
            this.setForeground(Color.white);
            this.setBackground(bgDark);
            this.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        }
    }

    /**
     * Custom text area with styling
     * @author Yaw Jalik
     */
    class MyTextArea extends JTextArea {
        /**
         * Constructor
         * @param text text
         * @param rows number of rows
         * @param cols number of columns
         */
        public MyTextArea(String text, int rows, int cols) {
            super(text, rows, cols);
            this.setEditable(false);
            this.setBackground(bgDark);
            this.setForeground(textLight);
            this.setFont(fontSmall);
        }
    }

    /**
     * Custom menu item with styling
     * @author Yaw Jalik
     */
    class MyMenuItem extends JMenuItem {
        /**
         * Constructor
         * @param text text
         */
        public MyMenuItem(String text) {
            super(text);
            this.setBackground(bgPrimary);
            this.setForeground(textLight);
            this.setFont(fontSmall);
        }
    }

}
