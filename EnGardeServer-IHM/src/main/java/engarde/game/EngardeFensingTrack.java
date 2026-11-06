package engarde.game;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ktajima
 */
public class EngardeFensingTrack {
    private int trackSize;
    /** それぞれのプレイヤーの位置 */
    private int[] swordmanPlaces;
    /** それぞれのプレイヤーの点数 */
    private int[] playerScores;
    
    
    public EngardeFensingTrack(){
        this.trackSize = EngardeGame.defaultTrackSize;
        this.swordmanPlaces = new int[2];
        this.swordmanPlaces[0] = 1;
        this.swordmanPlaces[1] = EngardeGame.defaultTrackSize;
        this.playerScores = new int[2];
        this.playerScores[0] = 0;
        this.playerScores[0] = 0;
    }
    /** 強制的に位置を変えるメソッド */
    protected boolean setPlayerPlace(int[] places){
        if(places[1] > EngardeGame.defaultTrackSize){
            return false;
        }
        if(places[0] < places[1]){
            return false;
        }
        this.swordmanPlaces = places;
        return true;    
    }
    
    protected int[] getScores(){
        return this.playerScores;
    }
    protected void setScore(int[] sc){
        this.playerScores[0] = sc[0];
        this.playerScores[1] = sc[1];
    }
    
    
    public boolean forwardSwordman(int playerID,int step){
        if(playerID == 0){
            this.swordmanPlaces[0] += step;
            return true;    
        } else if(playerID == 1){
            this.swordmanPlaces[1] -= step;
            return true;    
        }
        return false;    
    }
    
    public boolean backwardSwordman(int playerID,int step){
        if(playerID == 0){
            this.swordmanPlaces[0] -= step;
            return true;    
        } else if(playerID == 1){
            this.swordmanPlaces[1] += step;
            return true;    
        }
        return false;    
    }
    
    /** プレイヤー間の間合いを返す：この値がカード値と一致すると攻撃可能 */
    public int getSpacesofPlayers(){
        return this.swordmanPlaces[1] - this.swordmanPlaces[0];
    }
    
    /** プレイヤーの現在の位置を返す */
    public int getPlaceofPlayer(int playerID){
        return this.swordmanPlaces[playerID];
    }

    public boolean checkMoveableSwordman(int playerID,int step,int way){
        if(step < 1){
            return false;
        }
        if(step > 5){
            return false;
        }
        
        if(way == EngardeGame.SWORDMAN_MOVE_FORWARD){
            if(this.getSpacesofPlayers() > step){
                return true;
            } else {
                //追い越しはできない
                return false;
            }
        } else if(playerID == 0 && way == EngardeGame.SWORDMAN_MOVE_BACKWARD){
            //1番プレイヤーは0までしか下がれない
            if(this.swordmanPlaces[playerID] - step < 1){
                return false;
            }
            return true;
        } else if(playerID == 1 && way == EngardeGame.SWORDMAN_MOVE_BACKWARD){
            //1番プレイヤーはボードの最大マスまでしか下がれない
            if(this.trackSize < this.swordmanPlaces[playerID] + step ){
                return false;
            }
            return true;
        }
        return false;
    }
    
    
}
