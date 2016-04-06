package autoplay;

import java.util.HashMap;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import student_player.eval_funcs.AlphaBetaSearch;

public class LambdaPlayer extends HusPlayer {
	HashMap<String, Integer> weights = new HashMap<String, Integer>();
	
    /** You must modify this constructor to return your student number.
     * This is important, because this is what the code that runs the
     * competition uses to associate you with your agent.
     * The constructor should do nothing else. */
    public LambdaPlayer(String genome) { 
    	super("LambdaPlayer: " + genome);
    	String[] pieces = genome.split(" ");
    	String[] keys = { "MY_INNER_WEIGHT", "MY_OUTER_WEIGHT", "MY_STEAL_WEIGHT",
    			"OP_INNER_WEIGHT", "OP_OUTER_WEIGHT", "OP_STEAL_WEIGHT"};
    		
    	for(int i = 0; i < keys.length; i++) {
    		Integer weight = Integer.parseInt(pieces[i]);
    		weights.put(keys[i], weight);
    	}
    	
    	System.out.println(weights.toString());
    }

    /** This is the primary method that you need to implement.
     * The ``board_state`` object contains the current state of the game,
     * which your agent can use to make decisions. See the class hus.RandomHusPlayer
     * for another example agent. */
    public HusMove chooseMove(HusBoardState board_state)
    {
        AlphaBetaSearch search = new AlphaBetaSearch(board_state, player_id, opponent_id, weights);
        return search.decide();
    }
}
