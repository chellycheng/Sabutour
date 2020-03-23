package student_player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import boardgame.Move;

public class AlphaBetaPlayer extends SaboteurPlayer {
	
	
	private int opponent_id;
	public AlphaBetaPlayer() {
        super("AlphaBetaPlayer");
        this.opponent_id = Math.abs(1-player_id);
    }
	
	@Override
	public Move chooseMove(SaboteurBoardState boardState) {
		// TODO Auto-generated method stub
		SaboteurMove bestMoveAB;
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		int turnNumber = boardState.getTurnNumber();
		if(turnNumber == 0){

            int depthTurn0 = 8;
            //System.out.println("Running Minimax Depth "+depthTurn0);     
            bestMoveAB = MinimaxAB(boardState, depthTurn0, alpha, beta, 5000000000L, 5); 


        } else {        //turnNumber greater than 0. 


            //MinimaxAB depths
            int depthL6 = 11;       //10
            int depthL5 = 10;       //9     working values.
            int depthL4 = 9;        //8
            int depthL3 = 8;
            int depthL2 = 8;
            int depthL1 = 7;
            int depthMain = 6;

            
             bestMoveAB = MinimaxAB(boardState, depthL6, alpha, beta, 900000000L, 5);
            
        }
		 return bestMoveAB;
	}
	
    public SaboteurMove MinimaxAB(SaboteurBoardState board_state, int depth, double alpha, double beta, long buffer, int quiescenceDepth){
        
        //method to resort the nodes on a level such that their evaluation function results sort them in an increasing index. 
        //ArrayList<Integer> newSearchOrder = MyTools.NewSearchOrder(board_state, myPlayer, oppPlayer);
        
        //method to use a Quiescence search to determine which nodes to look at deeper.
        ArrayList<Integer> newSearchOrder = QuiescenceSearch(board_state, quiescenceDepth, alpha, beta);
       
        //Collections.reverse(newSearchOrder);

        ArrayList<SaboteurMove> moves = board_state.getAllLegalMoves();

    	double maxScore = 0;
    	SaboteurMove maxMove = moves.get(newSearchOrder.get(0));
    	Timer husTimer  = new Timer();
    husTimer.setTimeoutMove(maxMove);

    	for(int i = 0; i< newSearchOrder.size(); i++){
        //for(int i = moves.size()-1; i > 0; i=i-1){
            long timeLeft = husTimer.getTimeLeft();
            //System.out.println("Time Left : "+timeLeft);
            if(timeLeft > buffer){

                long bufferStart = System.nanoTime();

                //System.out.println("Greater than buffer, looking at root child: "+i);

                SaboteurBoardState cloned_board_state = (SaboteurBoardState) board_state.clone();
                cloned_board_state.processMove(moves.get(newSearchOrder.get(i)));
                
                double score = MinValue(cloned_board_state, depth-1, alpha, beta);
                
                if (score > maxScore){
                    maxScore = score;
                    maxMove = moves.get(newSearchOrder.get(i));
                }

                if (maxScore > (husTimer.getTimeoutMoveScore())){
                    husTimer.setTimeoutMove(maxMove);
                    husTimer.setTimeoutMoveScore(maxScore);
                }

                buffer = Math.max(System.nanoTime() - bufferStart + ((long) 0.3*(System.nanoTime() - bufferStart)), 40000000L);

            } else {
                //System.out.println("Running out of time... Returning the best move so far");
                return husTimer.getTimeoutMove();
            }
    	}
		//System.out.println("Whole Tree Built. The best move: "+maxMove.getPit());

    	return maxMove;
    }

    //method to look for quiet or noisy regions in the upper portion of the game tree
    public ArrayList<Integer> QuiescenceSearch(SaboteurBoardState board_state, int depth, double alpha, double beta){
    
        ArrayList<SaboteurMove> moves = board_state.getAllLegalMoves();

        ArrayList<Node> quiescenceResults = new ArrayList<Node>();

        for(int i = 0; i< moves.size(); i++){
            
            //System.out.println("Greater than buffer, looking at root child: "+i);

            SaboteurBoardState cloned_board_state = (SaboteurBoardState) board_state.clone();
            cloned_board_state.processMove(moves.get(i));
            
            int score = (int) MinValue(cloned_board_state, depth-1, alpha, beta);

            Node childResult = new Node(score, i);

            quiescenceResults.add(childResult);
            
        }

        Collections.sort(quiescenceResults);

        ArrayList<Integer> scoresList = new ArrayList<Integer>();
        for(int i = 0; i < quiescenceResults.size(); i++){
            scoresList.add(quiescenceResults.get(i).getScore());
        }
        //System.out.println("Quiescence Results: "+scoresList);

        int selectionAmount = Math.min(8, quiescenceResults.size());

        ArrayList<Integer> newSearchNodes = new ArrayList<Integer>();

        for (int i = 0; i < selectionAmount; i++){
            int index = quiescenceResults.size()-1-i;
            newSearchNodes.add(quiescenceResults.get(index).getIndex());
        }

        //System.out.println("After Selection : "+ newSearchNodes);
        
        return newSearchNodes;
    }


    public double MaxValue(SaboteurBoardState board_state, int depth, double alpha, double beta){

    	if(depth ==0 || board_state.getAllLegalMoves() == null){
    		double score = util(board_state);	
    		return score;
    		
    	} else {
    		
    		ArrayList<SaboteurMove> moves = board_state.getAllLegalMoves();
      //       ArrayList<Integer> newSearchOrder = MyTools.NewSearchOrder(board_state, myPlayer, oppPlayer);
            //System.out.println("New Search Order: "+newSearchOrder);

    		
    		for(int i=0; i< moves.size(); i++){
            //for(int i = moves.size()-1; i > 0; i=i-1){
    	
    			SaboteurBoardState cloned_board_state = (SaboteurBoardState) board_state.clone();
    			cloned_board_state.processMove(moves.get(i));
    			
    			alpha = Math.max(alpha, MinValue(cloned_board_state, depth-1, alpha, beta));
    			if (alpha >= beta) {
    				//System.out.println("MaxValue : Cutoff Occured, Alpha ("+alpha+") >= Beta ("+beta+"), Returning Beta");
    				return beta;
    			}
    			
    		}
    		return alpha;	
    	}
    }
   public double util(SaboteurBoardState boardState) {
		
       	double score =0;
		if(boardState.getWinner()==player_id) {
			score +=10000;
		}
		else {
			score -=10000;
		}
		return score;
   }
    
    public double MinValue(SaboteurBoardState board_state, int depth, double alpha, double beta){
    
    	if (depth == 0){
    		double score = util(board_state);			
    		return score;
    		
    	} else {
    		
    		ArrayList<SaboteurMove> moves = board_state.getAllLegalMoves();
            //this has returned an asscending list. we want to reverse its direction
            // ArrayList<Integer> newSearchOrder = MyTools.NewSearchOrder(board_state, myPlayer, oppPlayer);
            // Collections.reverse(newSearchOrder);
            //System.out.println("New Search Order: "+newSearchOrder);

    		
    		for(int i=0; i < moves.size(); i++){
            //for(int i = moves.size()-1; i > 0; i=i-1){    
    			
    			SaboteurBoardState cloned_board_state = (SaboteurBoardState) board_state.clone();
    			cloned_board_state.processMove(moves.get(i));
    			beta = Math.min(beta, MaxValue(cloned_board_state, depth-1, alpha, beta));
    			
    			if (alpha >= beta) {
    				//System.out.println("Min Vaue : Cutoff Occured, Alpha ("+alpha+") >= Beta ("+beta+") , Returning Alpha");
    				return alpha;
    			}
    		}
    		return beta;
    		}
    }
}

class Timer {
	
	
	private long endTime;
	private SaboteurMove timeoutMove;
	private double timeoutMoveScore;
	private long startTime;


	public Timer(){
		timeoutMoveScore = 0.0;
	}


	public void setEndTime(int option){
		startTime = System.nanoTime();

		if (option == 0) {		//turn 0
			long turnTime0 = 30000000000L;
			endTime = startTime + turnTime0;
			double timerLength = ((endTime - startTime));
			//System.out.println("\nMY TURN: "+option+", "+timerLength+" second Timer created, startTime = " + startTime + ", End Time = "+endTime);

		} else {					//turn > 0
			endTime = startTime + 2000000000L;
			//System.out.println("\nMY TURN: "+option+" 2 second Timer created, startTime = " + (double) startTime/1000000000 + ", End Time = "+(double)endTime/1000000000);

		}

	}


	public long getCurrentTime(){
		return System.nanoTime();
	}

	public long getEndTime(){
		return endTime;
	}

	public long getTimeUsed(){
		return System.nanoTime() - startTime;
	}

	public long getTimeLeft(){
		return endTime - System.nanoTime();

	}

	//Fail Safe Move, Get and Set Methods

	public void setTimeoutMove(SaboteurMove newMove){
		this.timeoutMove = newMove;
	}

	public void setTimeoutMoveScore(double newScore){
		this.timeoutMoveScore = newScore;
	}

	public SaboteurMove getTimeoutMove(){
		return timeoutMove;
	}

	public double getTimeoutMoveScore(){
		return timeoutMoveScore;
	}

}

class Node implements Comparable<Node> {
	private int score;
	private int index;
	
	//CONSTRUCTOR FOR NODE
	public Node(int score, int index){
		this.score = score;	
		this.index = index;
	}
	
	//SETTER METHODS FOR NODE
	public void setScore(int score){
		this.score = score;
	}


	//GETTER METHODS for Node
	public int getScore(){
		return score;
	}
	public int getIndex(){
		return index;
	}

	public int compareTo(Node compareNode) {

		int compareScore = ((Node)compareNode).getScore();

		//sort in ascending order (i.e. increasing)

		return this.score - compareScore;
	}

}
