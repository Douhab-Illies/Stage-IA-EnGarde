package org.example;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Hand {
    private int Hand1;
    private int Hand2;
    private int Hand3;
    private int Hand4;
    private int Hand5;

    public int getHand1() {
        return Hand1;
    }

    public void setHand1(int hand1) {
        Hand1 = hand1;
    }

    public int getHand2() {
        return Hand2;
    }

    public void setHand2(int hand2) {
        Hand2 = hand2;
    }

    public int getHand3() {
        return Hand3;
    }

    public void setHand3(int hand3) {
        Hand3 = hand3;
    }

    public int getHand4() {
        return Hand4;
    }

    public void setHand4(int hand4) {
        Hand4 = hand4;
    }

    public int getHand5() {
        return Hand5;
    }

    public void setHand5(int hand5) {
        Hand5 = hand5;
    }

    public void getallhandlist(){
        ArrayList<Integer> lescartes= new ArrayList<Integer>();
        lescartes.add(this.Hand1);
        lescartes.add(this.Hand2);
        lescartes.add(this.Hand3);
        lescartes.add(this.Hand4);
        lescartes.add(this.Hand5);

    }
    public Hand(int hand1, int hand2, int hand3, int hand4, int hand5) {
        Hand1 = hand1;
        Hand2 = hand2;
        Hand3 = hand3;
        Hand4 = hand4;
        Hand5 = hand5;
    }
}
