package student_player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import Saboteur.SaboteurPlayer;
import boardgame.Move;

public class MinMaxPlayer extends SaboteurPlayer  {
	
	
    public MinMaxPlayer() {
        super("MinMaxPlayer");
    }


    @Override
    public Move chooseMove(SaboteurBoardState boardState) {
        System.out.println("MINMAX acting as player number: "+boardState.getTurnPlayer());
        Move myMove = getBestMove(boardState,10000);
        return myMove;
      
    }
    
	public Move getBestMove(SaboteurBoardState bs, int height) {
        double maxValue = Integer.MIN_VALUE;

        List<SaboteurMove> bestMoves = new ArrayList<SaboteurMove>();

        //First level for loop is always max, as this move is the move of the current player
        for (SaboteurMove move : bs.getAllLegalMoves()) {
        		SaboteurBoardState cloneBS = (SaboteurBoardState) bs.clone();
            cloneBS.processMove(move);
            MinMaxNode newNode = new MinMaxNode( null,cloneBS, move);
            
            double value = buildUpGameTree(newNode, Integer.MIN_VALUE, Integer.MAX_VALUE,height);
            if (value > maxValue) {
                maxValue = value;
                //clear best moves, we have a better group
                bestMoves.clear();
                bestMoves.add(move);
            }
            //If move is equally as good, add to list
            if( value == maxValue) {
                bestMoves.add(move);
            }
        }

        //If multiple best moves, chose a random one
        if (bestMoves.size() > 0) {
            Random random = new Random();
            return bestMoves.get(random.nextInt(bestMoves.size()));
        } else {
            return bs.getRandomMove();
        }
    }
	
	public double buildUpGameTree(MinMaxNode node, double alpha,double beta, int height) {
		double bestValue = 0;
		SaboteurBoardState bs = node.getBoardState();
		if(height ==0 || bs.gameOver()) {
			double value = util(bs,node.getMove());
			return value;
		}
		
		//minPlayer
		if(bs.getTurnPlayer()!=player_id) {
			 bestValue = Double.MAX_VALUE;
			 for(SaboteurMove m: bs.getAllLegalMoves()) {
				 SaboteurBoardState cloneBS = (SaboteurBoardState) bs.clone();
				 cloneBS.processMove(m);
				 MinMaxNode child = new MinMaxNode(node, cloneBS,m);
				 bestValue = Math.min(bestValue, buildUpGameTree(child, alpha, beta, height - 1 ));
				 beta = Math.min(beta, bestValue);
				 if (beta <= alpha) {
					break;
				 }
			 }
		}
		else {
			//maxPlayer
			bestValue = Integer.MIN_VALUE;

            for (SaboteurMove m: bs.getAllLegalMoves()) {
            		SaboteurBoardState cloneBS = (SaboteurBoardState) bs.clone();
				cloneBS.processMove(m);
				MinMaxNode child = new MinMaxNode(node, cloneBS,m);
                bestValue = Math.max(bestValue, buildUpGameTree(child, alpha, beta,height - 1 ));
                alpha = Math.max(alpha, bestValue);
                //if alpha has surpassed beta, prune this branch
                if (beta <= alpha) {
                    break;
                }
            }
		}
		return bestValue;
	}
	
	   public double util(SaboteurBoardState boardState, SaboteurMove m) {
		String[] moveResult = m.toTransportable().split(" ");
		double score = 0; 
		
		//Heuristic For tile
		if(moveResult[0].matches("Tile:(.*)")) {
       		int difference =(Integer.parseInt(moveResult[1])-boardState.originPos);
       		if(difference<0) score-=difference*5000;
       		else score += difference*1000;
       		
       		score -= (12-Integer.parseInt(moveResult[1]))*2000;
		}
		
		if(moveResult[0].matches("Map(.*)")) {
			score +=1000;
		}
		if(moveResult[0].matches("Malus(.*)")) {
			score+=5000;
		}
		
		if(moveResult[0].matches("Bonus(.*)")) {
			score+=100;
		}
       		
		if(boardState.getWinner()==player_id) {
			score +=10000;
		}
		else {
			score -=10000;
		}
   		
   	return score;
	   }
}

class MinMaxNode{
	
	private MinMaxNode parent;
	private SaboteurBoardState boardState;
	private SaboteurMove move;
	private List<MinMaxNode> children = new ArrayList<MinMaxNode>();
	
	public MinMaxNode(MinMaxNode parent, SaboteurBoardState boardState, SaboteurMove move) {
		this.parent = parent;
		this.boardState = boardState;
		this.move = move;
	}
	
	public SaboteurMove getMove() {
		return move;
	}

	public void setMove(SaboteurMove move) {
		this.move = move;
	}

	public SaboteurBoardState getBoardState() {
		return boardState;
	}

	public void setBoardState(SaboteurBoardState boardState) {
		this.boardState = boardState;
	}

	public void addChild(MinMaxNode c) {
		children.add(c);
	}
	public MinMaxNode getParent() {
		return parent;
	}

	public void setParent(MinMaxNode parent) {
		this.parent = parent;
	}

	public List<MinMaxNode> getChildren() {
		return children;
	}
	
	
}
