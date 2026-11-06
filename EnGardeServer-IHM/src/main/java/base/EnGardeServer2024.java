/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package base;

import engarde.gui.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import engarde.game.EngardeGame;
import engarde.game.EngardePlayer;
import engarde.gui.EnGardeGUI;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import logger.CSVLogger;

/**
 *
 * @author ktajima
 */
public class EnGardeServer2024 {

    public static final String PROGRAM_TITLE = "En Garde Server 2024";
    public static final String PROGRAM_VERSION = "2024.04.11 beta 0.10";

    public static ServerSocket getWatingPort() {
        int baseumber = 12052;
        ServerSocket soc = null;
        for (int i = 0; i < 100; i++) {
            try {
                soc = new ServerSocket(baseumber);
                return soc;
            } catch (IOException e) {
            }
            baseumber++;
        }
        return soc;
    }

    //表示関係
    private SimpleCUIFrame cuiframe;
    private EnGardeGUI gui;

    //通信関係
    private int waitingPort;
    private boolean waiting = false;
    private ServerSocket serverSocket;
    private serverThread[] clients;
    private int connectedCount;

    //ゲームの制御関係
    protected EngardeGame game;
    private static File logDir;

    public EnGardeServer2024() {
        this.cuiframe = new SimpleCUIFrame(this);
        this.cuiframe.setTitle(PROGRAM_TITLE + PROGRAM_VERSION);
        this.cuiframe.setVersion(PROGRAM_VERSION);
        this.cuiframe.setVisible(true);
        long seed = Math.abs(new Random(new Date().getTime()).nextInt());
        this.cuiframe.setSeedText(seed);

        this.gui = new EnGardeGUI();
        gui.setVisible(true);

    }
    
    /** NOT DEBUGED */
    public void resetServer(){
        try {
            this.serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(EnGardeServer2024.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.cuiframe.setVisible(false);
        this.cuiframe.dispose();
        this.gui.setVisible(false);
        this.gui.dispose();
        
                this.cuiframe = new SimpleCUIFrame(this);
        this.cuiframe.setTitle(PROGRAM_TITLE + PROGRAM_VERSION);
        this.cuiframe.setVersion(PROGRAM_VERSION);
        this.cuiframe.setVisible(true);
        long seed = Math.abs(new Random(new Date().getTime()).nextInt());
        this.cuiframe.setSeedText(seed);

        this.gui = new EnGardeGUI();
        gui.setVisible(true);
        
    }

    public void initWithSeed(long seed) {
        this.initServer();

        //待ち受け開始
        while (this.waiting) {
            try {
                Socket csoc = this.serverSocket.accept();
                this.printMessage(csoc.getInetAddress() + "が接続しました。");
                if (connectedCount < clients.length) {
                    this.clients[connectedCount] = new serverThread(connectedCount, this, csoc);
                    this.clients[connectedCount].start();
                    connectedCount++;
                    if (connectedCount == 2) {
                        //ゲームの準備が完了
                        this.gameInit(seed, null);
                    }
                    this.clients[connectedCount - 1].sendInitMessage();
                } else {
                    //接続上限
                    this.sendConnectionErrorMessage(csoc);
                }

                //子スレッドスタート
            } catch (IOException ex) {
                this.waiting = false;
            }
        }
    }

    /**
     * ２クライアントがそろったらゲームの初期化を行う
     */
    private void gameInit(long seed, File ld) {
        try {
            logDir = ld;
            if (logDir == null) {
                logDir = new File("." + File.separator + "log");
                logDir.mkdirs();
            }
            SimpleDateFormat dformat = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = dformat.format(new Date()) + ".csv";
            File logFile = new File(logDir.getPath() + File.separator + filename);
            CSVLogger.setOutputFile(logFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EnGardeServer2024.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.printMessage("This game seed is :" + seed);
        this.game = new EngardeGame(seed);
        this.game.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                EnGardeServer2024.this.propertyChanged(evt);
            }
        });

    }

    public void propertyChanged(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("GAME_STATE_CHANGED")) {

            int state = (int) evt.getNewValue();
            if (state == EngardeGame.GAMESTATUS_INIT) {
            } else if (state == EngardeGame.GAMESTATUS_INGAME_PLAYER0_PLAY) {
                this.showGameState(50);
                this.clients[0].sendBoardInfo();
                this.clients[1].sendBoardInfo();
                this.resendRequest(0);

            } else if (state == EngardeGame.GAMESTATUS_INGAME_PLAYER1_REACT) {
            } else if (state == EngardeGame.GAMESTATUS_INGAME_PLAYER1_PLAY) {
                this.showGameState(50);
                this.clients[0].sendBoardInfo();
                this.clients[1].sendBoardInfo();
                this.resendRequest(1);

            } else if (state == EngardeGame.GAMESTATUS_INGAME_PLAYER0_REACT) {
            } else if (state == EngardeGame.GAMESTATUS_CALC_SCORE) {
                this.showGameState(50);
            } else if (state == EngardeGame.GAMESTATUS_ROUND_END) {
                this.showGameState(50);//
                int[] info = this.game.getWinner();
                String message = "This round ends. winner is player" + info[2] + ". Score is" + info[0] + "vs" + info[1];
                this.clients[0].sendRoundEndMessage(info[2], info[0], info[1], message);
                this.clients[1].sendRoundEndMessage(info[2], info[0], info[1], message);
            } else if (state == EngardeGame.GAMESTATUS_GAMEEND) {
                this.showGameState(2000);
                this.clients[0].sendBoardInfo();
                this.clients[1].sendBoardInfo();
                //ゲームの終わり　→　確認後強制終了
                int[] info = this.game.getWinner();
                String message = "The game end. winner is player" + info[2] + ". Score is" + info[0] + "vs" + info[1];
                this.clients[0].sendGameEndMessage(info[2], info[0], info[1], message);
                this.clients[1].sendGameEndMessage(info[2], info[0], info[1], message);
                JOptionPane.showMessageDialog(null, message, "Game END", JOptionPane.INFORMATION_MESSAGE);
                //System.exit(0);
            }
        }
    }

    private synchronized void showGameState(int delay) {
        EngardeGame.printGameState(this.game, System.out);
        //GUI infomation
        int cp = this.game.getCurrentPlayer();
        int p0s = this.game.getPlayerScores()[0];
        int p1s = this.game.getPlayerScores()[1];
        int p0p = this.game.getTrackInformation()[EngardeGame.TRACK_INFORMATION_PLACE_OF_PLAYER0];
        int p1p = this.game.getTrackInformation()[EngardeGame.TRACK_INFORMATION_PLACE_OF_PLAYER1];
        int dc = this.game.getDeckCards().size();
        ArrayList<Integer> hand0 = this.game.getHandofPlayer(0);
        ArrayList<Integer> hand1 = this.game.getHandofPlayer(1);
        //
        gui.setDrawData(cp, p0s, p1s, p0p, p1p, dc, hand0, hand1);
        /*try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {
            Logger.getLogger(EnGardeServer2024.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    //** サーバー満員時にエラーを返す */
    private void sendConnectionErrorMessage(Socket soc) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(soc.getOutputStream()));

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("Type", "ConnectionError");
        data.put("From", "Server");
        data.put("To", "Client");
        data.put("MessageID", "400");
        data.put("Message", "Server is full.");

        StringBuilder response = new StringBuilder();
        //response.append("<json>");
        ObjectMapper mapper = new ObjectMapper();
        response.append(mapper.writeValueAsString(data));
        //response.append("</json>");

        writer.println(response.toString());
        writer.flush();
        writer.close();
        soc.close();
    }

    private void initServer() {
        this.printMessage("Server 起動完了 " + EnGardeServer2024.PROGRAM_VERSION);
        this.clients = new serverThread[2];
        this.connectedCount = 0;
        this.serverSocket = EnGardeServer2024.getWatingPort();
        if (this.serverSocket == null) {
            this.printMessage("待ち受けが開始できません。");
            return;
        }
        this.waitingPort = this.serverSocket.getLocalPort();
        this.printMessage(this.waitingPort + "で待ち受け開始しました。");
        this.waiting = true;

    }

    public static void main(String[] args) {
        //Nomal Mode
        Constants.setFiles();
        //
        //Debug for ktajima
//        File dataDir = new File("C:\\Users\\ktajima\\OneDrive - 独立行政法人 国立高等専門学校機構\\NetBeansProjects\\novaluna\\img");
//        Constants.setFiles(dataDir);
        //
        EnGardeServer2024 server = new EnGardeServer2024();
    }

    public void receiveMessageFromClient(int playerID, String smessage) {
        try {
            //JSON -> HashMAP
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, String> de_map = mapper.readValue(smessage, HashMap.class);
            this.receiveMessage(playerID, de_map);
        } catch (JsonProcessingException ex) {
        }
    }

    /**
     * サーバのメッセージハンドリングメソッド
     */
    public void receiveMessage(int playerID, HashMap<String, String> data) {
        //タイプがない場合は捨てる
        if (!data.containsKey("Type")) {
            return;
        }
        //適切なクライアントからのメッセージでない場合は捨てる（本来はここに認証キーを組み込む）
        if (!data.get("From").equals("Client")) {
            return;
        }
        if (!data.get("To").equals("Server")) {
            return;
        }

        //DEBUG
        this.printMessage("[Recv" + playerID + "]" + data.toString());
        //DEBUG

        HashMap<String, String> response = new HashMap<>();
        response.put("From", "Server");
        response.put("To", "Client");

        //以後メッセージ毎の処理
        if (data.get("Type").equals("PlayerName")) {
            //コネクション初期化メッセージ → 名前を返して登録してもらう
            String ClientName = data.get("Name");
            this.clients[playerID].setPlayerName(ClientName);
            response.put("Type", "NameReceived");
            this.clients[playerID].sendMessage(response);

            //もう一方のプレイヤー名が確定していたら両方準備完了とする。（配ったクライアントでは理論的には0->1だが、プロトコル的に逆の場合もある）
            if (this.game != null) {
                int other = (playerID + 1) % 2;
                if (this.clients[other] != null) {
                    if (this.clients[other].getPlayerName() != null) {
                        this.game.PlayerConnected(this.clients[0].getPlayerName());
                        this.game.PlayerConnected(this.clients[1].getPlayerName());
                    }
                }
            }

        } else if (data.get("Type").equals("Evaluation")) {
            if ((playerID == 0 && this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER0_PLAY))
                    || (playerID == 1 && this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER1_PLAY))) {
                
                HashMap<String,Double> evaldata = new HashMap<>();
                for(int i=1;i<=5;i++){
                    String keyF = EngardePlayer.MakeEvalationKey(i,EngardeGame.SWORDMAN_MOVE_FORWARD);
                    String keyB = EngardePlayer.MakeEvalationKey(i,EngardeGame.SWORDMAN_MOVE_BACKWARD);
                    if (data.containsKey(keyF)) {
                        evaldata.put(keyF,Double.parseDouble(data.get(keyF)));
                    }
                    if (data.containsKey(keyB)) {
                        evaldata.put(keyB,Double.parseDouble(data.get(keyB)));
                    }
                }
                this.game.setEvaluationValues(playerID,evaldata);
                response.put("Type", "Accept");
                response.put("MessageID", "200");
                this.clients[playerID].sendMessage(response);
            } else {
                response.put("Type", "Accept");
                response.put("MessageID", "500");
                this.clients[playerID].sendMessage(response);
            }
        } else if (data.get("Type").equals("Play")) {
            //手を打つコマンド：サブIDで分けてある
            int messageID = 0;
            if (data.containsKey("MessageID")) {
                messageID = Integer.parseInt(data.get("MessageID"));
            }
            switch (messageID) {
                case 101:
                    //サーバがPLAYメッセージを受け取れない場合はエラー処理に回す
                    if ((playerID == 0 && this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER0_EVALUATED))
                            || (playerID == 1 && this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER1_EVALUATED))) {
                        //正しい状態
                        if (data.get("PlayCard") == null) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        if (data.get("Direction") == null) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        //カードの数値
                        int value_of_card = Integer.parseInt(data.get("PlayCard")); // 1-5
                        if (value_of_card < 1) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        } else if (value_of_card > 5) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        if (!this.game.checkPlayableCards(playerID, value_of_card, 1)) {
                            //そんなカード持ってないぞエラー
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        //方向
                        String direction = data.get("Direction"); // F or B
                        int direction_number = -1;
                        if (direction.equals("F")) {
                            direction_number = EngardeGame.SWORDMAN_MOVE_FORWARD;
                        } else if (direction.equals("B")) {
                            direction_number = EngardeGame.SWORDMAN_MOVE_BACKWARD;
                        } else {
                            //方向の指定エラー
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        if (!this.game.checkMoveableSwordman(playerID, value_of_card, direction_number)) {
                            //移動不可能エラー
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        //ここまでくれば移動可能であることがわかる
                        //仕様上先にメッセージを転送してから、駒を動かす
                        response.put("Type", "Played");
                        response.put("MessageID", "101");
                        response.put("PlayCard", Integer.toString(value_of_card));
                        response.put("Direction", direction);
                        this.clients[(playerID + 1) % 2].sendMessage(response);
                        //駒を動かす→イベントが発火してDoplayも相手に届く
                        this.game.moveSwordmanWithValue(playerID, value_of_card, direction_number);

                    } else {
                        //不正なタイミングでメッセージを受け取っている。
                        this.sendStateErrorMessage(playerID);
                    }
                    break;
                case 102:
                    //サーバがPLAYメッセージを受け取れない場合はエラー処理に回す
                    if ((playerID == 0 && this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER0_EVALUATED))
                            || (playerID == 1 && this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER1_EVALUATED))) {
                        //正しい状態
                        if (data.get("PlayCard") == null) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        if (data.get("NumOfCard") == null) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        //カードの数値
                        int value_of_card = Integer.parseInt(data.get("PlayCard")); // 1-5
                        //かーどの枚数
                        int num_of_card = Integer.parseInt(data.get("NumOfCard")); // 1-5
                        if (value_of_card < 1) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        } else if (value_of_card > 5) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        if (num_of_card < 1) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        } else if (num_of_card > 5) {
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        //攻撃可能かカードを持っているかのチェック
                        if (!this.game.checkPlayableCards(playerID, value_of_card, num_of_card)) {
                            //そんなカード持ってないぞエラー
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        //攻撃可能かのチェック
                        if (!this.game.checkAttackableToOpponent(playerID, value_of_card, num_of_card)) {
                            //移動不可能エラー
                            this.sendMessageErrorMessage(playerID);
                            return;
                        }
                        //メッセージの転送
                        response.put("Type", "Played");
                        response.put("MessageID", "102");
                        response.put("PlayCard", Integer.toString(value_of_card));
                        response.put("NumOfCard", Integer.toString(num_of_card));
                        this.clients[(playerID + 1) % 2].sendMessage(response);
                        //攻撃実行
                        int res = this.game.attacktoOppnent(playerID, value_of_card, num_of_card);
                        if (res == 0) {
                            //ラウンド終了
                        }

                    } else {
                        //不正なタイミングでメッセージを受け取っている。
                        this.sendStateErrorMessage(playerID);
                    }
                    break;
                case 103:
                    //基本ルールでは自動で受け流す（後退による回避の選択の余地がない）のでこのメッセージは処理しない
                    this.sendMessageErrorMessage(playerID);
                default:
                    //それ以外はエラー
                    this.sendMessageErrorMessage(playerID);
            }
        } else if (data.get("Type").equals("")) {
        } else if (data.get("Type").equals("")) {
        } else if (data.get("Type").equals("")) {

        }

    }

    public void sendMessageErrorMessage(int playerID) {
        this.clients[playerID].sendErrorMessage(404, "Message fromat error.");
        resendRequest(playerID);
    }

    public void sendStateErrorMessage(int playerID) {
        this.clients[playerID].sendErrorMessage(403, "Do NOT allow this command now.");
        resendRequest(playerID);
    }

    private void resendRequest(int playerID) {
        if (playerID == 0) {
            if (this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER0_PLAY)) {
                this.clients[playerID].sendHandInfo(playerID);
                this.clients[playerID].sendDoPlayMessage(101, "Plase select your card.");
            } else if (this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER0_REACT)) {
                this.clients[playerID].sendHandInfo(playerID);
                this.clients[playerID].sendDoPlayMessage(102, "Plase select your card.");
            }
        } else if (playerID == 1) {
            if (this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER1_PLAY)) {
                this.clients[playerID].sendHandInfo(playerID);
                this.clients[playerID].sendDoPlayMessage(101, "Plase select your card.");
            } else if (this.game.isState(EngardeGame.GAMESTATUS_INGAME_PLAYER1_REACT)) {
                this.clients[playerID].sendHandInfo(playerID);
                this.clients[playerID].sendDoPlayMessage(102, "Plase select your card.");
            }
        }
    }

    public void printMessage(String message) {
        this.cuiframe.addMessage(message);
        System.out.println(message);
    }

}

class serverThread extends Thread {

    private int playerID;
    private String PlayerName;
    private EnGardeServer2024 parent;
    private BufferedReader clientReader;
    private PrintWriter clientWriter;
    private StringBuilder sbuf;

    public static Pattern jsonStartEnd = Pattern.compile(".*?\\{(.*)\\}.*?");
    public static Pattern jsonEnd = Pattern.compile("(.*)\\}.*?");
    public static Pattern jsonStart = Pattern.compile(".*?\\{(.*)");

    public serverThread(int pid, EnGardeServer2024 p, Socket csoc) {
        this.playerID = pid;
        this.parent = p;
        this.sbuf = new StringBuilder();
        try {
            this.clientReader = new BufferedReader(new InputStreamReader(csoc.getInputStream()));
            this.clientWriter = new PrintWriter(new OutputStreamWriter(csoc.getOutputStream()));
        } catch (IOException ex) {

        }

    }

    public void setPlayerName(String name) {
        this.PlayerName = name;
    }

    public String getPlayerName() {
        return this.PlayerName;
    }

    public void sendBoardInfo() {
        HashMap<String, String> response = new HashMap<>();
        response.put("From", "Server");
        response.put("To", "Client");
        response.put("Type", "BoardInfo");

        int[] info = this.parent.game.getTrackInformation();
        response.put("PlayerPosition_0", Integer.toString(info[EngardeGame.TRACK_INFORMATION_PLACE_OF_PLAYER0]));
        response.put("PlayerPosition_1", Integer.toString(info[EngardeGame.TRACK_INFORMATION_PLACE_OF_PLAYER1]));
        int[] scores = this.parent.game.getPlayerScores();
        response.put("PlayerScore_0", Integer.toString(scores[0]));
        response.put("PlayerScore_1", Integer.toString(scores[1]));
        int deck = this.parent.game.getDeckCards().size();
        response.put("NumofDeck", Integer.toString(deck));

        if (this.parent.game.getGameStatus() == EngardeGame.GAMESTATUS_INGAME_PLAYER0_PLAY
                || this.parent.game.getGameStatus() == EngardeGame.GAMESTATUS_INGAME_PLAYER0_REACT) {
            response.put("CurrentPlayer", "0");
        } else if (this.parent.game.getGameStatus() == EngardeGame.GAMESTATUS_INGAME_PLAYER1_PLAY
                || this.parent.game.getGameStatus() == EngardeGame.GAMESTATUS_INGAME_PLAYER1_REACT) {
            response.put("CurrentPlayer", "1");
        }

        this.sendMessage(response);
    }

    public void sendHandInfo(int playerID) {
        HashMap<String, String> response = new HashMap<>();
        response.put("From", "Server");
        response.put("To", "Client");
        response.put("Type", "HandInfo");
        ArrayList<Integer> hands = this.parent.game.getHandofPlayer(playerID);
        for (int i = 0; i < hands.size(); i++) {
            response.put("Hand" + Integer.toString(i + 1), Integer.toString(hands.get(i)));
        }
        this.sendMessage(response);

    }

    public void sendDoPlayMessage(int id, String message) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("To", "Client");
        data.put("From", "Server");
        data.put("Type", "DoPlay");
        data.put("MessageID", Integer.toString(id));
        data.put("Message", message);
        this.sendMessage(data);
    }

    public void sendPlayedMessage(int id, Object message) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("To", "Client");
        data.put("From", "Server");
        data.put("Type", "Played");
        data.put("MessageID", Integer.toString(id));
        if (id == 101) {
            int value = (int) message;
            data.put("SelectedPlace", Integer.toString(value));
        } else if (id == 102) {
            Point place = (Point) message;
            data.put("SelectedPlace_X", Integer.toString(place.x));
            data.put("SelectedPlace_Y", Integer.toString(place.y));
        } else if (id == 103) {
            data.put("Refilled", (String) message);
        }
        this.sendMessage(data);
    }

    public void sendErrorMessage(int id, String message) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("To", "Client");
        data.put("From", "Server");
        data.put("Type", "Error");
        data.put("MessageID", Integer.toString(id));
        data.put("Message", message);
        this.sendMessage(data);
    }

    public void sendInitMessage() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("Type", "ConnectionStart");
        data.put("To", "Client");
        data.put("From", "Server");
        data.put("ClientID", Integer.toString(playerID));
        this.sendMessage(data);
    }

    public void sendRoundEndMessage(int winner, int score0, int socre1, String message) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("To", "Client");
        data.put("From", "Server");
        data.put("Type", "RoundEnd");
        data.put("RWinner", Integer.toString(winner));
        data.put("Score0", Integer.toString(score0));
        data.put("Score1", Integer.toString(socre1));
        data.put("Message", message);
        this.sendMessage(data);
    }

    public void sendGameEndMessage(int winner, int score0, int socre1, String message) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("To", "Client");
        data.put("From", "Server");
        data.put("Type", "GameEnd");
        data.put("Winner", Integer.toString(winner));
        data.put("Score0", Integer.toString(score0));
        data.put("Score1", Integer.toString(socre1));
        data.put("Message", message);
        this.sendMessage(data);
    }

    public void sendMessage(String message) {
        this.clientWriter.println(message);
        this.clientWriter.flush();
    }

    public void sendMessage(HashMap<String, String> data) {
        try {
            StringBuilder response = new StringBuilder();

            //response.append("<json>");
            ObjectMapper mapper = new ObjectMapper();
            response.append(mapper.writeValueAsString(data));
            //response.append("</json>");

            this.sendMessage(response.toString());

            //DEBUG
            parent.printMessage("[Sent" + playerID + "]" + data.toString());
            //DEBUG

        } catch (JsonProcessingException ex) {
            Logger.getLogger(serverThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = this.clientReader.readLine()) != null) {
                Matcher startend = jsonStartEnd.matcher(line);
                Matcher end = jsonEnd.matcher(line);
                Matcher start = jsonStart.matcher(line);
                if (startend.matches()) {
                    sbuf = new StringBuilder();
                    sbuf.append("{");
                    sbuf.append(startend.group(1));
                    sbuf.append("}");
                    this.parent.receiveMessageFromClient(playerID, sbuf.toString());
                } else if (end.matches()) {
                    sbuf.append(end.group(1));
                    sbuf.append("}");
                    this.parent.receiveMessageFromClient(playerID, sbuf.toString());
                } else if (start.matches()) {
                    sbuf = new StringBuilder();
                    sbuf.append("{");
                    sbuf.append(start.group(1));
                } else {
                    sbuf.append(line);
                }
            }
        } catch (IOException ex) {
        }
    }

}
