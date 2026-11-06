package Ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Définition de la classe Position
class Position {
    private Board board;
    private Double evalValue;
    private int distance;
    private Hand hand;
    private Integer pos;
    private Integer card;
    private String direction = "/";
    private Integer numberOfCard = null;
    private Integer evilPos;
    private String evilDirection = "/";
    private Integer evilNumberOfCard = null;

    public Position(Hand h, Board b) {
        this.hand = h;
        this.board = b;
        this.pos = b.getActuallyPos();
        this.getHand().initEvilCards();
        this.hand.getEvilCards();
        this.evilPos = this.board.getEvilPos();
        this.distance = this.board.getDistancePlayer() + 1;
        this.evaluate();
    }

    public Position(Hand h, Board b, int position) {
        this(h, b);
        this.pos = position;
    }

    public Position(Hand h, Board b, int position, String direction) {
        this(h, b, position);
        this.direction = direction;
    }

    public Position(Hand h, Board b, int position, String direction, int card) {
        this(h, b, position, direction);
        this.card = card;
    }

    public static Position newPos(Position pos, int newPos) {
        Position posi = new Position(pos.getHand(), pos.getBoard(), newPos, pos.getDirection(), pos.getCard());
        return posi;
    }

    public static Position newEvilPos(Position pos, int newPosition) {
        Position posi = new Position(pos.getHand(), pos.getBoard(), pos.getPos(), pos.getDirection(), pos.getCard());
        posi.setEvilPos(newPosition);
        return posi;
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

    public Double getEvalValue() {
        return evalValue;
    }

    public void setEvalValue(Double evalValue) {
        this.evalValue = evalValue;
    }

    public void setNumberOfCard(int num) {
        this.numberOfCard = num;
    }

    public Integer getNumberOfCard() {
        return this.numberOfCard;
    }

    public Integer getEvilPos() {
        return evilPos;
    }

    public boolean possibleAttack(String coup) {
        return this.hand.getNumeberOfCards(this.board.getDistancePlayer()) > 0;
    }

    public void setEvilDirection(String evilDirection) {
        this.evilDirection = evilDirection;
    }

    public Integer move(int position, String card, String direction) {
        Integer newPos = this.getBoard().move(position, card, direction);
        this.setPos(newPos);
        return newPos;
    }

    public Integer evilMove(int position, String card, String direction) {
        Integer newPos = this.getBoard().evilPlay(position, card, direction);
        if (newPos != null) {
            setEvilPos(newPos);
            return newPos;
        }
        return this.getEvilPos();
    }

    public void setPos(Integer pos) {
        this.pos = pos;
        this.getBoard().tp(pos);
    }

    public void setEvilPos(Integer evilPos) {
        this.evilPos = evilPos;
        this.getBoard().evilTp(evilPos);
    }

    public List<String> getEvilCards() {
        List<String> res = new ArrayList<>();
        String[] evilCards = this.getHand().getEvilCards();
        for (String evilCard : evilCards) {
            if (evilCard != null) {
                res.add(evilCard);
            }
        }
        return res;
    }

    public double evaluate() {
        double res = 0.0;
        res += 23.0 / this.distance;

        if ("F".equals(this.direction)) {
            res += 11.0;
        } else if ("B".equals(this.direction)) {
            res += 9.0;
        }

        if (this.canWin()) {
            res += 100000.0;
        }

        if (this.board.getDistanceWhithNegative() < 0) {
            res -= 1000.0;
        }
        this.setEvalValue(res);
        return res;
    }

    public boolean desavantage(int distance) {
        return this.getHand().getNumeberOfCards(distance) < this.getHand().getNumeberOfEvilCards(distance);
    }

    public int probaToWin(int distance) {
        int numberOfACardEvil = this.getHand().getNumeberOfEvilCards(distance);
        return (numberOfACardEvil / 5) * 100;
    }

    private boolean canWin() {
        int distance = this.getBoard().getDistancePlayer();
        return distance < 5 && this.getHand().getNumeberOfCards(distance) > 2;
    }

    public String toString() {
        return "EVAL : " + this.getEvalValue() + " CARD : " + this.getCard() + " DIRECTION : " + this.direction + " POS : " + this.pos;
    }

    public Position clone() {
        Position clone = new Position(this.hand.copy(), this.board.copy(), this.pos, this.direction, this.card);
        clone.setEvalValue(this.evalValue);
        clone.setNumberOfCard(this.numberOfCard);
        clone.setEvilPos(this.evilPos);
        return clone;
    }

    public boolean isTerminal() {
        if(this.getDirection().equals("A")) return true;
        return false;
    }

    public List<Position> getPossibleStates() {
        List<Position> possibleStates = new ArrayList<>();
        
        
        List<String> legalMoves = this.getAllPosition();
        
        
        for (String move : legalMoves) {
            Position newState = this.clone(); 
            newState.move(pos, move, direction); 
            possibleStates.add(newState);
        }
        
        return possibleStates;
    
        
    }


    public List<String> getAllPosition(){
        List<String> res = new ArrayList<>();

        System.out.println("HAND : " + hand.toString());
        String[] move = {"F", "B"};

        
        for (int i = 0; i < hand.size(); i++) {
            for (int j = 0; j < move.length; j++) {
                Integer newPos = this.board.getNewPos(this.pos, this.hand.get(i), move[j]);

                if(newPos != null) res.add("" + newPos);
            }
        }
        return res;
    }

    public double simulate() {
        // Cloner la position actuelle pour ne pas modifier l'état d'origine
        Random random = new Random();
        Position currentState = this.clone();
    
        // Simulation d'un jeu aléatoire jusqu'à ce qu'un état terminal soit atteint
        System.out.println("LA PARTIE N'EST PAS TERMINÉE");
        while (!currentState.isTerminal()) {
            // Obtenir tous les mouvements légaux possibles à partir de la position actuelle
            List<String> legalMoves = currentState.getAllPosition();
    
            // Vérifier s'il y a des mouvements possibles
            if (legalMoves.isEmpty()) {
                // S'il n'y a pas de mouvements possibles, considérez cet état comme terminal
                break;
            }
    
            // Choisir un mouvement aléatoire parmi les mouvements légaux
            String randomMove = legalMoves.get(random.nextInt(legalMoves.size()));
    
            // Appliquer le mouvement à la position actuelle
            currentState.move(currentState.getPos(), randomMove, currentState.getDirection());
    
            // Si la position actuelle est devenue terminale, sortir de la boucle de simulation
            if (currentState.isTerminal()) {
                break;
            }
    
            // Passer au joueur suivant en inversant le tour
            currentState = new Position(currentState.getHand(), currentState.getBoard(), currentState.getEvilPos());
        }
    
        // Une fois qu'un état terminal est atteint, retourner un résultat (par exemple, le score final)
        // Pour l'exemple, nous retournons simplement une valeur aléatoire entre 0 et 1
        return random.nextDouble();
    }
    
}
