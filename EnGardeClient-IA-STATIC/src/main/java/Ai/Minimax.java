package Ai;

import java.util.ArrayList;
import java.util.List;

public class Minimax {

    public int miniMax(Node node, int depth, boolean maximizingPlayer) {
       
        if (depth == 0 || node.getChildren().isEmpty()) {
            System.out.println("J'EVALUATE " + node.getPosition().pos + " " + evaluate(node.getPosition()));
            return evaluate(node.getPosition());
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Node child : node.getChildren()) {
                int eval = miniMax(child, depth - 1, false);
                maxEval = Math.max(maxEval, eval);
            }
            System.out.println("J'EVALUATE MAX" + node.getPosition().pos + " " + evaluate(node.getPosition()));
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Node child : node.getChildren()) {
                int eval = miniMax(child, depth - 1, true);
                minEval = Math.min(minEval, eval);
            }
            System.out.println("J'EVALUATE MIN" + node.getPosition().pos + " " + evaluate(node.getPosition()));

            return minEval;
        }
    }

    public Node findBestMove(Node rootNode, int depth) {
        int bestValue = Integer.MIN_VALUE;
        Node bestMove = null;
        for (Node child : rootNode.getChildren()) {
            int value = miniMax(child, depth - 1, false);
            if (value > bestValue) {
                bestValue = value;
                bestMove = child;
            }
        }
        return bestMove;
    }

    public boolean isGameOver(Position position){
        return position.hand.getCountCard(position.board.getDistancePlayer()) > 0;
    }

    private int evaluate(Position position) {
        return position.pos;

    }

    public static void main(String[] args) {
        Board b = new Board("23", "1", "15", "0", "0", "0", "0", "0");
        b.printBoard();
        
        String[] main = {"3", "2", "3", "2", "5"};
        Hand h = new Hand(main);

        Position p = new Position(h, b);
        Node rootNode = new Node(p);
        rootNode.generateChildren();

        Minimax minimax = new Minimax();
        System.out.println(minimax.miniMax(rootNode, 3, true));
    }
}
