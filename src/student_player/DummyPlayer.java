package student_player;

import java.util.Arrays;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import boardgame.Move;

public class DummyPlayer extends SaboteurPlayer  {
	
	
    public DummyPlayer() {
        super("DummyPlayer");
    }


    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        System.out.println("Dummy acting as player number: "+boardState.getTurnPlayer());
        Move myMove = getCorodinateBestMove(boardState);
        return myMove;
    }
    
    public Move getCorodinateBestMove(SaboteurBoardState boardState) {
    		Move bestMove=null;
    		double bestscore = Double.MIN_VALUE;
    		for(SaboteurMove m: boardState.getAllLegalMoves()) {
    			String[] moveResult = m.toTransportable().split(" ");
    			double score = 0; 
    			
    			//Heuristic For tile
    			if(moveResult[0].matches("Tile:(.*)")) {
            		int difference =(Integer.parseInt(moveResult[1])-boardState.originPos);
            		if(difference<0) score-=difference*50;
            		else score += difference*10;
            		
            		score -= (12-Integer.parseInt(moveResult[1]))*10;
    			}
    			
    			if(moveResult[0].matches("Map(.*)")) {
    				score +=10;
    			}
    			if(moveResult[0].matches("Malus(.*)")) {
    				score+=300;
    			}
    			
    			if(moveResult[0].matches("Bonus(.*)")) {
    				score+=10;
    			}
            		SaboteurBoardState temp = (SaboteurBoardState) boardState.clone();
            		temp.processMove(m);
        		if(temp.gameOver()) {
        			if(temp.getWinner()==player_id) {
        				score +=1000;
        			}
        			else {
        				score -=1000;
        			}
        		}
        		
        		if(bestscore< score) {
        			bestMove = m;
        			bestscore=score;
        		}
            
    		}
    		if(bestMove==null) {
    			bestMove = boardState.getRandomMove();
    		}
    		return bestMove;
    }

}
