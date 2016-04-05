package student_player.eval_funcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import hus.HusBoardState;
import hus.HusMove;

public class AlphaBetaSearch {
	public static int MAX_DEPTH = 5;
	public static int MAX_NODES = 200000;
	private HusBoardState board_state;
	private int my_id;
	private int op_id;
	private int expandedNodes;
	private LinearEval heuristic;

	public AlphaBetaSearch(HusBoardState board_state, int my_id, int op_id, HashMap<String, Double> weights) {
		this.board_state = board_state;
		this.my_id = my_id;
		this.op_id = op_id;
		this.heuristic = new LinearEval(my_id, weights);
	}
	
	public HusMove decide(){
		this.expandedNodes = 0;
		HusMove result = new HusMove();
		double resultValue = Double.NEGATIVE_INFINITY;
		
		ArrayList<HusMove> moves = this.board_state.getLegalMoves();
		Collections.shuffle(moves);
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) this.board_state.clone();
			moveResult.move(move);
			
			double value = min(moveResult, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1);
			if(value > resultValue) {
				result = move;
				resultValue = value;
			}
		}
		
		System.out.println("Alpha " + resultValue + " / Expanded " + this.expandedNodes + " nodes");
		
		return result;
	}
	
	public double max(HusBoardState state, double alpha, double beta, int depth) {
		this.expandedNodes++;
		
		if(state.gameOver() || depth > MAX_DEPTH)
			return heuristic.eval(state.getPits()[this.my_id], state.getPits()[this.op_id]);
		
		double value = Double.NEGATIVE_INFINITY;
		ArrayList<HusMove> moves = state.getLegalMoves();
		Collections.shuffle(moves);
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) state.clone();
			moveResult.move(move);
			value = Math.max(value, this.min(moveResult, alpha, beta, depth + 1));
			if(value >= beta)
				return value;
			
			alpha = Math.max(alpha, value);
		}
		
		return value;
	}
	
	public double min(HusBoardState state, double alpha, double beta, int depth) {
		this.expandedNodes++;
		
		if(state.gameOver() || depth > MAX_DEPTH)
			return heuristic.eval(state.getPits()[this.my_id], state.getPits()[this.op_id]);
				
		double value = Double.POSITIVE_INFINITY;
		ArrayList<HusMove> moves = state.getLegalMoves();
		Collections.shuffle(moves);
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) state.clone();
			moveResult.move(move);
			value = Math.min(value, this.max(moveResult, alpha, beta, depth + 1));
			if(value <= alpha)
				return value;
			
			beta = Math.min(beta, value);
		}
		
		return value;
	}
}
