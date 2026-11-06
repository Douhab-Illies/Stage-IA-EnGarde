package Ai;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private Position position;
    private List<Node> children;
    private boolean isPlayerTurn;
    private Node parent;
    String[] move = {"F", "B"};
    public static int distance;

    public Node(Position position, boolean isPlayerTurn) {
        this.position = position;
        this.children = new ArrayList<>();
        this.isPlayerTurn = isPlayerTurn;
        this.parent = null;
    }

    public Node(Position position, boolean isPlayerTurn, Node parent) {
        this.position = position;
        this.children = new ArrayList<>();
        this.isPlayerTurn = isPlayerTurn;
        this.parent = parent;
    }

    public Position getPosition() {
        return position;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getParent() {
        return parent;
    }

    public void addChild(Node child) {
        child.setParent(this);
        children.add(child);
    }

    private void setParent(Node parent) {
        this.parent = parent;
    }

    public void generateChildren() {
        Position position = this.getPosition();
        Hand hand = position.getHand();
        Board board = position.getBoard();
        this.children.clear();

        if (isPlayerTurn) {
            generatePlayerMoves(hand, board);
        } else {
            generateEvilMoves(hand, board);
        }
    }

    private void generatePlayerMoves(Hand hand, Board board) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) != null) {
                for (String move : this.move) {
                    Integer newPos = board.move(position.getPos(), hand.get(i), move);
                    if (newPos != null && isValidMove(newPos, position.getEvilPos())) {
                        Hand newHand = hand.copy();
                        newHand.remove(Integer.parseInt(hand.get(i)));
                        Position newPosition = new Position(newHand, board.copy(), newPos, move);
                        Node childNode = new Node(newPosition, false, this);
                        childNode.getPosition().setCard(Integer.parseInt(hand.get(i)));
                        childNode.getPosition().setNumberOfCard(childNode.getPosition().getHand().getNumeberOfCards(Integer.parseInt(hand.get(i))));
                        childNode.getPosition().evaluate();

                        if (this.getPosition().getEvalValue() > 1000.0) {
                            System.out.println( "DISTANCE : " + Node.distance);
                            childNode.getPosition().setDirection("A");
                            childNode.getPosition().setCard(Node.distance);
                            childNode.getPosition().setNumberOfCard(this.getPosition().getHand().getNumeberOfCards(Node.distance));

                        }
                       
                        if (this.getPosition().getEvalValue() != null &&  Integer.parseInt(hand.get(i)) <= board.getDistanceWhithNegative()){
                            this.children.add(childNode);

                        }


                    }
                }
            }
        }
    }

    public List<String> getPossibleActions() {
        List<String> actions = new ArrayList<>();
        
        for (int i = 0; i < getPosition().getHand().size(); i++) {
            String card = getPosition().getHand().get(i);
            if (card != null) {
                for (String direction : new String[]{"F", "B"}) {
                    String action = card + direction;
                    Integer newPos = getPosition().getBoard().getNewPos(getPosition().getPos(), card, direction);
                    if (newPos != null) {
                        actions.add(action);
                    }
                }
            }
        }
        return actions;
    }

    private void generateEvilMoves(Hand hand, Board board) {


        for (int i = 0; i < hand.evilSize(); i++) {
            if (hand.getEvil(i) != null) {
                for (String move : this.move) {
                    Integer newPos = board.evilPlay(position.getEvilPos(), hand.getEvil(i), move);
                    if (newPos != null && isValidMove(newPos, position.getPos())) {
                        Hand newHand = hand.copy();
                        newHand.evilRemove(Integer.parseInt(hand.getEvil(i)));
                        Position newPosition = new Position(newHand, board.copy(), position.getPos(), move);
                        newPosition.setEvilPos(newPos);
                        Node childNode = new Node(newPosition, true, this);
                        childNode.getPosition().evaluate();
                        

                        this.children.add(childNode);
                    }
                }
            }
        }
    }

    private boolean isValidMove(int newPos, int opponentPos) {
        return newPos > 0 && newPos < 23 && getDistance(newPos, opponentPos) > 0;
    }

    private int getDistance(int a, int b) {
        return Math.abs(a - b);
    }

    public String toString() {
        return this.getPosition().getPos() + " " + this.getPosition().getDirection() + " " + this.getPosition().getEvilPos() + " " + this.getPosition().getEvalValue();
    }


    public static void main(String[] args) {
        Board board = new Board("23", "1", "0", "0", "0", "0", "0", "0");
        String[] main = {"5", "4", "3", "2", "1"};
        Hand hand = new Hand(main);
        Position initialPosition = new Position(hand, board);
        Node rootNode = new Node(initialPosition, true); // Player starts
        board.printBoard();
        TreeGenerator.generateTree(rootNode, 3);
        TreeGenerator.printTree(rootNode, 3);
    }
}
