package Ai;

import java.util.ArrayList;
import java.util.List;

// Définition de la classe Position
    class Position {
        public Board board;
        public Hand hand;
        public Integer pos;
        public Integer card;
        public String direction = "F";
        public Integer evalValue;
        public Integer numberOfCard = null;

        
        public Position(Hand h, Board b){
            this.hand = h;
            this.board = b;
            this.pos = b.getPos(b.getCurrentPlayer());
           
        }
        public Position(Hand h, Board b, int pos){
            this(h, b);
            this.pos = pos;
        }

        public Position(Hand h, Board b, int pos, String direction){
            this(h, b, pos);
            this.direction = direction;
        }

        public Position(Hand h, Board b, int pos,String direction, int card){
            this(h, b, pos, direction);
            this.card = card;
        }
        public Board getBoard() {
            return board;
        }
        public void setBoard(Board board) {
            this.board = board;
        }
        public Hand getHand() {
            return hand;
        }
        public void setHand(Hand hand) {
            this.hand = hand;
        }
        public Integer getPos() {
            return pos;
        }
        public void setPos(Integer pos) {
            this.pos = pos;
        }
        public Integer getCard() {
            return card;
        }
        public void setCard(Integer card) {
            this.card = card;
        }
        public String getDirection() {
            return direction;
        }
        public void setDirection(String direction) {
            this.direction = direction;
        }
        public Integer getEvalValue() {
            return evalValue;
        }
        public void setEvalValue(Integer evalValue) {
            this.evalValue = evalValue;
        }

        public void setNumberOfCard(int num){
            this.numberOfCard = num; 
        }


        public Integer[] chsooseAttack(){
            Integer[] attack = null;
    
            if (this.board.getDistancePlayer() < 5 && this.hand.getCountCard(this.board.getDistancePlayer()) > 0) {
                attack = new Integer[2];
                attack[0] = this.board.getDistancePlayer();
                attack[2] = this.hand.getCountCard(this.board.getDistancePlayer());
            }

            return attack;
        }


    }