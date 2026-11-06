package org.example;

public class Board {
    private int position0;
    private int position1;
    private int decknb;

    public Board(int position0, int position1, int decknb) {
        this.position0 = position0;
        this.position1 = position1;
        this.decknb = decknb;
    }

    public int getPosition0() {
        return position0;
    }

    public void setPosition0(int position0) {
        this.position0 = position0;
    }

    public int getPosition1() {
        return position1;
    }

    public void setPosition1(int position1) {
        this.position1 = position1;
    }

    public int getDecknb() {
        return decknb;
    }

    public void setDecknb(int decknb) {
        this.decknb = decknb;
    }
}
