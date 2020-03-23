package student_player;

import boardgame.Move;

import Saboteur.SaboteurPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import Saboteur.SaboteurBoardState;

/** A player file submitted by a student. */
public class StudentPlayer extends SaboteurPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
	private MinimaxTree mt = null;
    public StudentPlayer() {
        super("StudentTesting");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    
    public Move chooseMove(SaboteurBoardState boardState) {
    		System.out.println("Student player acting as player number: "+boardState.getTurnPlayer());
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
//    		if(mt==null) {
//    			System.out.println("Tree creating");
//    			mt = new MinimaxTree(1,0,boardState);
//    		}
	    //	long startTime = System.currentTimeMillis()/1000;
        // Is random the best you can do?
	   

		
	    //	Move myMove = mt.getBestMove(1);
        //Move myMove = MyTools.getBestMove(boardState, depth);

        // Return your move to be processed by the server.
	    	//long endTime   = System.currentTimeMillis()/1000;
		//long totalTime = endTime - startTime;
		//System.out.println("Mytime: "+totalTime);
        return null;
    }
}
