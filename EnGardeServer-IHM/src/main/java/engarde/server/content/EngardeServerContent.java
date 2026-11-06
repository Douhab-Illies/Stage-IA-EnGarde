/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package engarde.server.content;

import engarde.game.EngardeGame;
import java.util.ArrayList;

/**
 *
 * @author ktajima
 */
public class EngardeServerContent {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        long seed = Long.parseLong("7632102757062152144");
        EngardeGame game = new EngardeGame(seed);
        printGameState(game);
        
        //デモンストレーション　１手目：プレイヤー０が一番大きいカードで前進
        int player = 0;
        forwardMax(player,game);
        
        //2手目
        player = 1;
        forwardMax(player,game);
        
        //3手目
        player = 0;
        forwardMax(player,game);
        
        //4手目
        player = 1;
        forwardMax(player,game);
        
        //5手目。プレイヤーが攻撃できるなら攻撃、できなければ移動。
        player = 0;
        if(game.checkPlayableCards(player, game.getTrackInformation()[EngardeGame.TRACK_INFORMATION_SPACE_OF_PLAYERS], 1)){
            attackMax(player,game);
        } else {
            //できないならまた前進
            forwardMax(player,game);
        }
        
        //5手目。プレイヤーが攻撃できるなら攻撃、できなければ移動。
        player = 1;
        if(game.checkPlayableCards(player, game.getTrackInformation()[EngardeGame.TRACK_INFORMATION_SPACE_OF_PLAYERS], 1)){
            attackMax(player,game);
        } else {
            //できないならまた前進
            forwardMax(player,game);
        }
        
        
    }

    /** 持てるだけのカードで攻撃 */
    public static void attackMax(int player,EngardeGame game){
        int space = game.getTrackInformation()[EngardeGame.TRACK_INFORMATION_SPACE_OF_PLAYERS];
        int num = 1;
        //そのカードの枚数を取得
        for(int i=1;i<=5;i++){
            if(game.checkPlayableCards(player, space, i)){
                num = i;
            }
        }
        game.attacktoOppnent(player, space, num);
        printGameState(game); 
    }
    
    /** 一番大きいカードで進む */
    public static void forwardMax(int player,EngardeGame game){
        int f = 0;
        for(int i=0;i<=5;i++){
            if(game.checkMoveableSwordman(player, i, EngardeGame.SWORDMAN_MOVE_FORWARD)){
                int cid = game.getHandofPlayer(player).indexOf(i);
                if(cid != -1){
                    f = cid;
                }
            }
        }
        System.out.println("Cid:"+f+" Number:"+game.getHandofPlayer(player).get(f));
        game.moveSwordman(player, f, EngardeGame.SWORDMAN_MOVE_FORWARD);
        printGameState(game);  
    }
    
    public static void printGameState(EngardeGame game){
        System.out.println("-----");
        System.out.println("GameStatus: " + game.getGameStatus());
        printCardListAll(game);
        printTrackStatus(game);
        System.out.println("-----");
        
    }
    
    public static void printCardListAll(EngardeGame game){
        System.out.print("player0:");
        printCardList(game.getHandofPlayer(0));
        System.out.print("player1:");
        printCardList(game.getHandofPlayer(1));
        System.out.print("Deck:");
        printCardList(game.getDeckCards());
        System.out.print("Used:");
        printCardList(game.getUsedCards());
    }
    
    public static void printTrackStatus(EngardeGame game){
        int[] info = game.getTrackInformation();
        int[] scores = game.getPlayerScores();
        System.out.println("Player 0 position:"+info[0]);
        System.out.println("Player 1 position:"+info[1]);
        System.out.println("Players space:"+info[2]);
        System.out.println("Players score:"+scores[0]+ " vs " + scores[1]);
        
    }
    
    public static void printCardList(ArrayList<Integer> hands){
        System.out.print("[");
        for(int i : hands){
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println("]");
    }
    
    
}
