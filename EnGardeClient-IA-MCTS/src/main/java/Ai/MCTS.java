package Ai;

import java.util.Random;

public class MCTS {
    private static final int SIMULATIONS_PER_TURN = 100;
    private Node rootNode;
    private Random random;

    public MCTS(Node rootNode) {
        this.rootNode = rootNode;
        this.random = new Random();
    }

    public Node findNextMove() {
        for (int i = 0; i < SIMULATIONS_PER_TURN; i++) {
            Node nodeToExplore = selectNodeToExplore(rootNode);
            double value = simulatePlayout(nodeToExplore);
            backPropagation(nodeToExplore, value);
        }
        return rootNode.getChildWithMaxVisits();
    }

    private Node selectNodeToExplore(Node rootNode) {
        Node node = rootNode;
        while (!node.isTerminal()) {
            if (!node.isFullyExpanded()) {
                node.generateChildren(); // Générer les enfants du nœud avant de l'explorer
                return node;
            } else {
                double parentVisits = node.getVisits();
                node = node.getChildWithMaxUctValue();
            }
        }
        return node;
    }
    
    
    private double simulatePlayout(Node node) {
        
        Node currentNode = node;
        while (!currentNode.isTerminal()) {
            if (currentNode.isFullyExpanded()) {
                currentNode = currentNode.getChildWithMaxUctValue();
            } else {
                currentNode = currentNode.getRandomChild();
            }
            if (currentNode == null) {
                // Gérer le cas où currentNode devient null
                break;
            }
        }
        if (currentNode != null) {
            return currentNode.simulate();
        } else {
            // Gérer le cas où currentNode est null
            return 0.0; // Valeur arbitraire
        }
    }
    

    private void backPropagation(Node node, double value) {
        while (node != null) {
            node.incrementVisits();
            node.addValue(value);
            node = node.getParent();
        }
    }

    public static void main(String[] args) {
        // Créez votre position initiale
        Board board = new Board("23", "1", "0", "0", "0", "0", "0", "0");
        String[] main = {"5", "4", "3", "2", "1"};
        Hand hand = new Hand(main);
        Position initialPosition = new Position(hand, board);
        
        // Créez le nœud racine
        Node rootNode = new Node(initialPosition, true);

        // Créez une instance de MCTS avec le nœud racine
        MCTS mcts = new MCTS(rootNode);

        // Trouvez le prochain meilleur coup en utilisant MCTS
        Node bestMove = mcts.findNextMove();

        // Affichez le meilleur coup
        System.out.println("Best Move: " + bestMove);
    }
}
