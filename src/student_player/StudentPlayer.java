package student_player;

import hus.HusBoardState;
import hus.HusPlayer;
import hus.HusMove;

import java.util.ArrayList;

import student_player.eval_funcs.LinearEval;

/** A Hus player submitted by a student. */
public class StudentPlayer extends HusPlayer {

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
        // Get the legal moves for the current board state.
        ArrayList<HusMove> moves = board_state.getLegalMoves();
        // Loop through moves and find one which has the greatest estimated value
        double best_result = 0.0;
        HusMove best_move = new HusMove();
        for(HusMove move : moves) {
        	HusBoardState cloned_board_state = (HusBoardState) board_state.clone();
            cloned_board_state.move(move);
            
            // Get the contents of the pits so we can use it to make decisions.
            int[][] pits = cloned_board_state.getPits();

            // Use ``player_id`` and ``opponent_id`` to get my pits and opponent pits.
            int[] my_pits = pits[player_id];
            int[] op_pits = pits[opponent_id];
            
            double result = LinearEval.eval(my_pits, op_pits);
            
            if(result > best_result) {
            	best_result = result;
            	best_move = move;
            	
            	System.out.println("New best move: pit " + move.getPit() + " for result " + result);
            }
        }
        

        // But since this is a placeholder algorithm, we won't act on that information.
        return best_move;
    }
}
