/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

package Ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author ktajima
 */
public class Ai {

    private int myPlayerID = -1;
    private boolean attack = false;
    Integer evilCard = -1;
    
    Scanner sc = new Scanner(System.in);
    public static int err = 0;
    
    public static void main(String[] args) {
        Ai ai = new Ai();
        ai.connectToServer();
    }

    private Integer serverPort;
    private String serverAddress;
    private Socket connectedSocket;
    private BufferedReader serverReader;
    private PrintWriter serverWriter;


    private void connectToServer(){
        //this.serverAddress = sc.nextLine();
        //this.serverPort = sc.nextInt();
        

        // Socket版
        try {
            this.connectedSocket = new Socket("localhost",12052);
            this.serverReader = new BufferedReader(new InputStreamReader(connectedSocket.getInputStream()));
            this.serverWriter = new PrintWriter(new OutputStreamWriter(connectedSocket.getOutputStream()));
        
            System.out.println("Connected");
    
            MessageReceiver m = new MessageReceiver(this);
            m.run();
        } catch (IOException ex) {
            System.out.println("サーバに接続できませんでした。");
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

        /** スレッドから読みこんだメッセージを受信するメソッド
     * @param message */
    public void receiveMessageFromServer(String message){
        this.showRecivedMessage(message);
        try {
            //JSON -> HashMAP
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String,String> de_map = mapper.readValue(message, HashMap.class);
            this.receiveDataFromServer(de_map);

        } catch (JsonProcessingException ex) {
        }
    }

    Hand handInfo = new Hand();
    Board board = new Board();
    /** データハンドリングメソッド
     * @param data */
    @SuppressWarnings("unused")
	public void receiveDataFromServer(HashMap<String,String> data){

        if(!data.containsKey("Type")){
            return;
        }
        
        try {
            String type = data.get("Type");
            
            
            switch(type){
                case "ConnectionStart":
                    //自分の番号の確定
                    this.myPlayerID = Integer.parseInt(data.get("ClientID"));
                    //なまえの入力
                    this.sendMyName();
                    break;
                 case "BoardInfo":
                     board = new Board(data);
                     
                    break;
                case "HandInfo":
                    handInfo = new Hand(data); 
                    
                    break;
                case "DoPlay":
                    sendEvaluateMessage();
                    this.actionAi(board, handInfo);
                    break;
                case "Played":
                    System.out.println("CARTE JOUÉE : " + data.get("PlayCard"));
                    this.handInfo.evilRemove(Integer.parseInt(data.get("PlayCard")));
                    if (Integer.parseInt(data.get("PlayCard")) == board.getDistancePlayer()) {
                        this.evilCard = Integer.parseInt(data.get("PlayCard"));
                        this.attack = true;
                    }
                    handInfo.printEvilHand();
                    break;
                default:
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




    // AI ---------------------------------------------------------------------------------------------------------------------------------

    public void actionAi(Board board, Hand hand) throws IOException, InterruptedException{

        System.out.println("--------------------------------------");
        board.printBoard();
        hand.printHand();
        int distance = board.getDistancePlayer();

        if (attack && hand.getNumeberOfCards(distance) > 0) {
            sendAttackMessage(distance, hand.getNumeberOfCards(distance));
            System.out.println("JE ME DEFEND !!!!!!!!!!!!!!!!!!!!!!!!!!");
            attack = false;
            return;
        }

        

        Position initialPosition = new Position(hand, board);
        Node rootNode = new Node(initialPosition, true);

        String action = rootNode.selectBestAction(initialPosition);

        if (action == null) {
            sendAttackMessage(distance, hand.getNumeberOfCards(distance));
            System.out.println("J'ATTAQUE !!!! : " + distance);
            return;
        }
        
        String card = "" + action.charAt(0);
        String direction = "" + action.charAt(1);

        if (rootNode.getPosition().getEvalValue() > 0) {
            sendAttackMessage(distance, hand.getNumeberOfCards(distance));
        }else {
            if (direction.equals("F")) {
                sendForewardMessage(Integer.parseInt(card));
                System.out.println("J'AVANCE !!!! : " + card);
            }
            if (direction.equals("B")) {
                sendBackwardMessage(Integer.parseInt(card));
                System.out.println("JE RECULE  !!!! : " + card);
            }
        }

        String actionMap = "" + card + direction;
        String apprend = QLearning.getStateActionKey(initialPosition, actionMap);
        Map<String, Double> map = new HashMap<>();
        map.put(apprend,initialPosition.getEvalValue());
        rootNode.qLearning.appendQTable("qtable.ser", map);
    }


    
    //---------------------------------------------------------------------------------------------------------------------------------
    









    private void sendAttackMessage(int cardNumber,int cardCount) throws IOException, InterruptedException{

        HashMap<String, String> response = new HashMap<>();
        response.put("From","Client");
        response.put("To","Server");
        response.put("Type","Play");
        response.put("MessageID","102");
        response.put("NumOfCard",Integer.toString(cardCount));
        response.put("PlayCard",Integer.toString(cardNumber));
        
        StringBuilder sbuf = new StringBuilder();
        try {
            ObjectMapper mapper = new ObjectMapper();
            //sbuf.append("<json>");
            sbuf.append(mapper.writeValueAsString(response));
            //sbuf.append("</json>");
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.sendMassageWithSocket(sbuf.toString());
        
    }

    private void sendBackwardMessage(int cardNumber) throws IOException, InterruptedException{

        HashMap<String, String> response = new HashMap<>();
        response.put("From","Client");
        response.put("To","Server");
        response.put("Type","Play");
        response.put("Direction","B");
        response.put("MessageID","101");
        response.put("PlayCard",Integer.toString(cardNumber));
        
        StringBuilder sbuf = new StringBuilder();
        try {
            ObjectMapper mapper = new ObjectMapper();
            sbuf.append(mapper.writeValueAsString(response));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(sbuf.length() > 0){
            this.sendMassageWithSocket(sbuf.toString());
        }
    }

    private void sendForewardMessage(Integer cardNumber) throws IOException, InterruptedException{
        HashMap<String, String> response = new HashMap<>();
        response.put("From","Client");
        response.put("To","Server");
        response.put("Type","Play");
        response.put("Direction","F");
        response.put("MessageID","101");
        response.put("PlayCard",Integer.toString(cardNumber));
        
        StringBuilder sbuf = new StringBuilder();
       try {
            ObjectMapper mapper = new ObjectMapper();
            sbuf.append(mapper.writeValueAsString(response));
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        }if(sbuf.length() > 0){
            this.sendMassageWithSocket(sbuf.toString());
        }
       
   }



    public void action(Hand data) throws IOException, InterruptedException{
        String choix;
        sc = new Scanner(System.in);
        do{
            System.out.println("What you want do ? 1:Forward 2:Backward 3:Attack");
            choix = sc.nextLine();
        }while (Integer.parseInt(choix) < 1 && Integer.parseInt(choix) > 3  );
        

        Integer choiceNumerOfCard;
        String[] cardsValues = data.getHand();
        do{
            
            System.out.println("Choose a card number 0-4");
            choiceNumerOfCard = sc.nextInt();
        
        }while (choiceNumerOfCard < 0 && choiceNumerOfCard > 4 );


        int cardNumber = Integer.parseInt(cardsValues[choiceNumerOfCard]);
        String move = " ";
        String messageID = " ";
        switch(choix){
            case "1":
                move = "F";
                messageID="101";
                break;
            case "2":
                move = "B";
                messageID="101";
                break;
            case "3":
                move = "A";
                messageID="102";
                break;
            default:
                move = "F";
                messageID="101";
                break;
        }

        sendForewardMessage(cardNumber);

    }
    private void sendEvaluateMessage() throws IOException, InterruptedException{
            //JSON構成
            //<json>{"Type":"Evaluation","PlayCard":"4","From":"Client","To":"Server","NumOfCard":"2","MessageID":"102"}</json>
            HashMap<String, String> response = new HashMap<>();
            response.put("From","Client");
            response.put("To","Server");
            response.put("Type","Evaluation");
            response.put("1F","0.1");
            response.put("2F","0.1");
            response.put("3F","0.1");
            response.put("4F","0.1");
            response.put("5F","0.1");
            response.put("1B","0.1");
            response.put("2B","0.1");
            response.put("3B","0.1");
            response.put("4B","0.1");
            response.put("5B","0.1");
            
            StringBuilder sbuf = new StringBuilder();
            try {
                ObjectMapper mapper = new ObjectMapper();
                //sbuf.append("<json>");
                sbuf.append(mapper.writeValueAsString(response));
                //sbuf.append("</json>");
            } catch (JsonProcessingException ex) {
                Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(sbuf.length() > 0){
                this.sendMassageWithSocket(sbuf.toString());
            }
        }

    

    /** GUI上に受信したメッセージを表示するメソッド
     * @param message */
    public void showRecivedMessage(String message){
            System.out.println("Message : " + message);

    }

    /** スレッドに読み込みを行わせる用の取り出しメソッド
     * @return  */
    public BufferedReader getServerReader(){
        return this.serverReader;
    }

    //SEND

    /** HashMapを送るメソッド */
    private void sendMassageWithSocket(HashMap<String,String> data) throws IOException, InterruptedException{
            StringBuilder response = new StringBuilder();
            ObjectMapper mapper = new ObjectMapper();
            //response.append("<json>");
            response.append(mapper.writeValueAsString(data));
            //response.append("</json>");
            this.serverWriter.println(response.toString());
            this.serverWriter.flush();
            //DEBUG
            System.out.println("[Sent]"+data.toString());
            //DEBUG
    }

        /** 指定した文字列を送るメソッド */
    private void sendMassageWithSocket(String message) throws IOException, InterruptedException{
        this.serverWriter.println(message);
        this.serverWriter.flush();
        //DEBUG
        System.out.println("[Sent]"+message);
        //DEBUG
    }



    private void sendMyName() throws IOException, InterruptedException{
        System.out.println("Please your player name");

        String inputValue = "NO-GUI-PLAYER";
        
        
        //JSON構成
        //likes <json>{"Type":"PlayerName","From":"Client","To":"Server","Name":"Simple"}</json>
        HashMap<String, String> response = new HashMap<>();
        response.put("From","Client");
        response.put("To","Server");
        response.put("Type","PlayerName");
        response.put("Name",inputValue);
        StringBuilder sbuf = new StringBuilder();
        try {
            ObjectMapper mapper = new ObjectMapper();
            //sbuf.append("<json>");
            sbuf.append(mapper.writeValueAsString(response));
            //sbuf.append("</json>");
        } catch (JsonProcessingException ex) {
            Logger.getLogger(Ai.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(sbuf.length() > 0){
            this.sendMassageWithSocket(sbuf.toString());
        }
        
    }
    
}



class MessageReceiver extends Thread {
    private Ai parent;
    private BufferedReader serverReader;
    private StringBuilder sbuf;
    
//    public static Pattern jsonStartEnd = Pattern.compile(".*?<json>(.*)</json>.*?");
//    public static Pattern jsonEnd = Pattern.compile("(.*)</json>.*?");
//    public static Pattern jsonStart = Pattern.compile(".*?<json>(.*)");
    public static Pattern jsonStartEnd = Pattern.compile(".*?\\{(.*)\\}.*?");
    public static Pattern jsonEnd = Pattern.compile("(.*)\\}.*?");
    public static Pattern jsonStart = Pattern.compile(".*?\\{(.*)");
    public MessageReceiver(Ai p){
        this.parent = p;
        this.serverReader = this.parent.getServerReader();
        this.sbuf = new StringBuilder();
    }
    
    @Override
    public void run(){
        try {
            String line;
            while((line = this.serverReader.readLine()) != null){
                Matcher startend = jsonStartEnd.matcher(line);
                Matcher end = jsonEnd.matcher(line);
                Matcher start = jsonStart.matcher(line);
                if(startend.matches()){
                    sbuf = new StringBuilder();
                    sbuf.append("{");
                    sbuf.append(startend.group(1));
                    sbuf.append("}{");
                    this.parent.receiveMessageFromServer(sbuf.toString());
                } else if(end.matches()){
                    sbuf.append(end.group(1));
                    sbuf.append("}{");
                    this.parent.receiveMessageFromServer(sbuf.toString());
                } else if(start.matches()){
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

