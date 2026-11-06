package Ai;

import java.util.ArrayList;
import java.util.List;

public class Minimax {

    public static Node minimax(Node node, int depth, boolean maximizingPlayer) {
        if (depth == 0 || node.getChildren().isEmpty()) {
            return node;
        }
    
        Node bestMove = null;
        double bestEval = maximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
    
        for (Node child : node.getChildren()) {
            Node evalNode = minimax(child, depth - 1, !maximizingPlayer);
            double eval = evalNode.getPosition().getEvalValue();
    
            if ((maximizingPlayer && eval > bestEval) || (!maximizingPlayer && eval < bestEval)) {
                bestEval = eval;
                bestMove = evalNode;
            }
        }
    
        return bestMove;
    }

    public static Node getBestMove(Node node, int depth, boolean maximizingPlayer) {
        double bestEval = maximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Node bestMove = null;
    
        for (Node child : node.getChildren()) {
            Node evalNode = minimax(child, depth - 1, !maximizingPlayer);
            double eval = evalNode.getPosition().getEvalValue();
    
            if ((maximizingPlayer && eval > bestEval) || (!maximizingPlayer && eval < bestEval)) {
                bestEval = eval;
                bestMove = evalNode;
            }
        }
    
        return bestMove;
    }
    

    public static void main(String[] args) {
        Board board = new Board("15", "10", "0", "0", "0", "0", "0", "0");
        String[] main = {"1", "1", "1", "1", "2"};
        Hand hand = new Hand(main);
        Position initialPosition = new Position(hand, board);

        Node rootNode = new Node(initialPosition, true); // Player starts
        
        board.printBoard();


        int numTurns = 10; 
        int depth = 10; 

        Node bestMove = null;
        List<Position> cardToPlay = new ArrayList<>();
        for (int turn = 0; turn < numTurns; turn++) {
            if (rootNode == null){
                break;
            }
            Node.distance = board.getDistancePlayer();
            rootNode.generateChildren();
            bestMove = Minimax.getBestMove(rootNode, depth, true);
            rootNode = bestMove;

            if (bestMove != null) {
                System.out.println("Tour " + (turn + 1) + ": Meilleur coup trouvé : " + bestMove.getPosition().getCard());
                cardToPlay.add(bestMove.getPosition());
            } else {
                System.out.println("Tour " + (turn + 1) + ": Aucun coup trouvé.");
            }
        }

        for (Position pos : cardToPlay) {
            Board b = new Board(Integer.toString(pos.getPos()), Integer.toString(pos.getEvilPos()), "15", "1", "0", "0", "0", "0");
            b.printBoard();
            System.out.println(pos.getCard() + " " + pos.getDirection() + " " + pos.getEvalValue());
        }
    }



}
