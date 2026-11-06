package Ai;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Position position;
    private List<Node> children;

    public Node(Position position) {
        this.position = position;
        this.children = new ArrayList<>();
    }

    public Position getPosition() {
        return position;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addChild(Node child) {
        children.add(child);
    }


    
    /*public void generateChildren(){
        Position position = this.position;
        Hand hand = position.hand;
        String[] move = {"F", "A", "B"};
        // Effacer les enfants précédents
        this.children.clear();

        for (int j = 0; j < move.length; j++) {
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i) != null) {
                    Hand newHand = new Hand(hand.getHand()); 
                    newHand.removeCard(Integer.parseInt(hand.get(i)));
                    
                    if (move[j].equals("F") || move[j].equals("B")) {
                        //System.out.println("hand.get(i) : " + hand.get(i) + " move[j] : " + move[j]);
                        if (position.board.getNewPos(hand.get(i), move[j]) != null && position.board.getNewPos(hand.get(i), move[j]) != position.getPos()) {
                            int newPos = position.board.getNewPos(hand.get(i), move[j]);
                            Position newPosition = new Position(newHand, position.board, newPos, move[j]);
                            this.children.add(new Node(newPosition));
                        }
                    }
                    else if (this.getPosition().chsooseAttack() != null &&  move[j].equals("A")){
                        int newPos = position.pos;
                        this.position.setNumberOfCard(this.position.hand.getCountCard(this.position.board.getDistancePlayer()));
                        Position newPosition = new Position(newHand, position.board, newPos, move[j]);
                        this.children.add(new Node(newPosition));
                    }
                }
            }
        }
    }*/


    public void generateChildren(){

        String[] move = {"F", "A", "B"};
        this.children.clear();

        for (int j = 0; j < move.length; j++) {
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i) != null) {
                    Hand newHand = new Hand(hand.getHand()); 
                    newHand.removeCard(Integer.parseInt(hand.get(i)));
                    
                    if (move[j].equals("F") || move[j].equals("B")) {
                        //System.out.println("hand.get(i) : " + hand.get(i) + " move[j] : " + move[j]);
                        if (position.board.getNewPos(hand.get(i), move[j]) != null && position.board.getNewPos(hand.get(i), move[j]) != position.getPos()) {
                            int newPos = position.board.getNewPos(hand.get(i), move[j]);
                            Position newPosition = new Position(newHand, position.board, newPos, move[j]);
                            this.children.add(new Node(newPosition));
                        }
                    }
                    else if (this.getPosition().chsooseAttack() != null &&  move[j].equals("A")){
                        int newPos = position.pos;
                        this.position.setNumberOfCard(this.position.hand.getCountCard(this.position.board.getDistancePlayer()));
                        Position newPosition = new Position(newHand, position.board, newPos, move[j]);
                        this.children.add(new Node(newPosition));
                    }
                }
            }
        }
    }

    public String toString(){
        String res = this.position.pos +" " + this.position.direction;

        if (this.position.numberOfCard != null && this.position.direction.equals("A")) {
            res += "x" +  this.position.numberOfCard;
        }

        return res;
    }



}
