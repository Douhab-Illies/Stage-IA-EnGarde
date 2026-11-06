package Ai;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QLearning {
    public Map<String, Double> qTable;
    private double learningRate;
    private double discountFactor;
    private double explorationRate;
    private double explorationDecay;
    private Random random;

    public static int cpt = 0;
    public QLearning(double learningRate, double discountFactor, double explorationRate, double explorationDecay) {
        this.qTable = new HashMap<>();
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.explorationRate = explorationRate;
        this.explorationDecay = explorationDecay;
        this.random = new Random();
    }

    public static String getStateActionKey(Position position, String action) {
        return position.toString() + "_" + action;
    }

    public void updateQValue(Position currentState, String action, Position nextState, double reward) {
        String key = getStateActionKey(currentState, action);
        double oldQValue = qTable.getOrDefault(key, 0.0);
        double maxFutureQ = nextState.getPossibleActions().stream()
                .mapToDouble(a -> qTable.getOrDefault(getStateActionKey(nextState, a), 0.0))
                .max()
                .orElse(0.0);
        double newQValue = oldQValue + learningRate * (reward + discountFactor * maxFutureQ - oldQValue);
        qTable.put(key, newQValue);
    }


    public String selectAction(Position position) {
        List<String> possibleActions = position.getPossibleActions();
        if (possibleActions.isEmpty()) {
            return null;
        }

        if (random.nextDouble() < explorationRate) {
            // Exploration
            return possibleActions.get(random.nextInt(possibleActions.size()));
        } else {
            // Exploitation
            return possibleActions.stream()
                    .max((a1, a2) -> Double.compare(
                            qTable.getOrDefault(getStateActionKey(position, a1), 0.0),
                            qTable.getOrDefault(getStateActionKey(position, a2), 0.0)))
                    .orElse(possibleActions.get(0));
        }
    }


    public String selectEvilAction(Position position) {
        List<String> possibleActions = position.getAllEvilPossibleActions();
        if (possibleActions.isEmpty()) {
            return null;
        }
        String res = null;

        Random random = new Random();
        String direction = null;
        String card = null;
        Integer newPos = null;
        while (newPos == null) {
            
            direction = random.nextBoolean() ? "F" : "B";
            card = position.getHand().getRandomEvilGand();
            newPos = position.getBoard().getEvilNewPos(position.getBoard().getEvilPos(),card , direction);

        }
        res = "" + card + direction;

        return res;
    }




    

    public void appendQTable(String filePath, Map<String, Double> newEntries) {
        // Charger l'ancienne QTable
        loadQTable(filePath);
    
        // Ajouter les nouvelles entrées
        if (qTable == null) {
            qTable = new HashMap<>();
        }
        qTable.putAll(newEntries);
    
        // Sauvegarder la QTable mise à jour
        saveQTable(filePath);
    }
    



    public void decayExploration() {
        explorationRate *= explorationDecay;
    }


    public void loadQTable(String filePath) {
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            qTable = (HashMap<String, Double>) objectIn.readObject();
            objectIn.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            qTable = new HashMap<>(); // Initialiser qTable si le fichier n'existe pas
            e.printStackTrace();
        }
    }
    

    // Méthode pour sauvegarder la QTable dans un fichier
    public void saveQTable(String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(qTable);
            objectOut.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String selectBestAction(Position position) {
        List<String> possibleActions = position.getPossibleActions();
        if (possibleActions.isEmpty()) {
            return null;
        }
    
        String bestAction = possibleActions.get(0); // Sélectionnez la première action par défaut
        double bestQValue = Double.NEGATIVE_INFINITY;
    
        for (String action : possibleActions) {
            double qValue = qTable.getOrDefault(getStateActionKey(position, action), 0.0);
            if (qValue > bestQValue) {
                bestQValue = qValue;
                bestAction = action;
            }
        }
        System.out.println("BEST ACTION QVALUE : " + bestQValue);
        return bestAction;
    }
    

    public void displaySize() {
        System.out.println(qTable.size());
    }

    public static void main(String[] args) {
        QLearning qLearning = new QLearning(0.1, 0.9, 0.1, 0.99);
        qLearning.loadQTable("qtable.ser");

        qLearning.displaySize();
        qLearning.qTable.forEach((key, value) -> {
            if (value > 0.0) {
                System.out.println(key + " -> " + value);
                
            }
        });

    }

}
