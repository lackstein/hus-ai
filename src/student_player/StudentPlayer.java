package student_player;

import java.util.HashMap;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import student_player.eval_funcs.AlphaBetaSearch;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {
	HashMap<String, Double> weights = new HashMap<>();
	
    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public StudentPlayer() { super("260524490"); }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
        if(board_state.getTurnNumber() == 0) {
        	String genome = System.getenv("P"+player_id+"_GENOME");
        	//System.out.println("Initialized with genome: " + genome);
        	if(genome == null) {
        		weights.put("MY_INNER_WEIGHT", 0.75);
        		weights.put("MY_OUTER_WEIGHT", 1.1);
        		weights.put("MY_STEAL_WEIGHT", 0.5);
        		weights.put("OP_INNER_WEIGHT", 0.0);
        		weights.put("OP_OUTER_WEIGHT", 0.0);
        		weights.put("OP_STEAL_WEIGHT", -0.1);
        	} else {
        		
        	}
        	
        	System.out.println(weights.toString());
        }
        
    	AlphaBetaSearch search = new AlphaBetaSearch(board_state, player_id, opponent_id, weights);
        return search.decide();
    }
}
