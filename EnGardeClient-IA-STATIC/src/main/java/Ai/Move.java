package Ai;

public class Move {
    private int cardIndex; // Index de la carte dans la main du joueur
    private int newPosition; // Nouvelle position sur le plateau de jeu


    //TMP
    private int card;
    private String direction;

    public Move(int card, String direction) {
        this.card = card;
        this.direction = direction;
    }
    public String getType(){
        return this.direction;
    }

    //TMP


    public Move(int cardIndex, int newPosition) {
        this.cardIndex = cardIndex;
        this.newPosition = newPosition;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(int cardIndex) {
        this.cardIndex = cardIndex;
    }

    public int getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(int newPosition) {
        this.newPosition = newPosition;
    }


    
}