/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package engarde.game;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

/**
 *
 * @author ktajima
 */
public class EngardeGame {
    /** フィールドの大きさ */
    public static int defaultTrackSize = 23;
    /** 勝利までのポイント数 */
    public static int defalutVictoryPointSize = 10000;
    public static int totalGame = 400 ;

    /** プレイヤー情報 */
    private EngardePlayer[] player = new EngardePlayer[2];
    private String[] playerNames = new String[2];
    private Stack<Integer> mainDeck;
    private ArrayList<Integer> usedCards;
    private EngardeFensingTrack track;
    private int currentStartPlayer;
    private int currentPlayer;
    private Random rand;
    private int winner = -1;
    
    private int gameStatus;
    
    public static final int GAMESTATUS_INIT                 = 0;
//    public static final int GAMESTATUS_CONNECTING           = 1;
    public static final int GAMESTATUS_PLAYERWAITING_0      = 10;
    public static final int GAMESTATUS_PLAYERWAITING_1      = 11;


    public static final int GAMESTATUS_INGAME_PLAYER0_PLAY  = 20;
    public static final int GAMESTATUS_INGAME_PLAYER1_PLAY  = 21;
    public static final int GAMESTATUS_INGAME_PLAYER0_EVALUATED = 22;
    public static final int GAMESTATUS_INGAME_PLAYER1_EVALUATED = 23;
    public static final int GAMESTATUS_INGAME_PLAYER1_REACT = 24;
    public static final int GAMESTATUS_INGAME_PLAYER0_REACT = 25;
    
    public static final int GAMESTATUS_CALC_SCORE           = 4;
    public static final int GAMESTATUS_ROUND_END            = 41;
    public static final int GAMESTATUS_GAMEEND              = 5;
    public static final int SWORDMAN_MOVE_FORWARD = 0;
    public static final int SWORDMAN_MOVE_BACKWARD = 1;
    public static final int TRACK_INFORMATION_PLACE_OF_PLAYER0 = 0;
    public static final int TRACK_INFORMATION_PLACE_OF_PLAYER1 = 1;
    public static final int TRACK_INFORMATION_SPACE_OF_PLAYERS = 2;

    
    private PropertyChangeListener listner;

    //GUI等への通知用
    private PropertyChangeSupport changes;
     /**
     * リスナの追加を行います.
     * @param l 追加するリスナ
     */
    public void addPropertyChangeListener(PropertyChangeListener l){
        changes.addPropertyChangeListener(l);
    }
    
    /**
     * リスナの削除を行います.
     * @param l 削除するリスナ
     */
    public void removePropertyChangeListener(PropertyChangeListener l){
        changes.removePropertyChangeListener(l);
    }
    
    
    public EngardeGame(){
        Date dt = new Date();
        Random r = new Random(dt.getTime());
        long seed = r.nextLong();
        System.out.println("seed is "+ seed);
        this.init(seed);
    }
    
    public EngardeGame(long seed){
        this.init(seed);
    }
    
    private void init(long seed){
        this.rand = new Random(seed);
        this.changes = new PropertyChangeSupport(this);

        this.mainDeck = new Stack<>();
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                this.mainDeck.add(i+1);
            }
        }
        Collections.shuffle(this.mainDeck, this.rand);
        
        this.usedCards = new ArrayList<>();
        this.gameStatusChange(GAMESTATUS_PLAYERWAITING_0);
        
    }
    
    private void initPlayers(){
        this.player[0] = new EngardePlayer();
        this.player[1] = new EngardePlayer();
        this.drawCards(0);
        this.drawCards(1);
        
        this.track = new EngardeFensingTrack();
        
        this.currentStartPlayer = 0;
        this.currentPlayer = this.currentStartPlayer;
        this.gameStatusChange(GAMESTATUS_INGAME_PLAYER0_PLAY);
        
    }
    
    public boolean isState(int state){
        return this.gameStatus == state;
    }
    
    public boolean PlayerConnected(String playerName){
        if(this.gameStatus == GAMESTATUS_PLAYERWAITING_0){
            this.playerNames[0] = playerName;
            this.player[0] = new EngardePlayer();
            this.gameStatusChange(GAMESTATUS_PLAYERWAITING_1);
            return true;
        } else if(this.gameStatus == GAMESTATUS_PLAYERWAITING_1){
            this.playerNames[1] = playerName;
            this.initPlayers();
            return true;
        }
        return false;
    }
    public int getCurrentPlayer(){
        return this.currentPlayer;
    }    
    public void startNewRound(){
        this.mainDeck = new Stack<>();
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                this.mainDeck.add(i+1);
            }
        }
        Collections.shuffle(this.mainDeck, this.rand);
        
        this.usedCards = new ArrayList<>();
        
        this.player[0].clearCards();
        this.player[1].clearCards();
        
        this.drawCards(0);
        this.drawCards(1);
        
        this.track = new EngardeFensingTrack();
        int[] score_for_view = new int[2];
        score_for_view[0] = this.player[0].playerScore;
        score_for_view[1] = this.player[1].playerScore;
        this.track.setScore(score_for_view);
        
        if(this.currentStartPlayer == 0){
            this.currentStartPlayer = 1;
            this.currentPlayer = this.currentStartPlayer;
            this.gameStatusChange(GAMESTATUS_INGAME_PLAYER1_PLAY);
        } else {
            this.currentStartPlayer = 0;
            this.currentPlayer = this.currentStartPlayer;
            this.gameStatusChange(GAMESTATUS_INGAME_PLAYER0_PLAY);
        }        
        
    }
    
    public boolean mainDeckisEmpty(){
        return this.mainDeck.isEmpty();
    }
    
    /** 手札を５毎に戻す処理 */
    private int drawCards(int playerID){
        int before = this.mainDeck.size();
        
        while(this.player[playerID].getNumberofCards() < 5){
            if(this.mainDeck.isEmpty()){
                return this.mainDeck.size() - before;
            }
            int card = this.mainDeck.pop();
            this.player[playerID].giveACard(card);
        }
        
        return this.mainDeck.size() - before;
    }
    
    
    public ArrayList<Integer> getHandofPlayer(int playerID){
        return this.player[playerID].getCardList();
    }
    
    public ArrayList<Integer> getDeckCards(){
        ArrayList<Integer> list = new ArrayList<>();
        list.addAll(this.mainDeck);
        return list;
    }

    public int[] getTrackInformation(){
        int[] info = new int[3];
        info[0] = this.track.getPlaceofPlayer(0);
        info[1] = this.track.getPlaceofPlayer(1);
        info[2] = this.track.getSpacesofPlayers();
        return info;
    }
    
    public int[] getPlayerScores(){
        //修正　トラックの情報はリセット時に書き直すのでこちらからとらないと正しくない
        int[] socres = new int[2];
        socres[0] = this.player[0].playerScore;
        socres[1] = this.player[1].playerScore;
        return socres;
    }
    
    public int getGameStatus(){
        return this.gameStatus;
    }
    
    public ArrayList<Integer> getUsedCards(){
        return this.usedCards;
    }
    
    //ここは仕様上の不具合で、ゲーム側のmoveSwordmanは何枚目の手札を使うかの指定になっているのでIDを取り直す。
    public boolean moveSwordmanWithValue(int playerID,int cardNumber,int backward_or_forward){
        ArrayList<Integer> list = this.player[playerID].getCardList();
        int cardID = list.indexOf(cardNumber);
        if(cardID == -1){
            return false;
        }
        return moveSwordman(playerID,cardID,backward_or_forward);
    }
    
    
    public boolean moveSwordman(int playerID,int cardID,int backward_or_forward){
        if(!checkPlayeAblePlayer(playerID)){
            return false;
        }
        int cardNumber = this.player[playerID].getCardValueOftheCard(cardID);
        if(!this.track.checkMoveableSwordman(playerID, cardNumber,backward_or_forward)){
            return false;
        }
        if(backward_or_forward == SWORDMAN_MOVE_FORWARD){
            this.track.forwardSwordman(playerID, cardNumber);
            this.player[playerID].playACard(cardNumber);
            this.usedCards.add(cardNumber);
            this.drawCards(playerID);
        } else if(backward_or_forward == SWORDMAN_MOVE_BACKWARD){
            this.track.backwardSwordman(playerID, cardNumber);
            this.player[playerID].playACard(cardNumber);
            this.usedCards.add(cardNumber);
            this.drawCards(playerID);
        } else {
            return false;
        }
        //山札の枚数を確認
        if(this.mainDeck.isEmpty()){
            //移動が終わって山札がない場合は終了処理に入る
            this.cardDeckEmptyProcess();
            return true;
        }

        //手番の切り替え
        this.playerChangeProcess(playerID);

        return true;
    }
    
    
    private void cardDeckEmptyProcess(){
        this.gameStatusChange(GAMESTATUS_CALC_SCORE);
        int win = this.determinateWinner();
        if(win == 0){
            this.player[win].addScore();
        } else if(win == 1){
            this.player[win].addScore();
        } else {
            //引き分け
        }
        if(win == 0 || win== 1){
            //if(this.player[win].getScore() >= defalutVictoryPointSize){
            if ((this.player[0].getScore() + this.player[1].getScore()) >= totalGame){
                //ゲーム終了
                this.gameStatusChange(GAMESTATUS_GAMEEND);
                Insert i = new Insert(playerNames[0], player[0].getScore(), playerNames[1], player[1].getScore());
                i.insert();
            } else {
                //次のラウンドへ移る
                this.gameStatusChange(GAMESTATUS_ROUND_END);
                this.startNewRound();
            }
        } else {
            //引き分け
            //次のラウンドへ移る
            this.gameStatusChange(GAMESTATUS_ROUND_END);
            this.startNewRound();
        }
    }
    
    private void playerChangeProcess(int playerID){
        //手番の切り替え
        if(this.isPlayAble(this.getOppnentID(playerID))){
            this.currentPlayer = this.getOppnentID(playerID);
            if(playerID == 0){
                this.gameStatusChange(GAMESTATUS_INGAME_PLAYER1_PLAY);
            } else if(playerID == 1){
                this.gameStatusChange(GAMESTATUS_INGAME_PLAYER0_PLAY);
            }
        } else {
            //相手を行動不能にしたら勝利
            this.gameStatusChange(GAMESTATUS_CALC_SCORE);
            this.player[playerID].addScore();
            this.winner = playerID;
            if(this.player[playerID].getScore() >= defalutVictoryPointSize){
                //ゲーム終了
                this.gameStatusChange(GAMESTATUS_GAMEEND);
            } else {
                //次のラウンドへ移る
                this.gameStatusChange(GAMESTATUS_ROUND_END);
                this.startNewRound();
            }
        }
    }
    
    /** カードの山札が切れた時の処理 */
    private int determinateWinner(){
        this.winner = -1;
        //それぞれの持つカードで適正距離の枚数を数える
        int cardNumber = this.track.getSpacesofPlayers();
        int player0cards = this.player[0].getNumberofCards(cardNumber);
        int player1cards =  this.player[1].getNumberofCards(cardNumber);
        if(player0cards > player1cards){
            this.winner = 0;
        } else if(player0cards > player1cards){
            this.winner = 1;
        } else {
            //同数の場合は進んだ距離を計算
            int p0move = this.track.getPlaceofPlayer(0) - 1;
            int p1move = defaultTrackSize - this.track.getPlaceofPlayer(1);
            if(p0move > p1move){
                this.winner = 0;
            } else { // fixed 2023.04.27 12:00
                this.winner = 1;
            }
        }
        return this.winner;
    }
    
    public int[] getWinner(){
        int[] info = new int[3];
        info[2] = this.winner;
        info[0] = this.getPlayerScores()[0];
        info[1] = this.getPlayerScores()[1];
        return info;
    }
    
    
    
    
    /** 相手プレイヤーが移動または攻撃が可能かを判定する：移動も攻撃も不可能な場合は負け */
    private boolean isPlayAble(int playerID){
        for(int cid=0;cid<this.player[playerID].getNumberofCards();cid++){ //fixed 23.05.22
            int cardNumber = this.player[playerID].getCardValueOftheCard(cid);
            //前に動けるかを確認
            if(this.checkMoveableSwordman(playerID, cardNumber, SWORDMAN_MOVE_FORWARD)){
                return true;
            }
            //後ろに動けるかを確認
            if(this.checkMoveableSwordman(playerID, cardNumber, SWORDMAN_MOVE_BACKWARD)){
                return true;
            }
            //攻撃できるかを確認
            if(this.checkPlayableCards(playerID, cardNumber, 1)){
                if(cardNumber == this.track.getSpacesofPlayers()){
                    return true;
                } 
            }
        }
        return false;
    }
    
    public boolean checkAttackableToOpponent(int playerID,int cardNumber,int numofcards){
        if(!this.checkPlayeAblePlayer(playerID)){
            return false;
        }
        if(this.track.getSpacesofPlayers() != cardNumber){
            return false;
        }
        if(!this.player[playerID].checkPlayableCards(cardNumber, numofcards)){
            return false;
        }
        return true;
    }
    
    /** 攻撃を行うと次の結果のどれかが返ってくる
     * -1 攻撃失敗：距離や指定されたカードが正しくない
     *　0 攻撃成功・勝利：相手にヒットしてラウンド終了
     *  1 攻撃成功・反撃：相手がparryしてラウンド継続
     */
    public int attacktoOppnent(int playerID,int cardNumber,int numofcards){
        if(!this.checkAttackableToOpponent(playerID,cardNumber,numofcards)){
            return -1;
        }
        if(this.player[getOppnentID(playerID)].checkPlayableCards(cardNumber, numofcards)){
            //parry
            if(playerID == 0){
                this.gameStatusChange(GAMESTATUS_INGAME_PLAYER1_REACT);
                //その分手札を捨て、リーロードせずに手番が来る
                this.player[playerID].playCards(cardNumber, numofcards);
                this.player[getOppnentID(playerID)].playCards(cardNumber, numofcards);
                for(int i=0;i<numofcards;i++){
                    this.usedCards.add(cardNumber);
                    this.usedCards.add(cardNumber);
                }
                this.drawCards(playerID);

                //山札の枚数を確認
                if(this.mainDeck.isEmpty()){
                    //カードを引き終わったあとに、山札がない場合は終了処理に入る
                    this.cardDeckEmptyProcess();
                    return 0;
                }
                //手番の切り替え
                this.playerChangeProcess(playerID);

            } else if(playerID == 1){
                this.gameStatusChange(GAMESTATUS_INGAME_PLAYER0_REACT);
                //その分手札を捨て、リーロードせずに手番が来る
                this.player[playerID].playCards(cardNumber, numofcards);
                this.player[getOppnentID(playerID)].playCards(cardNumber, numofcards);
                for(int i=0;i<numofcards;i++){
                    this.usedCards.add(cardNumber);
                    this.usedCards.add(cardNumber);
                }
                this.drawCards(playerID);
                
                //山札の枚数を確認
                if(this.mainDeck.isEmpty()){
                    //カードを引き終わったあとに、山札がない場合は終了処理に入る
                    this.cardDeckEmptyProcess();
                    return 0;
                }

                //手番の切り替え
                this.playerChangeProcess(playerID);

            } else {
                return -1;
            }
        } else {
            //hit
            this.player[playerID].playCards(cardNumber, numofcards);
            this.gameStatusChange(GAMESTATUS_CALC_SCORE);
            this.player[playerID].addScore();
            if(this.player[playerID].getScore() >= defalutVictoryPointSize){
                //ゲーム終了
                this.gameStatusChange(GAMESTATUS_GAMEEND);
            } else {
                //次のラウンドへ移る
                this.gameStatusChange(GAMESTATUS_ROUND_END);
                this.startNewRound();
            }
            return 0;
        }
        return -1;
    }
    private int getOppnentID(int playerID){
        return (playerID+1)%2;
    }
    
    
    public boolean setEvaluationValues(int playerID,HashMap<String,Double> evalue){
        if(playerID == 0){
            if(this.gameStatus == GAMESTATUS_INGAME_PLAYER0_PLAY){
                this.player[0].setEvalationValues(evalue);
                this.gameStatusChange(GAMESTATUS_INGAME_PLAYER0_EVALUATED);
                return true;
            }
            return false;
        } else if (playerID == 1){
            if(this.gameStatus == GAMESTATUS_INGAME_PLAYER1_PLAY){
                this.player[1].setEvalationValues(evalue);
                this.gameStatusChange(GAMESTATUS_INGAME_PLAYER1_EVALUATED);
                return true;
            }
            return false;
        }
        return false;
        
    }
    
    public boolean checkPlayeAblePlayer(int playerID){
        if(playerID == 0){
            if(this.gameStatus == GAMESTATUS_INGAME_PLAYER0_EVALUATED){
                return true;
            } else if(this.gameStatus == GAMESTATUS_INGAME_PLAYER0_REACT){
                return true;
            }
            return false;
        } else if (playerID == 1){
            if(this.gameStatus == GAMESTATUS_INGAME_PLAYER1_EVALUATED){
                return true;
            } else if(this.gameStatus == GAMESTATUS_INGAME_PLAYER1_REACT){
                return true;
            }
            return false;
        }
        return false;
    }
    
    public boolean checkMoveableSwordman(int playerID,int step,int backward_or_forward){
        return this.track.checkMoveableSwordman(playerID, step, backward_or_forward);
    }
    
    public boolean checkPlayableCards(int playerID,int cardValue,int numofcards){
        return this.player[playerID].checkPlayableCards(cardValue,numofcards);
    }
    
    /** イベントを発火させるために状態チェンジは必ずこのメソッドで呼び出すこと */
    private void gameStatusChange(int status){
        int backState = this.gameStatus;
        this.gameStatus = status;
        this.changes.firePropertyChange("GAME_STATE_CHANGED", backState, this.gameStatus);
    }
    
    /* ゲーム状態の出力メソッド */
    public static void printGameState(EngardeGame game,PrintStream ps){
        ps.println("-----");
        
        ps.println("GameStatus: " + game.getGameStatus());
        
        printCardListAll(game,ps);
        printTrackStatus(game,ps);
        
        ps.println("-----");
        
    }
    
    public static void printCardListAll(EngardeGame game,PrintStream ps){
        ps.print("player0:");
        printCardList(game.getHandofPlayer(0),ps);
        ps.print("player1:");
        printCardList(game.getHandofPlayer(1),ps);
        ps.print("Deck:");
        printCardList(game.getDeckCards(),ps);
        ps.print("Used:");
        printCardList(game.getUsedCards(),ps);
    }
    
    public static void printTrackStatus(EngardeGame game,PrintStream ps){
        int[] info = game.getTrackInformation();
        int[] scores = game.getPlayerScores();
        ps.println("Player 0 position:"+info[0]);
        ps.println("Player 1 position:"+info[1]);
        ps.println("Players space:"+info[2]);
        ps.println("Players score:"+scores[0]+ " vs " + scores[1]);
        
    }
    
    public static void printCardList(ArrayList<Integer> hands,PrintStream ps){
        ps.print("[");
        for(int i : hands){
            ps.print(i);
            ps.print(" ");
        }
        ps.println("]");
    }
}
