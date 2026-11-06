package Ai;

public class TreeBuilder {
    public static void main(String[] args) {
        // Créer un jeu de test avec une main de cartes
        Board board = new Board("23", "1", "15", "0", "0", "0", "0", "0");
        board.printBoard();
        
        String[] main = {"5", "4", "3", "2", "1"};
        Hand hand = new Hand(main);

        // Créer une position initiale
        Position initialPosition = new Position(hand, board);

        // Créer un nœud racine
        Node rootNode = new Node(initialPosition);

        // Générer l'arbre
        buildTree(rootNode, 5); // Profondeur maximale de l'arbre

        // Afficher l'arbre
        printTree(rootNode, 0);
    }

    // Méthode récursive pour construire l'arbre jusqu'à une certaine profondeur
    private static void buildTree(Node node, int depth) {
        if (depth == 0) {
            return;
        }
        node.generateChildren();
        for (Node child : node.getChildren()) {
            buildTree(child, depth - 1);
        }
    }

    // Méthode récursive pour afficher l'arbre
    private static void printTree(Node node, int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("    "); // Espaces pour l'indentation
        }
        System.out.println(indent.toString() + node.toString());
        for (Node child : node.getChildren()) {
            printTree(child, depth + 1);
        }
    }


}



