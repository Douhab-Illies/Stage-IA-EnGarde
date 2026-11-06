package Ai;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node {
    private Position position;
    private List<Node> children;
    private boolean isPlayerTurn;
    private Node parent;
    String[] move = {"F", "B"};
    public static int distance;

    public QLearning qLearning;
    public String qTableFilePath = "qtable.ser";

    public  static boolean win = false;
    public static boolean loose = false;

    public Node(Position position, boolean isPlayerTurn) {
        this(position, isPlayerTurn, null);

        qTableFilePath = "qtable.ser";
        this.qLearning = new QLearning(0.1, 0.99, 1.0, 0.99);

        // Chargement de la table Q si elle existe
        File qTableFile = new File(qTableFilePath);
        qLearning.loadQTable(qTableFilePath);
        

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

    public boolean isTerminal(){
        return this.getPosition().getEvalValue() > 1000.0 || Node.loose;
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


    public static int tour = 0;

    public void trainQLearning(int episodes) {
        for (int i = 0; i < episodes; i++) {

            Position currentPosition = this.getPosition();
            while (!isTerminal(currentPosition)) {
                String action = null;
                Position nextPosition = null;
                if (tour == 1) {
                    action = qLearning.selectAction(currentPosition);
                    if (action == null) {
                        break;
                    }
                    nextPosition = simulateAction(currentPosition, action);
                }else{
                    action = qLearning.selectEvilAction(currentPosition);
                    if (action == null) {
                        break;
                    }
                    nextPosition = simulateEvilAction(currentPosition, action);
                }
                if (nextPosition != null) {
                    //System.out.println("POS : " + nextPosition.getPos() + " EVILPOS : " + nextPosition.getEvilPos()  + " Eval : " + nextPosition.getEvalValue() + "\n" + nextPosition.getHand());
                }
                tour = (tour+1)%2;
                
                if (nextPosition == null) {
                    continue; 
                }
                
                double reward = nextPosition.evaluate();
                qLearning.updateQValue(currentPosition, action, nextPosition, reward);
                currentPosition = nextPosition;
            }
            qLearning.decayExploration();
        }
    }
    

    private Position simulateAction(Position position, String action) {
        // Simuler l'application de l'action et retourner la nouvelle position
        String card = action.substring(0, action.length() - 1);
        String  direction = action.substring(action.length() - 1);
        Integer newPos = position.getBoard().move(position.getPos(), card, direction);
        
        if (newPos == null) {
            return null; 
        }
    
        Hand newHand = position.getHand().copy();
        newHand.remove(Integer.parseInt(card));
        newHand.giveNewCard();
        return new Position(newHand, position.getBoard().copy(), newPos, direction, Integer.parseInt(card));
    }
    

    private Position simulateEvilAction(Position position, String action) {
        String card = action.substring(0, action.length() - 1);
        String direction = action.substring(action.length() - 1);
        Integer newPos = position.getBoard().evilPlay(position.getEvilPos(), card, direction);
        
        if (newPos == null) {
            return null; 
        }
    
        position.setEvilPos(newPos);
        Hand newHand = position.getHand();
        newHand.evilRemove(Integer.parseInt(card));
        return position;
    }

    public boolean isTerminal(Position position) {
        if(position.getEvalValue() != 0 || position.getHand().getEvilCardNonNull() == 0){

            
            ////////////////////System.out.println("GAGNÉÉÉÉÉÉÉ : POS : " + position.getPos() + " EVILPOS : " + position.getEvilPos() + " Win : " + win + " Eval : " + position.getEvalValue() + "\n" + this.position.getHand());
            return  true;

        }
        return false;
    }

    public static void main(String[] args) {
        String pos0 = "5";
        String pos1 = "20";
        Random random = new Random();
        
        int victory = 0;
        Node rootNode = null;
        for (int i = 0; i < 2000; i++) {
            String[] playerHand = {Integer.toString(random.nextInt(4) + 1 ),
                Integer.toString(random.nextInt(4) + 1 ),
                Integer.toString(random.nextInt(4) + 1 ),
                Integer.toString(random.nextInt(4) + 1 ),
                Integer.toString(random.nextInt(4) + 1 )};
            
            
            Board board = new Board(pos1, pos0, "0", "0", "0", "0", "0", "0");
            Hand hand = new Hand(playerHand);
            Position initialPosition = new Position(hand, board);
            rootNode = new Node(initialPosition, true);
            

            // Entraîner le QLearning
            rootNode.trainQLearning(1);

            // Ajouter les nouvelles entrées et sauvegarder sans écraser l'ancienne QTable
            rootNode.qLearning.appendQTable(rootNode.qTableFilePath, rootNode.qLearning.qTable);
            rootNode.toString();

            System.out.println("TOUR ::: " + i);
        }
        
        
        
    }
    
    String selectBestAction(Position pos){
        return qLearning.selectBestAction(pos);
    }



}
