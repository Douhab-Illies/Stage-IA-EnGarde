package Ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Hand {

    public final static int MAX_CARD_VALUE = 5;
    public String Hand1;
    public String Hand2;
    public String Hand3;
    public String Hand4;
    public String Hand5;
    public String Type;
    public String From;
    public String To;
    public String[] hand = new String[5];
    private static final Random random = new Random();

    private String[] evilCards = { "1", "1","1","1","1",
                                    "2", "2","2","2","2",
                                    "3", "3","3","3","3",
                                    "4", "4","4","4","4",
                                    "5", "5","5","5","5"};


    public Hand(String hand1, String hand2, String hand3, String hand4, String hand5, String type, String from,
            String to) {
        this.hand[0] = hand1;
        this.hand[1] = hand2;
        this.hand[2] = hand3;
        this.hand[3] = hand4;
        this.hand[4] = hand5;
        Type = type;
        From = from;
        To = to;
    }

    public Hand(String[] hand) {
        this.hand = hand;
    }

    public Hand(HashMap<String, String> data) {
        this.hand[0] = data.get("Hand1");
        this.hand[1] = data.get("Hand2");
        this.hand[2] = data.get("Hand3");
        this.hand[3] = data.get("Hand4");
        this.hand[4] = data.get("Hand5");
        Type = data.get("Type");
        From = data.get("From");
        To = data.get("To");

        
    }

    public Hand(Hand hand) {
        this.hand = hand.getHand();
        this.Type = hand.getType();
        this.From = hand.getFrom();
        this.To = hand.getTo();
    }

    public Hand() {}

    public String[] getHand() {return hand;}
    public String getType() {return Type;}
    public String getFrom() {return From;}
    public String getTo() {return To;}

    public String get(int i){
        if (i >= 0 && i <= 4) {
            return hand[i] != null ? hand[i] : null;
        }
        return null;
    }

    public int size(){return this.hand.length;}

    public int getNumeberOfCards(int card){
        int count = 0;
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null && Integer.parseInt(hand[i]) == card) {
                count++;
            }
        }
        return count;
    }

    public int getNumeberOfEvilCards(int card){
        int count = 0;
        for (int i = 0; i < evilCards.length; i++) {
            if (evilCards[i] != null && Integer.parseInt(evilCards[i]) == card) {
                count++;
            }
        }
        return count;
    }


    public int remove(int card) {

        for (int i = 0; i < hand.length; i++) {
            if (this.hand[i] != null && card == Integer.parseInt(this.hand[i])) {
                this.hand[i] = null;
                return i;
            }
        }

        return -1;
    }




    public Hand copy() {
        Hand copyHand = new Hand();
        for (int i = 0; i < hand.length; i++) {
            copyHand.hand[i] = hand[i];
        }
        copyHand.Type = Type;
        copyHand.From = From;
        copyHand.To = To;
        return copyHand;
    }



    public String[] evilRemove(int card) {

        for (int i = 0; i < evilCards.length; i++) {
            if (evilCards[i] != null && evilCards[i].equals(String.valueOf(card))) {
                this.evilCards[i] = null;
                break; 
            }
        }

        return this.evilCards;
    }

    public void setEvilCards(String[] evilCards){
        this.evilCards = evilCards;
    }




    public void initEvilCards(){
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null) {
                evilRemove(Integer.parseInt(hand[i]));
            }
        }
    }

    public String[] getEvilCards(){
        return this.evilCards;
    }

    public String getEvil(int i){
        if (i >= 0 && i <= 25) {
            return evilCards[i] != null ? evilCards[i] : null;
        }
        return null;
    }



    public int evilSize(){
        return this.evilCards.length;
    }
    


    public String toString(){
        String res = "HAND : ";
        for (int i = 0; i < hand.length; i++) {
            res += hand[i] + " ";
        }

        res += "\nEVIL : ";
        for (int i = 0; i < evilCards.length; i++) {
            res += evilCards[i] + " ";
        }

        return res;
    }


    public String evilToString(){
        String res = "EVIL : ";
        for (int i = 0; i < evilCards.length; i++) {
            res += evilCards[i] + " ";
        }

        return res;
    }

    public void printHand(){
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null) {
                System.out.print(hand[i] + " ");
            }
        }
        System.out.println();
    }

    public void printEvilHand(){
        for (int i = 0; i < evilCards.length; i++) {
            if (evilCards[i] != null) {
                System.out.print(evilCards[i] + " ");
            }
        }
        System.out.println();
    }


    public static void main(String[] args) {
        String[] s = {"1", "4", "3", "3", "1"};
        Hand hand = new Hand(s);
        hand.initEvilCards();
        String[] evil = hand.getEvilCards();
        
        for (String string : evil) {
            System.out.println(string);
        }
    }
}
