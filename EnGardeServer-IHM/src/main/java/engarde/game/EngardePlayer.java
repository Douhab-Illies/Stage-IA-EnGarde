/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package engarde.game;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ２０２３ゲーム　アンギャルド　それぞれのプレイヤーが持つ情報
 * @author ktajima
 */
public class EngardePlayer {
    /** プレイヤーの位置 */
    protected int swordmanPlace;
    /** プレイヤーの点数 */
    protected int playerScore;
    /** 保持しているカード */
    private ArrayList<Integer> playerHand;
    /** 各カードの評価値（内部保持情報） */
    private HashMap<String,Double> evaluationValues;
    
    public EngardePlayer(){
        this.playerHand = new ArrayList<>();
        this.evaluationValues = new HashMap<>();
    }
    
    public int getNumberofCards(){
        return this.playerHand.size();
    }
    public int getNumberofCards(int cardValue){
        int count = 0;
        for(int cv:this.playerHand){
            if(cv == cardValue){
                count++;
            }
        }
        return count;
    }
    
    public ArrayList<Integer> getCardList(){
        return this.playerHand;
    }
    
    public int getScore(){
        return this.playerScore;
    }
    
    public int addScore(){
        this.playerScore++;
        return this.playerScore;
    }
    
    public void clearCards(){
        this.playerHand.clear();
    }
    public void giveACard(int cardValue){
        this.playerHand.add(cardValue);
    }
    
    public boolean playACard(int cardValue){
        return playCards(cardValue, 1);
    }
    public boolean playCards(int cardValue,int numofcards){
        if(!checkPlayableCards(cardValue,numofcards)){
            return false;
        }
        for(int i=0;i<numofcards;i++){
            int index = this.playerHand.indexOf(cardValue);
            this.playerHand.remove(index);
            
        }
        return true;
    }
    
    public boolean checkPlayableCards(int cardValue,int numofcards){
        int count = 0;
        for(int handvalue:this.playerHand){
            if(handvalue == cardValue){
                count++;
            }
        }
        if(count >= numofcards ){
            return true;
        } else {
            return false;
        }
    }
    
    public int getCardValueOftheCard(int cardID){
        if(cardID > 5){
            return -1;
        }
        return this.playerHand.get(cardID);
    }
    
    public void setEvalationValues(HashMap<String,Double> input){
        for(String key:input.keySet()){
            this.evaluationValues.put(key, input.get(key));
        }
    }

    //第一引数はカードの数字
    //第二引数は
    //EngardeGame.SWORDMAN_MOVE_FORWARD
    //EngardeGame.SWORDMAN_MOVE_BACKWARD
    //から選択
    public void setEvalationValueOf(int NumofCard,int ForB, double value){
        String key = MakeEvalationKey(NumofCard,ForB);
        if(key == null) return;

        this.evaluationValues.put(key, value);
    }
    public double getEvaluetionValueOf(int NumofCard,int ForB){
        String key = MakeEvalationKey(NumofCard,ForB);
        if(key == null) return 0.0;
        
        if(this.evaluationValues.containsKey(key)){
            return this.evaluationValues.get(key);
        }
        
        return 0.0;
    }
    
    public static String MakeEvalationKey(int NumofCard,int ForB){
        //第一引数はカードの数字
        //第二引数は
        //EngardeGame.SWORDMAN_MOVE_FORWARD
        //EngardeGame.SWORDMAN_MOVE_BACKWARD
        //から選択
        if(NumofCard < 0){
            return null;
        }
        if(NumofCard > 5){
            return null;
        }
        String ret = Integer.toString(NumofCard);
        
        if(ForB == EngardeGame.SWORDMAN_MOVE_FORWARD){
            return ret+"F";
        } else if(ForB == EngardeGame.SWORDMAN_MOVE_BACKWARD){
            return ret+"B";
        } else {
            return null;
        }
        
    }
    
}
