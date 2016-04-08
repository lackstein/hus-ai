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
    		weights.put("MY_INNER_WEIGHT", 36);
    		weights.put("MY_OUTER_WEIGHT", 40);
    		weights.put("MY_STEAL_WEIGHT", 20);
    		weights.put("OP_INNER_WEIGHT", -65);
    		weights.put("OP_OUTER_WEIGHT", -30);
    		weights.put("OP_STEAL_WEIGHT", -66);
        }
        
    	AlphaBetaSearch search = new AlphaBetaSearch(board_state, player_id, opponent_id, weights);
        return search.decide();
    }
}
