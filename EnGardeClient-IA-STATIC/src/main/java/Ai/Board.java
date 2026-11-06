package Ai;

import java.util.HashMap;

public class Board {
    public String PlayerPosition_1;
    public String PlayerPosition_0;
    public String NumofDeck;
    public String CurrentPlayer;
    public String From;
    public String To;
    public String PlayerScore_0;
    public String PlayerScore_1;

	public final static int MAX_DISTANCE = 23;

	public boolean estPartieTerminee = false;
	
    public char[] board;

	public Board(String playerPosition_1, String playerPosition_0, String numofDeck, String currentPlayer,
            String from, String to, String playerScore_0, String playerScore_1) {
        PlayerPosition_1 = playerPosition_1;
        PlayerPosition_0 = playerPosition_0;
        NumofDeck = numofDeck;
        CurrentPlayer = currentPlayer;
        From = from;
        To = to;
        PlayerScore_0 = playerScore_0;
        PlayerScore_1 = playerScore_1;
    }

    public Board (HashMap<String,String> data){
        PlayerPosition_1 = data.get("PlayerPosition_1");
        PlayerPosition_0 = data.get("PlayerPosition_0");
        NumofDeck = data.get("NumofDeck");
        CurrentPlayer = data.get("CurrentPlayer");
        From = data.get("From");
        To = data.get("To");
        PlayerScore_0 = data.get("PlayerScore_0");
        PlayerScore_1 = data.get("PlayerScore_1");
    }

    public Board(){

    }

	public Board(char[] board){
		
	}




    public void printBoard(){
        //System.out.println("Turn of : " + this.CurrentPlayer);
        this.board = new char[23];
        for (int i = 0; i < board.length; i++) {
            this.board[i] = ' ';
        }

        this.board[Integer.parseInt(PlayerPosition_0) - 1] = 'W';
        this.board[Integer.parseInt(PlayerPosition_1) - 1] = 'B';

        System.out.print("|");
        for (int i = 0; i < board.length; i++) {
            System.out.print(this.board[i] + "|");
        }
        System.out.println();
    }

	public char getCase(int i){
		return this.board[i];
	}

	public boolean estPartieTerminee(){
		return this.estPartieTerminee;
	}
    
    
    public String getPlayerPosition_1() {
		return PlayerPosition_1;
	}
	public void setPlayerPosition_1(String playerPosition_1) {
		PlayerPosition_1 = playerPosition_1;
	}
	public String getPlayerPosition_0() {
		return PlayerPosition_0;
	}
	public void setPlayerPosition_0(String playerPosition_0) {
		PlayerPosition_0 = playerPosition_0;
	}
	public String getNumofDeck() {
		return NumofDeck;
	}
	public void setNumofDeck(String numofDeck) {
		NumofDeck = numofDeck;
	}
	public String getCurrentPlayer() {
		return CurrentPlayer;
	}
	public void setCurrentPlayer(String currentPlayer) {
		CurrentPlayer = currentPlayer;
	}
	public String getFrom() {
		return From;
	}
	public void setFrom(String from) {
		From = from;
	}
	public String getTo() {
		return To;
	}
	public void setTo(String to) {
		To = to;
	}
	public String getPlayerScore_0() {
		return PlayerScore_0;
	}
	public void setPlayerScore_0(String playerScore_0) {
		PlayerScore_0 = playerScore_0;
	}
	public String getPlayerScore_1() {
		return PlayerScore_1;
	}
	public void setPlayerScore_1(String playerScore_1) {
		PlayerScore_1 = playerScore_1;
	}

	public int getDistancePlayer(){
		return Math.abs(Integer.parseInt(PlayerPosition_0) - Integer.parseInt(PlayerPosition_1));
	}

	public int getDistanceWithWall(){
		if(CurrentPlayer.equals("0")){
			return Math.min(Integer.parseInt(PlayerPosition_0), 23 - Integer.parseInt(PlayerPosition_0));
		}
		else{
			return Math.min(Integer.parseInt(PlayerPosition_1), 23 - Integer.parseInt(PlayerPosition_1));
		}	
	} 

	/*
	public Integer getNewPos(String coup, String direction){
		char[] tmp = this.board;
		Integer newPos = 0;
		if(this.CurrentPlayer.equals("1")){
			if(direction.equals("B")){
				newPos = Integer.parseInt(this.PlayerPosition_0) - Integer.parseInt(coup);
				if (newPos > 0 && newPos < 24) {
					return newPos;
				}
			}else{
				newPos = Integer.parseInt(this.PlayerPosition_0) + Integer.parseInt(coup);
				if (newPos > 0 && newPos < 24) {
					return newPos;
				}
				
			}
		}else{
			if(direction.equals("B")){
				newPos = (Integer.parseInt(this.PlayerPosition_1) - 1) + Integer.parseInt(coup);
				if (newPos > 0 && newPos < 24) {
					return newPos;
				}
				
			}else{
				newPos = (Integer.parseInt(this.PlayerPosition_1) - 1 ) - Integer.parseInt(coup);
				if (newPos > 0 && newPos < 24) {
					return newPos;
				}
				
			}
		}
		return null;
	}*/


	public Integer getNewPos(String coup, String direction){
		int newPos = -1 ;
		if (coup != null && direction != null) {
				
			if (this.CurrentPlayer.equals("0")) {
				if (direction.equals("F")) {
					newPos = (Integer.parseInt(this.PlayerPosition_1)) - Integer.parseInt(coup);
					if (newPos > 0 && newPos < 24) {
						return newPos;
					}
				}else{
					
					newPos = (Integer.parseInt(this.PlayerPosition_1)) + Integer.parseInt(coup);
					if (newPos > 0 && newPos < 24) {
						return newPos;
					}
				}
			}else{
				if(direction.equals("F")){
					newPos = Integer.parseInt(this.PlayerPosition_0) + Integer.parseInt(coup);
					if (newPos > 0 && newPos < 24) {
						return newPos;
					}
				}else{
					newPos = Integer.parseInt(this.PlayerPosition_0) - Integer.parseInt(coup);
					if (newPos > 0 && newPos < 24) {
						return newPos;
					}
					
				}
			}
		}

		return null;
	}







	public int getPos(String playeString){	
		if(playeString.equals("B")){
			return Integer.parseInt(this.PlayerPosition_0);
		}
		else{
			return Integer.parseInt(this.PlayerPosition_1);
		}
	}



	public int getPostionFromStart(){
		if(CurrentPlayer.equals("0")){
			return Integer.parseInt(PlayerPosition_0);
		}
		else{
			return Integer.parseInt(PlayerPosition_1);
		}
	}


	public int[] getSpace(){
		int[] space = new int[2];

		if(CurrentPlayer.equals("0")){
			space[0] = Integer.parseInt(this.getPlayerPosition_0());
			space[1] = this.getDistancePlayer();
		}
		else{
			space[0] = this.getDistancePlayer();
			space[1] = 23 - Integer.parseInt(this.getPlayerPosition_1());
		}
		return space;
	}





	public static void main(String[] args) {
		Board b = new Board("23", "1", "15", "1", "0", "0", "0", "0");
		b.printBoard();
		System.out.println(b.getNewPos("2", "B"));

	}



}