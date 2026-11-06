package Ai;

import java.util.HashMap;
import java.util.Map;
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

    private static String[] cards = { "1", "1", "1", "1", "1",
                                        "2", "2", "2", "2", "2",
                                        "3", "3", "3", "3", "3",
                                        "4", "4", "4", "4", "4",
                                        "5", "5", "5", "5", "5"};


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
        //printHand();
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

        //this.printHand();
        
    }




    public Hand(Hand hand) {
        this.hand = hand.getHand();
        this.Type = hand.getType();
        this.From = hand.getFrom();
        this.To = hand.getTo();
    }

    public Hand() {

    }

    public int getCountCard(int card) {
        int count = 0;
        for (int i = 0; i < hand.length; i++) {
            if (this.hand[i] != null && hand[i].equals(Integer.toString(card))) {
                count++;
            }
        }
        return count;
    }

    public int getCountInferiorCard(int card) {
        int count = 0;
        for (int i = 0; i < hand.length; i++) {
            if (this.hand[i] != null && Integer.parseInt(hand[i]) < card) {
                count++;
            }
        }
        return count;
    }

    public int haveMoretwo(String val) {
        int cpt = 0;
        for (int i = 0; i < hand.length; i++) {
            if (val.equals(hand[i])) {
                cpt++;
            }
        }

        return cpt;
    }

    public void printHand() {

        System.out.println("HAND : ");
        for (int i = 0; i < hand.length; i++) {
            System.out.println(i + "->" + this.hand[i]);
        }
    }



    public int numberOfACard(String card) {
        int cpt = 0;

        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null && hand[i].equals(card)) {
                cpt++;
            }
        }

        return cpt;
    }

    public int size() {
        return hand.length;
    }

    public int numberOfTotalACard() {
        int cpt = 0;
        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null) {
                cpt++;
            }
        }

        return cpt;
    }

    public String[] getHand() {
        return hand;
    }

    public String getHand(int i) {
        if (this.hand[i - 1] != null) {
            return this.hand[i - 1];
        }
        return "0";
    }

    public String get(int i){
        return this.hand[i];
    }





    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getFrom() {
        return From;
    }

    public void setFrom(String from) {
        From = from;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < hand.length; i++) {
            res += i + " [" + this.get(i) + "] ";
        }
        return res;
    }

    public String getTo() {
        return To;
    }

    public void setTo(String to) {
        To = to;
    }

    public String getMinOccurrenceMin() {
        Map<String, Integer> occurrenceMap = new HashMap<>();

        for (String card : hand) {
            occurrenceMap.put(card, occurrenceMap.getOrDefault(card, 0) + 1);
        }

        int minOccurrence = Integer.MAX_VALUE;
        String minOccurrenceCard = null;
        for (Map.Entry<String, Integer> entry : occurrenceMap.entrySet()) {
            int occurrence = entry.getValue();
            if (occurrence < minOccurrence) {
                minOccurrence = occurrence;
                minOccurrenceCard = entry.getKey();
            }
        }

        if (minOccurrenceCard == null) {
            minOccurrenceCard = Integer.toString(this.getMin());
        }

        return minOccurrenceCard;
    }

    public String getMinOccurrenceMax() {
        Map<String, Integer> occurrenceMap = new HashMap<>();

        for (String card : hand) {
            occurrenceMap.put(card, occurrenceMap.getOrDefault(card, 0) + 1);
        }

        int minOccurrence = Integer.MAX_VALUE;
        String minOccurrenceCard = null;
        for (Map.Entry<String, Integer> entry : occurrenceMap.entrySet()) {
            int occurrence = entry.getValue();
            if (occurrence <= minOccurrence) {
                minOccurrence = occurrence;
                minOccurrenceCard = entry.getKey();
            }

        }

        if (minOccurrenceCard == null) {
            minOccurrenceCard = Integer.toString(this.getMin());
        }

        return minOccurrenceCard;
    }

    public Integer getMin() {

        int min = Integer.MAX_VALUE;

        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null && Integer.parseInt(hand[i]) <= min) {
                min = Integer.parseInt(hand[i]);
            }
        }

        for (int i = 0; i < hand.length; i++) {
            if (hand[i] != null && Integer.parseInt(hand[i]) == min) {
                return min;
            }
        }

        return null;
    }

    public void retirerCarte(int i) {
        this.hand[i] = null;
    }


    public int getSumm(){
        //System.out.println("[1] : " + this.hand[0]);
        int sum = 0;
        for (int i = 0; i < this.hand.length; i++) {
            if (this.hand[i] != null) {
                //System.out.println("SUMM : " + sum);
                sum += Integer.parseInt(hand[i]);
            }
        }
        return sum;
    }


    public void removeCard(int i) {
        for (int j = 0; j < this.size(); j++) {
            System.out.println("JE VAIS REMOVE " + i + " soit " + this.hand[j]);
            if (this.hand[j] != null && i == Integer.parseInt(this.hand[j])) {
                this.hand[j] = null;
            }
        }

    }


    public void removeCardPos(int i){
        this.hand[i] = null;
    }

    public static void main(String[] args) {
        String[] main = {"1", "2", "3", "2", "1"};
        Hand hand = new Hand(main);

        for (int i = 0; i < hand.size(); i++) {
            System.out.println(hand.get(i));
        }
    }

    

}
