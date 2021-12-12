import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Networking client for BigTwo
 * Connects to the server and sends & receives messages
 * @author Yaw Jalik
 */
public class BigTwoClient implements NetworkGame{
    private BigTwo game;
    private BigTwoGUI gui;
    private Socket sock;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int playerID;
    private String playerName;
    private String serverIP = "127.0.0.1";
    private int serverPort = 2396;

    /**
     * Constructor for the client
     * @param game BigTwo game
     * @param gui GUI object
     */
    public BigTwoClient(BigTwo game, BigTwoGUI gui) {
        this.game = game;
        this.gui = gui;

        playerName = null;
        do {
            playerName = JOptionPane.showInputDialog(
                    null, "Enter your name (20 characters or less)",
                    "BigTwo with Omori and Friends", JOptionPane.INFORMATION_MESSAGE);
            if (playerName == null)
                System.exit(0);
            playerName = playerName.trim();
        } while (playerName.equals("") || playerName.length() > 20);
    }

    /**
     * Returns the player ID
     * @return playerID
     */
    @Override
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Sets the player ID
     * @param playerID player ID to be set
     */
    @Override
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * Returns the player's name
     * @return playerName
     */
    @Override
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the player's name
     * @param playerName player name to be set
     */
    @Override
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Returns the server's IP
     * @return serverIP
     */
    @Override
    public String getServerIP() {
        return serverIP;
    }

    /**
     * Sets the server's IP
     * @param serverIP server IP to be set
     */
    @Override
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * Returns the server's port
     * @return serverPort
     */
    @Override
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets the server's port
     * @param serverPort server port to be set
     */
    @Override
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Method to connect to the BigTwo server
     * Create object input and output streams and a new message-receiving thread upon connection
     */
    @Override
    public void connect() {
        // If already connected
        if (sock != null)
            return;

        try {
            sock = new Socket(serverIP, serverPort);
            oos = new ObjectOutputStream(sock.getOutputStream());
            ois = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
            Thread receiveThread = new Thread(new ServerHandler());
            receiveThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            gui.printMsg("Connection failed\n");
        }
    }

    /**
     * Method for parsing messages received from the server
     * Behavior depends on message type
     * @param message message to be parsed
     */
    @Override
    public synchronized void parseMessage(GameMessage message) {
        int type = message.getType();
        int id =  message.getPlayerID();
        Object data = message.getData();

        switch (type) {
            case CardGameMessage.PLAYER_LIST:
                playerID = id;
                gui.setActivePlayer(playerID);
                game.getPlayerList().get(playerID).setName(playerName);
                String[] names = (String[]) data;
                for (int i = 0; i < game.getNumOfPlayers(); i++)
                    if (i != id)
                        game.getPlayerList().get(i).setName(names[i] == null ? "" : names[i]);
                sendMessage(new CardGameMessage(CardGameMessage.JOIN, -1, playerName));
                gui.enableChat();
                gui.repaint();
                break;

            case CardGameMessage.JOIN:
                game.getPlayerList().get(id).setName((String) data);
                if (id == playerID)
                    sendMessage(new CardGameMessage(CardGameMessage.READY, playerID, null));
                break;

            case CardGameMessage.FULL:
                gui.printMsg("Failed to join: Server is full!\n");
                sock = null;
                break;

            case CardGameMessage.QUIT:
                CardGamePlayer player = game.getPlayerList().get(id);
                gui.printMsg(player.getName() + " left the game\n");
                player.setName("");
                sendMessage(new CardGameMessage(CardGameMessage.READY, -1, null));
                break;

            case CardGameMessage.READY:
                gui.printMsg(game.getPlayerList().get(id).getName() + " is ready.\n");
                break;

            case CardGameMessage.START:
                game.start((BigTwoDeck) data);
                break;

            case CardGameMessage.MOVE:
                game.checkMove(id, (int[]) data);
                break;

            case CardGameMessage.MSG:
                if (data != null)
                    gui.printChat(data + "\n");
                break;
        }
    }

    /**
     * Method for sending a message to the server
     * @param message message to be sent
     */
    @Override
    public synchronized void sendMessage(GameMessage message) {
        try {
            oos.writeObject(message);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * A class for handling communications with the server
     * Implements runnable interface
     * @author Yaw Jalik
     */
    class ServerHandler implements Runnable {
        @Override
        public void run() {
            try {
                CardGameMessage message = (CardGameMessage) ois.readObject();
                while (message != null) {
                    parseMessage(message);
                    message = (CardGameMessage) ois.readObject();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
