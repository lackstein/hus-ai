package student_player;

import java.util.HashMap;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import student_player.eval_funcs.AlphaBetaSearch;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {
	HashMap<String, Integer> weights = new HashMap<String, Integer>();
	
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
        		// Or maybe 33 -28 -32 -50 -73 -48 ...
        		weights.put("MY_INNER_WEIGHT", 30);
        		weights.put("MY_OUTER_WEIGHT", 30);
        		weights.put("MY_STEAL_WEIGHT", 15);
        		weights.put("OP_INNER_WEIGHT", 0);
        		weights.put("OP_OUTER_WEIGHT", 0);
        		weights.put("OP_STEAL_WEIGHT", -10);
        	} else {
        		String[] pieces = genome.split(" ");
        		String[] keys = { "MY_INNER_WEIGHT", "MY_OUTER_WEIGHT", "MY_STEAL_WEIGHT",
        				"OP_INNER_WEIGHT", "OP_OUTER_WEIGHT", "OP_STEAL_WEIGHT"};
        		
        		for(int i = 0; i < keys.length; i++) {
        			Integer weight = Integer.parseInt(pieces[i]);
        			weights.put(keys[i], weight);
        		}
        	}
        	
        	System.out.println(weights.toString());
        }
        
    	AlphaBetaSearch search = new AlphaBetaSearch(board_state, player_id, opponent_id, weights);
        return search.decide();
    }
}
