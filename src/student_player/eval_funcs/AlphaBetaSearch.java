package student_player.eval_funcs;

import java.util.ArrayList;
import java.util.Collections;

import hus.HusBoardState;
import hus.HusMove;

public class AlphaBetaSearch {
	public static int MAX_DEPTH = 5;
	private HusBoardState board_state;
	private int my_id;
	private int op_id;
	private int expandedNodes;
	private LinearEval heuristic = new LinearEval();

	public AlphaBetaSearch(HusBoardState board_state, int my_id, int op_id) {
		this.board_state = board_state;
		this.my_id = my_id;
		this.op_id = op_id;
	}
	
	public HusMove decide(){
		this.expandedNodes = 0;
		HusMove result = new HusMove();
		double resultValue = Double.NEGATIVE_INFINITY;
		
		ArrayList<HusMove> moves = this.board_state.getLegalMoves();
		Collections.shuffle(moves);
		double[] value = { Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) this.board_state.clone();
			moveResult.move(move);
			
			value = min(moveResult, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
			if(value[0] > resultValue) {
				result = move;
				resultValue = value[0];
			}
		}
		
		System.out.println("Alpha " + value[0] + " / Beta " + value[1] + " / Expanded " + this.expandedNodes + " nodes");
		
		return result;
	}
	
	public double[] max(HusBoardState state, double alpha, double beta, int depth) {
		this.expandedNodes++;
		
		if(state.gameOver() || depth > MAX_DEPTH)
			return new double[] { heuristic.eval(state.getPits()[this.my_id], state.getPits()[this.op_id]), beta };
		
		double value = Double.NEGATIVE_INFINITY;
		double[] result;
		ArrayList<HusMove> moves = state.getLegalMoves();
		Collections.shuffle(moves);
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) state.clone();
			moveResult.move(move);
			result = this.min(moveResult, alpha, beta, depth + 1);
			value = Math.max(value, result[0]);
			if(value >= beta)
				return new double[] { alpha, value};
			
			alpha = Math.max(alpha, value);
		}
		
		return new double[] { value, beta };
	}
	
	public double[] min(HusBoardState state, double alpha, double beta, int depth) {
		this.expandedNodes++;
		
		if(state.gameOver() || depth > MAX_DEPTH)
			return new double[] { alpha, heuristic.eval(state.getPits()[this.my_id], state.getPits()[this.op_id]) };
				
		double value = Double.POSITIVE_INFINITY;
		double result[];
		ArrayList<HusMove> moves = state.getLegalMoves();
		Collections.shuffle(moves);
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) state.clone();
			moveResult.move(move);
			result = this.max(moveResult, alpha, beta, depth + 1);
			value = Math.min(value, result[1]);
			if(value <= alpha)
				return new double[] { alpha, value };
			
			beta = Math.min(beta, value);
		}
		
		return new double[] { alpha, value };
	}
}
