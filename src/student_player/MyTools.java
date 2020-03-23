package student_player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Saboteur.SaboteurBoardState;
import Saboteur.SaboteurMove;
import boardgame.Move;

public class MyTools {
	
	
    public static double getSomething() {
        return Math.random();
    }

}

//class Node{
//	
//	private Node parent;
//	private SaboteurBoardState boardState;
//	private Move move;
//	private double value=0;
//	private List<Node> children = new ArrayList<Node>();
//	
//	public Node(Node parent, SaboteurBoardState boardState, Move move) {
//		this.parent = parent;
//		this.boardState = boardState;
//		this.move = move;
//	}
//	
//	public void setValue(double value) {
//		this.value = value;
//	}
//	public double getValue() {
//		return this.value;
//	}
//	public SaboteurBoardState getBoardState() {
//		return boardState;
//	}
//
//	public void setBoardState(SaboteurBoardState boardState) {
//		this.boardState = boardState;
//	}
//
//	public void addChild(Node c) {
//		children.add(c);
//	}
//	public Node getParent() {
//		return parent;
//	}
//
//	public void setParent(Node parent) {
//		this.parent = parent;
//	}
//
//	public List<Node> getChildren() {
//		return children;
//	}
//	
//	
//}

class MinimaxTree {

	public int depth;
	public SaboteurBoardState boardState;
	public SaboteurMove prevMove;
	public ArrayList<MinimaxTree> children = new ArrayList<MinimaxTree>();
	public boolean leaf = false;
	public int treeNodeCount = 1;
	public double valuation;
	private Random rand = new Random(Math.round(Math.random()*2000));
	
	public MinimaxTree(int maxDepth, int depth, SaboteurBoardState boardState) {
		this.boardState = boardState;
		this.depth = depth;
		if (depth != maxDepth) {
			List<SaboteurMove> nextMoves = this.boardState.getAllLegalMoves();
			if (nextMoves.size() == 0) {
				this.leaf = true;
				System.out.println("NO MOVES AT NONLEAF NODE");
			}
			for (SaboteurMove move : nextMoves) {
				// clone the bs and process the move
				SaboteurBoardState cloneBS = (SaboteurBoardState) this.boardState.clone();
	            cloneBS.processMove(move);
	            // create a new child tree
	            MinimaxTree child = new MinimaxTree(maxDepth, this.depth+1, cloneBS);
	            child.setPrevMove(move);
	            children.add(child);
	            treeNodeCount += child.treeNodeCount;
			}
		}
		else {
			this.leaf = true;
		}
	}

	private void setPrevMove(SaboteurMove prevMove) {
		this.prevMove = prevMove;
	}
	
	
	public SaboteurMove getBestMove(int boardPlayer) {
//		System.out.println("getBestMove: player="+boardPlayer+" depth="+depth);
		//
		if (boardPlayer == 1 ) {
			double maxVal = 0;
			MinimaxTree maxChild = children.get(rand.nextInt(children.size()));
			for (MinimaxTree child : children) {
				double childVal = child.minimax(0);
				if (childVal > maxVal) {
					maxVal = childVal;
					maxChild = child;
				}
			}
			return maxChild.prevMove;
		}
		else {
			double minVal = 1;
			MinimaxTree minChild = children.get(rand.nextInt(children.size()));
			for (MinimaxTree child : children) {
				double childVal = child.minimax(1);
				if (childVal < minVal) {
					minVal = childVal;
					minChild = child;
				}
			}
			return minChild.prevMove;
		}
	}

	private double minimax(int boardPlayer) {
//		System.out.print("minimax: player="+boardPlayer+" depth="+depth);
		if (leaf == true) {
			valuation = evaluate();
			return valuation;
		}
		if (boardPlayer == 1) {
			double maxVal = 0;
			for (MinimaxTree child : children) {
				double childVal = child.minimax(0);
				maxVal = Math.max(maxVal, childVal);
			}
			return maxVal;
		}
		else {
			double minVal = 1;
			for (MinimaxTree child : children) {
				double childVal = child.minimax(1);
				minVal = Math.min(minVal, childVal);
			}
			return minVal;
		}
	}

	private double evaluate() {
//		System.out.println("Ran Evaluate");
		double score =0;
		if (boardState.gameOver()) {
//			System.out.println("FOUND WIN");
			switch(boardState.getWinner()) {
				case 0:  score -=1000;
				break;
				case 1: score +=1000;
				break;
				default: break;
			}
		}
		score -= boardState.getTurnNumber()*10;
		String[] result = prevMove.toTransportable().split(" ");
		
		score += (Integer.parseInt(result[1])-boardState.originPos)*100;
		return score;
	}
}


class MINMAX{
	public static Move getBestMove(SaboteurBoardState bs, int depth) {
        double maxValue = Integer.MIN_VALUE;

        List<SaboteurMove> bestMoves = new ArrayList<SaboteurMove>();

        //First level for loop is always max, as this move is the move of the current player
        for (SaboteurMove move : bs.getAllLegalMoves()) {
        		SaboteurBoardState cloneBS = (SaboteurBoardState) bs.clone();
            cloneBS.processMove(move);
            Node newNode = new Node( null,cloneBS, move);
            
            double value = buildUpGameTree(newNode, Integer.MIN_VALUE, Integer.MAX_VALUE,depth);
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
	
	
	public static double buildUpGameTree(Node node, double alpha,double beta, int depth) {
		double bestValue = 0;
		SaboteurBoardState bs = node.getBoardState();
		if(depth ==0 || bs.gameOver()) {
			double value = util(bs);
			node.setValue(value);
			return value;
		}
		
		//minPlayer
		if(node.getBoardState().getTurnPlayer()==0) {
			 bestValue = Double.MAX_VALUE;
			 for(SaboteurMove m: bs.getAllLegalMoves()) {
				 SaboteurBoardState cloneBS = (SaboteurBoardState) bs.clone();
				 cloneBS.processMove(m);
				 Node child = new Node(node, cloneBS,m);
				 bestValue = Math.min(bestValue, buildUpGameTree(child, alpha, beta, depth - 1 ));
				 beta = Math.min(beta, bestValue);
				 if (beta <= alpha) {
					break;
				 }
			 	node.addChild(child);
			 }
			 
	         
		}
		else {
			//maxPlayer
			bestValue = Integer.MIN_VALUE;

            for (SaboteurMove m: bs.getAllLegalMoves()) {
            		SaboteurBoardState cloneBS = (SaboteurBoardState) bs.clone();
				cloneBS.processMove(m);
				Node child = new Node(node, cloneBS,m);
                bestValue = Math.max(bestValue, buildUpGameTree(child, alpha, beta,depth - 1 ));
                alpha = Math.max(alpha, bestValue);
                //if alpha has surpassed beta, prune this branch
                if (beta <= alpha) {
                    break;
                }
                node.addChild(child);
            }
		}
		
		node.setValue(bestValue);
		return bestValue;
	}
	
    public static double util(SaboteurBoardState bs) {
		double score =0;
		//1 is human
		switch(bs.getWinner()) {
			case(1):
				score+=1000;
				break;
			case(0):
				score-=1000;
				break;
			default:
				break;
		}

		//Feature 1: more malus for opponent means good 
		//score += bs.getNbMalus(1)*10;
		//Feature 2: less malus for me means good
		//score -= bs.getNbMalus(0)*10;
		//Feature 3: distance from the gold chicken
		//Feature 4: lessTurnNumber
		//score-=bs.getTurnNumber()*10;
		//Feature 4: the number of tile card left.
		//score += bs.getPlayerCardsForDisplay(0).size()*10;
		return score;
}
	
}
