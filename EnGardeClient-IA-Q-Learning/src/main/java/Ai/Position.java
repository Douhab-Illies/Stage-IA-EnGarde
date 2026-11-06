package Ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// Définition de la classe Position
public class Position {
    private Board board;
    private Double evalValue;
    int distance;

    private Hand hand;
    private Integer pos;
    private Integer card;
    private String direction = "/";
    private Integer numberOfCard = null;

    private Integer evilPos;
    private String evilDirection = "/";
    private Integer evilNumberOfCard = null;


    
    public Position(Hand h, Board b){
        this.hand = h;
        this.board = b;
        this.pos = b.getActuallyPos();
        this.getHand().initEvilCards();
        this.hand.getEvilCards();
        this.evilPos = this.board.getEvilPos();
        this.distance = this.board.getDistancePlayer() + 1;
        this.evaluate();
        
        
    }
    public Position(Hand h, Board b,int position){
        this(h, b);
        this.pos = position;
        
    }

    public Position(Hand h, Board b, int position, String direction ){
        this(h, b, position);
        this.direction = direction;
    }
    
    public Position(Hand h, Board b, int position, String direction, int card){
        this(h, b,position , direction);
        this.card = card;

        
    }

    public static Position newPos(Position pos, int newPos){
        Position posi = new Position(pos.getHand(), pos.getBoard(), newPos, pos.getDirection(), pos.getCard());
    
        return posi;
    }

    public static Position newEvilPos(Position pos, int newPosition){
        Position posi = new Position(pos.getHand(), pos.getBoard(), pos.getPos(), pos.getDirection(), pos.getCard());
        posi.setEvilPos(newPosition);

        return posi;
    }
    
    public Board getBoard() {return board;}
    public void setBoard(Board board) {this.board = board;}
    public Hand getHand() {return hand;}
    public void setHand(Hand hand) {this.hand = hand;}
    public Integer getPos() {return pos;}
    public Integer getCard() {return card;}
    public void setCard(Integer card) {this.card = card;}
    public String getDirection() {return direction;}
    public void setDirection(String direction) {this.direction = direction;}
    public Double getEvalValue() {return evalValue;}
    public void setEvalValue(Double evalValue) {
        this.evalValue = evalValue;
        /*if (evalValue > 10000.0) {
            this.direction = "A";
            this.card = this.distance;
            this.numberOfCard = this.hand.getNumeberOfCards(this.distance);
            
        }*/
    
    }
    public void setNumberOfCard(int num){this.numberOfCard = num;}
    public Integer getNumberOfCard(){return this.numberOfCard;}
    //public Integer getNewPos(String coup, String direction){return this.getBoard().getNewPos(coup, direction);}
    public Integer getEvilPos() {return evilPos;}
    
    
    public boolean possibleAttack(String coup){
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
        
        
        // res += 23.0 / this.distance;

        // if ("F".equals(this.direction)) {
        //     res += 17;
        // } else if ("B".equals(this.direction)) {
        //     res += 10.0;
        // }

        if (this.canLoose()) {
            res = res - 10.0;
            System.out.println("PERDUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUU");
        }


        if (this.canWin()) {
            res += 10.0;
        }

        if (this.board.getDistanceWhithNegative() < 0) {
            res -= 1000.0;
        }

        System.out.println("RESSSSSS : " + res);

        
        this.setEvalValue(res);
        return res;
    }



    public boolean canLoose() {
        int distance = this.getBoard().getDistancePlayer();
        return distance < 5 && this.getHand().getNumeberOfEvilCards(distance) >= 2;
    }

    private boolean canWin() {
        int distance = this.getBoard().getDistancePlayer();
        return distance < 5 && this.getHand().getNumeberOfCards(distance) >= 2 ;
    }
    
    public boolean desavantage(int distance){
        return this.getHand().getNumeberOfCards(distance) < this.getHand().getNumeberOfEvilCards(distance);
    }

    public int probaToWin(int distance){
        int numberOfACardEvil = this.getHand().getNumeberOfEvilCards(distance);
        return (numberOfACardEvil/5)*100;
    }

    
    



    public List<String> getEvilPossibleActions() {
        List<String> actions = new ArrayList<>();
        for (int i = 0; i < hand.evilSize(); i++) {
            if (hand.getEvil(i) != null) {
                for (String move : new String[]{"F", "B"}) {
                    actions.add(hand.getEvil(i) + move);
                }
            }
        }
        return actions;
    }


    

    public String toString(){
        return "POS : " + pos + " EVILPOS : " + evilPos + " " + this.getHand().toStringOrder();
    }


    

    

    public List<String> getPossibleActions() {
        List<String> actions = new ArrayList<>();
        
        // Pour chaque carte dans la main
        for (int i = 0; i < hand.size(); i++) {
            String card = hand.get(i);
            if (card != null) {
                for (String direction : new String[]{"F", "B"}) {
                    // Créer l'action (par ex., "1F" ou "2B")
                    String action = card + direction;
                    // Simuler le mouvement pour obtenir la nouvelle position
                    Integer newPos = board.getNewPos(pos, card, direction);
                    // Ajouter l'action à la liste si le mouvement est valide (newPos n'est pas null)
                    if (newPos != null) {
                        actions.add(action);
                    }
                }
            }
        }
        return actions;
    }


    public List<String> getAllEvilPossibleActions() {
        List<String> actions = new ArrayList<>();
        
        for (int i = 0; i < hand.evilSize(); i++) {
            String card = hand.getEvil(i);
            if (card != null) {
                for (String direction : new String[]{"F", "B"}) {
                    String action = card + direction;
                    Integer newPos = board.getEvilNewPos(evilPos, card, direction);
                    if (newPos != null) {
                        actions.add(action);
                    }
                }
            }
        }
        return actions;
    }


    public static void main(String[] args) {
                String pos0 = "5";
        String pos1 = "20";
        Random random = new Random();
        
        int victory = 0;
        Node rootNode = null;
        String[] playerHand = {Integer.toString(random.nextInt(4) + 1 ),
            Integer.toString(random.nextInt(4) + 1 ),
            Integer.toString(random.nextInt(4) + 1 ),
            Integer.toString(random.nextInt(4) + 1 ),
            Integer.toString(random.nextInt(4) + 1 )};
        
        
        Board board = new Board(pos1, pos0, "0", "0", "0", "0", "0", "0");
        Hand hand = new Hand(playerHand);
        Position initialPosition = new Position(hand, board);


        System.out.println(initialPosition);
    }
}