package Ai;

public class TreeGenerator {

    public static void generateTree(Node rootNode, int depth) {
        if (depth == 0) {
            return; // Stop generation when max depth is reached
        }

        rootNode.generateChildren(); // Generate children for the current node

        for (Node child : rootNode.getChildren()) {
            generateTree(child, depth - 1); // Recursively generate for each child
        }
    }

    public static void printTree(Node rootNode, int depth) {
        printNode(rootNode, depth, 0);
    }

    private static void printNode(Node node, int maxDepth, int currentDepth) {
        System.out.println(getIndent(currentDepth) + node); // Print current node

        if (currentDepth < maxDepth) {
            for (Node child : node.getChildren()) {
                printNode(child, maxDepth, currentDepth + 1); // Recursively print for each child
            }
        }
    }

    private static String getIndent(int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("     "); // Add spaces for each level of depth
        }
        return indent.toString();
    }

    public static void main(String[] args) {
        // Create tree with a maximum depth of 3
        Board board = new Board("20", "13", "4", "1", "0", "0", "0", "0");
        String[] main = {"4", "4", "3", "1", "1"};
        Hand hand = new Hand(main);
        Position initialPosition = new Position(hand, board);
        Node rootNode = new Node(initialPosition, true); // Player starts
        board.printBoard();
        generateTree(rootNode, 2);
        printTree(rootNode, 2);
    }
}
