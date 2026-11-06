package Ai;
import javax.net.ssl.SNIHostName;
import javax.swing.*;

import org.w3c.dom.Node;

import java.net.Socket;
import java.nio.file.AtomicMoveNotSupportedException;
import java.security.cert.CertPathValidatorException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class tmp {
    public static int minimax(Board board, Hand hand, int depth, boolean isMaximizingPlayer, ArrayList<Integer> possibleMoves) {
        if (depth <= 0) {
            int player;
            if (isMaximizingPlayer) {
                player = 1;
            } else {
                player = 0;
            }
            System.out.println("j'appelle evaluate");
            evaluate(player, board, hand);
        } else if (isMaximizingPlayer) {
            int bestValue = Integer.MIN_VALUE;
            for (int move : possibleMoves) {
                // Appliquer le mouvement sur le plateau et dans la main
                // Board newBoard = board.applyMove(move);
                // Hand newHand = hand.applyMove(move);
                System.out.println("j'essaie d'appeller minimax avec comme paramètres:");
                System.out.println(board);
                System.out.println(hand);
                System.out.println(depth);
                System.out.println(possibleMoves);
                int value = minimax(board, hand, depth - 1, false, possibleMoves);
                bestValue = Math.max(bestValue, value);
            }
            return bestValue;
        } else {
            int bestValue = Integer.MAX_VALUE;
            for (int move : possibleMoves) {
                // Appliquer le mouvement sur le plateau et dans la main
                // Board newBoard = board.applyMove(move);
                // Hand newHand = hand.applyMove(move);

                int value = minimax(board, hand, depth - 1, true, possibleMoves);
                bestValue = Math.min(bestValue, value);
            }
            return bestValue;
        }
        return depth;
    }

    public static ArrayList<Move> evaluate(int player, Board board, Hand hand) {
        //TODO juste évaluer si il y a victoire ou défaite et renvoyer ces scores pour définir les premiers scores puis créer l'arbre depuis ces valeurs
        String[] main = hand.getHand();
        ArrayList<Move> nodes = new ArrayList<>();
        int distancepions = board.getDistancePlayer();
        for (int x = 0; x < main.length; x++) {
            if (distancepions == Integer.parseInt(main[x])) {
                nodes.add(new Move(main.length, Collections.frequency( convertStringArrayToIntList(main), Integer.parseInt(main[x]))));
            }
            if (distancepions - Integer.parseInt(main[x]) > 1) {
                nodes.add(new Move(Integer.parseInt(main[x]), "F"));
            }
            if (player == 0) {
                if (Integer.parseInt(board.getPlayerPosition_0()) - Integer.parseInt(main[x]) >= 0) {
                    nodes.add(new Move(Integer.parseInt(main[x]), "B"));
                }
            } else if (player == 1) {
                if (Integer.parseInt(board.getPlayerPosition_1()) + Integer.parseInt(main[x]) <= 23) {
                    nodes.add(new Move(Integer.parseInt(main[x]), "B"));
                }
            }
        }
        System.out.println("mes moves sont");
        for(int x= 0;x<nodes.size();x++){
            System.out.println(nodes.get(x).getinfos());
        }
        return nodes;
    }


	public static List<Integer> convertStringArrayToIntList(String[] strings) {
        List<Integer> integers = new ArrayList<>();

        for (String str : strings) {
            try {
                int num = Integer.parseInt(str);
                integers.add(num);
            } catch (NumberFormatException e) {
                // Gérer les erreurs de conversion ici si nécessaire
                System.err.println("Erreur de conversion pour la chaîne : " + str);
            }
        }

        return integers;
    }

    /*public static void testMinimax() {
        // Créer un objet Board et un objet Hand pour tester
        Board board = new Board(22, 23, 15);
        Hand hand = new Hand(5, 1, 2, 3, 4);

        // Créer une liste de mouvements possibles pour tester
        ArrayList<Integer> possibleMoves = new ArrayList<>();
        possibleMoves.add(1);
        possibleMoves.add(2);
        possibleMoves.add(3);
        possibleMoves.add(4);

        // Appeler la méthode printTree pour afficher l'arbre de recherche
        printTree(board, hand, 3, true, possibleMoves);
    }
*/
    //simulate a displacement on the board to create nodes on the tree
    public static Board displacement(Move move, Board board, int playerid){
        //TODO penser à l'appel de cette fonction à retirer de la main de la personne la carte jouée après dans le noeud suivant
        if (move.getType().equals("F")){
            if(playerid == 0){
                board.setPosition0(board.getPosition0()+move.card);
            } else if (playerid == 1) {
                board.setPosition1(board.getPosition1()-move.card);}
        } else if (move.getType().equals("B")) {
            if(playerid == 0){
                board.setPosition0(board.getPosition0()-move.card);
            } else if (playerid == 1) {
                board.setPosition1(board.getPosition0()+ move.card);}
        }
        return board;
    }

    public static boolean attack(int playerid, Hand playerhand, ArrayList<Integer> opponentHand, Board board){
        int distancepions = Math.abs(board.getPosition0()-board.getPosition1());
        ArrayList<Integer> listplayerhand = playerhand.getallhandlist();
        if(playerid == 0){
            //we compare the number of cards usable to attack for each player and we determine wich player wins the duel;
            if (Collections.frequency(listplayerhand,distancepions)>Collections.frequency(opponentHand,distancepions)){
                return true;//the player has more cards and dominate the opponent
            }
            else{
                //the opponent has more cards or the same amount as the player and can parry the attack
                return false;
            }
        }
        //now we take the case when the opponent attack our player
        else{
            if (Collections.frequency(opponentHand,distancepions)>Collections.frequency(listplayerhand,distancepions)){
                return true;//the opponent has more cards and win the attack
            }
            else{
                //the player has more cards or the same amount as the opponent and can parry the attack
                return false;
            }
        }
    }

    //FIX  problème avec la récursivité ,
    /*
    je suis un noeud profondeur 2775
distance pions est de 2
je suis un noeud profondeur 2776
distance pions est de 2
je suis un noeud profondeur 2777
distance pions est de 2
je suis un noeud profondeur 2778
distance pions est de 2
je suis un noeud profondeur 2779
distance pions est de 2
Exception in thread "main" java.lang.StackOverflowError
	at java.base/java.lang.System$2.encodeASCII(System.java:2506)
	at java.base/sun.nio.cs.UTF_8$Encoder.encodeArrayLoop(UTF_8.java:456)
	at java.base/sun.nio.cs.UTF_8$Encoder.encodeLoop(UTF_8.java:564)
	at java.base/java.nio.charset.CharsetEncoder.encode(CharsetEncoder.java:586)
	at java.base/sun.nio.cs.StreamEncoder.implWrite(StreamEncoder.java:370)
	at java.base/sun.nio.cs.StreamEncoder.implWrite(StreamEncoder.java:357)
	at java.base/sun.nio.cs.StreamEncoder.lockedWrite(StreamEncoder.java:158)

     */
public static Node minimaxtree(int playerid, Board board, Hand hand, ArrayList<Integer> opponenthand,int depth){
        Node node = new Node();

        System.out.println("je suis un noeud profondeur "+depth);
        ArrayList<Move> moves = evaluate(playerid,board,hand);
        if(moves.size() == 0){
            node.setValue(-100);
        }
        for(Move move : moves){
            if (move.type.equals("F") || move.type.equals("B")){
                board = displacement(move,board,playerid);
                hand.removecard(move.getCard());
                node.addson(minimaxtree(Math.abs(playerid-1),board,hand,opponenthand,depth+1));
            }
            else if (move.type.equals("A")){
                if (attack(playerid,hand,opponenthand,board)){
                    node.setValue(100);
                }
                else if(!attack(playerid,hand,opponenthand,board)){
                    node.setValue(-100);
                }
            }

        }
        if (node.getSons().size() >0){
            if(playerid == 0){
                System.out.println(node.getSons());
                node.setValue(Collections.min(node.getsonsvalue()));
            } else {
                node.setValue(Collections.max(node.getsonsvalue()));
            }
        }
        return node;
    }
//main only here to run functions independently;
public static void main(String[] args) {
    try {

        ArrayList<Integer> liste = new ArrayList<>();
        liste.add(1);
        liste.add(1);
        liste.add(1);
        liste.add(1);
        liste.add(2);
        liste.add(2);
        liste.add(2);
        liste.add(2);
        liste.add(3);
        liste.add(3);
        liste.add(3);
        liste.add(3);
        liste.add(4);
        liste.add(4);
        liste.add(4);
        liste.add(4);
        liste.add(5);
        liste.add(5);
        liste.add(5);
        liste.add(5);
        evaluate(0,new Board(0,7,25),new Hand(1,2,3,4,5));
        Node node = minimaxtree(0, new Board(0, 7, 25), new Hand(1, 2, 3, 4, 5), liste, 0);
    } catch (Exception e) {
        e.printStackTrace();
    }
        /*TODO faire en sorte de lancer la fonction avec des paramètres de base et simuler une partie en effectuant toutes les actions
             possibles par les deux joueurs en supposant qu'on ne repioche pas , on lance donc une fausse partie en supposant que le joueur 1 a ses cartes et l'autre joueur
             le reste des cartes qui n'ont pas été utilisées
             sur le papier
             on utilise evaluation , on fait tout récursivement et on a comme condition d'arrêt pour la branche l'attaque car on termine forcément la partie dessus
             une fois qu'on a tout les noeuds avec evaluation on simule les mouvements et boucle our avoir tout les fils puis
             on recommence la fonction de manière récursive pour obtenir
             la génération suivante
         */

}




}
