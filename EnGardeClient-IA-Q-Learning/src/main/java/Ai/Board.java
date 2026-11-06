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
    public char[] board = new char[23];


	public void printBoard(){
        
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


	public final static int MAX_DISTANCE = 23;

	public boolean estPartieTerminee = false;
	

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

    public Board(){}

	public Board(char[] board){this.board = board;}




    // public void printBoard(){
        
    //     this.board = new char[23];
    //     for (int i = 0; i < board.length; i++) {
    //         this.board[i] = ' ';
    //     }

    //     this.board[Integer.parseInt(PlayerPosition_0) - 1] = 'W';
    //     this.board[Integer.parseInt(PlayerPosition_1) - 1] = 'B';

    //     System.out.print("|");
    //     for (int i = 0; i < board.length; i++) {
    //         System.out.print(this.board[i] + "|");
    //     }
    //     System.out.println();
    // }

	public char getCase(int i){return this.board[i];}

	public boolean estPartieTerminee(){return this.estPartieTerminee;}
    
    
    public String getPlayerPosition_1() {return PlayerPosition_1;}
	public void setPlayerPosition_1(String playerPosition_1) {this.PlayerPosition_1 = playerPosition_1;}
	public String getPlayerPosition_0() {return PlayerPosition_0;}
	public void setPlayerPosition_0(String playerPosition_0) {this.PlayerPosition_0 = playerPosition_0;}
	public String getNumofDeck() {return NumofDeck;}
	public void setNumofDeck(String numofDeck) {this.NumofDeck = numofDeck;}
	public String getCurrentPlayer() {return CurrentPlayer;}
	public void setCurrentPlayer(String currentPlayer) {this.CurrentPlayer = currentPlayer;}
	public String getFrom() {return From;}
	public void setFrom(String from) {this.From = from;}
	public String getTo() {return To;}
	public void setTo(String to) {this.To = to;}
	public String getPlayerScore_0(){return PlayerScore_0;}
	public void setPlayerScore_0(String playerScore_0) {this.PlayerScore_0 = playerScore_0;}
	public String getPlayerScore_1() {return PlayerScore_1;}
	public void setPlayerScore_1(String playerScore_1) {this.PlayerScore_1 = playerScore_1;	}

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




	public Integer getNewPos(int depart, String coup, String direction){
		int newPos = -1 ;
		
		if (coup != null && direction != null) {
			if ((direction.equals("F") && Integer.parseInt(coup) < this.getDistancePlayer()) || direction.equals("B")) {			
				if (this.CurrentPlayer.equals("0")) {
					if (direction.equals("B")) {
						
						newPos = depart - Integer.parseInt(coup);
						if (newPos > 0 && newPos < 24) {
							return newPos;
						}
					}else{
						newPos = depart + Integer.parseInt(coup);
						if (newPos > 0 && newPos < 24) {
							return newPos;
						}
					}
				}else{
					if(direction.equals("B")){
						newPos = depart + Integer.parseInt(coup);
						if (newPos > 0 && newPos < 24) {
							
							return newPos;
						}
					}else{
						newPos = depart - Integer.parseInt(coup);
						if (newPos > 0 && newPos < 24) {
							return newPos;
						}
						
					}
				}
			}
		}

		return null;
	}

	public Integer getEvilNewPos(int depart, String coup, String direction){
		int newPos = -1 ;
		
		if (coup != null && direction != null) {
			if ((direction.equals("F") && Integer.parseInt(coup) < this.getDistancePlayer()) || direction.equals("B")){ 
				if (this.CurrentPlayer.equals("1")) {
					if (direction.equals("B")) {
						newPos = (depart) - Integer.parseInt(coup);
						//System.out.println("NEW POS : " + newPos);
						if (newPos > 0 && newPos < 24) {
							return newPos;
						}
					}else{
						newPos = (depart) + Integer.parseInt(coup);
						if (newPos > 0 && newPos < 24) {
							return newPos;
						}
					}
				}else{
					//System.out.println("NEW POS : " + newPos);
					if(direction.equals("B")){
						newPos = depart + Integer.parseInt(coup);
						if (newPos > 0 && newPos < 24) {
							return newPos;
						}
					}else{
						newPos = depart - Integer.parseInt(coup);
						if (newPos > 0 && newPos < 24) {
							return newPos;
						}
					}
				}
			}
		}

		return null;
	}

	
	public Integer move(String coup, String direction){
		
		//System.out.println("JE TENTE : " + this.getNewPos(coup, direction));
		if (this.CurrentPlayer.equals("0")) {
			Integer newPos = getNewPos(Integer.parseInt(this.getPlayerPosition_0()), coup, direction);
			if (newPos != null) {
				this.PlayerPosition_0 = Integer.toString(newPos);
				return newPos;
			}
		}else{
			Integer newPos = getNewPos(Integer.parseInt(this.getPlayerPosition_1()), coup, direction);
			if (newPos != null) {
				this.PlayerPosition_1= Integer.toString(newPos);
				return newPos;
			}
		}

		return null;
	}


	public Integer move(int position, String coup, String direction){
		
		//System.out.println("JE TENTE : " + this.getNewPos(coup, direction));
		if (this.CurrentPlayer.equals("0")) {
			Integer newPos = getNewPos(position, coup, direction);
			if (newPos != null) {
				this.PlayerPosition_0 = Integer.toString(newPos);
				return newPos;
			}
		}else{
			Integer newPos = getNewPos(position, coup, direction);
			if (newPos != null) {
				this.PlayerPosition_1= Integer.toString(newPos);
				return newPos;
			}
		}

		return null;
	}





	public int getActuallyPos(){
		if(CurrentPlayer.equals("0")){
			return Integer.parseInt(PlayerPosition_0);
		}
		else{
			return Integer.parseInt(PlayerPosition_1);
		}
	}

	public int getEvilPos(){
		if(CurrentPlayer.equals("0")){
			return Integer.parseInt(PlayerPosition_1);
		}
		else{
			return Integer.parseInt(PlayerPosition_0);
		}
	}

	public Integer evilPlay(int pos, String coup, String direction){
		//System.out.println("JE TENTE : " + this.getNewPos(coup, direction));
		if (this.CurrentPlayer.equals("1")) {
			Integer newPos = getEvilNewPos(pos, coup, direction);
			if (newPos != null) {
				this.PlayerPosition_0 = Integer.toString(newPos);
				return newPos;
			}
		}else{
			Integer newPos = getEvilNewPos(pos, coup, direction);
			if (newPos != null) {
				this.PlayerPosition_1= Integer.toString(newPos);
				return newPos;
			}
		}
		return null;
	}

	public Integer evilPlay(String coup, String direction){
		//System.out.println("JE TENTE : " + this.getNewPos(coup, direction));
		if (this.CurrentPlayer.equals("1")) {
			Integer newPos = getEvilNewPos(Integer.parseInt(this.getPlayerPosition_0()), coup, direction);
			if (newPos != null) {
				this.PlayerPosition_0 = Integer.toString(newPos);
				return newPos;
			}
		}else{
			Integer newPos = getEvilNewPos(Integer.parseInt(this.getPlayerPosition_1()), coup, direction);
			if (newPos != null) {
				this.PlayerPosition_1= Integer.toString(newPos);
				return newPos;
			}
		}
		return null;
	}




	public int getCurrentPos(){
		if (this.CurrentPlayer.equals("0")) {
			return Integer.parseInt(this.PlayerPosition_0);
			
		}else{
			return Integer.parseInt(this.PlayerPosition_1);
		}
	}


	public Integer getBasicPos(){
		if (this.CurrentPlayer.equals("0")) {
			return Integer.parseInt("23");
		}else{
			return Integer.parseInt("1");
		}
	}


	public void tp(int pos){
		if (this.CurrentPlayer.equals("0")) {
			this.PlayerPosition_0 = Integer.toString(pos);
		}else{
			this.PlayerPosition_1 = Integer.toString(pos);
		}
	}

	public void evilTp(int pos){
		if (this.CurrentPlayer.equals("0")) {
			this.PlayerPosition_1 = Integer.toString(pos);
		}else{
			this.PlayerPosition_0 = Integer.toString(pos);
		}
	}

	public int getDistanceWhithNegative(){

		return Integer.parseInt(PlayerPosition_1) - Integer.parseInt(PlayerPosition_0);
		
	}

	public Board copy() {
        Board copyBoard = new Board();
        for (int i = 0; i < this.board.length; i++) {
            copyBoard.board[i] = board[i];
        }
		copyBoard.PlayerPosition_0 = PlayerPosition_0;
		copyBoard.PlayerPosition_1 = PlayerPosition_1;
		copyBoard.NumofDeck = NumofDeck;
		copyBoard.CurrentPlayer = CurrentPlayer;
		copyBoard.From = From;
		copyBoard.To = To;
		copyBoard.PlayerScore_0 = PlayerScore_0;
		copyBoard.PlayerScore_1 = PlayerScore_1;
		return copyBoard;

    }



	public static void main(String[] args) {
		Board board = new Board("23", "1", "0", "0", "0", "0", "0", "0");

		board.printBoard();
		board.move("5", "F");
		board.evilPlay("3", "F");
		board.printBoard();
	}



}
