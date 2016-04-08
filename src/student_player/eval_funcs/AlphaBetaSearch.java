package student_player.eval_funcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hus.HusBoardState;
import hus.HusMove;

public class AlphaBetaSearch {
	private HusBoardState board_state;
	private int my_id;
	private int op_id;
	private int expandedNodes;
	private LinearEval heuristic;
	private long startTime;
	private long allowedTime = 1600000000;
	private final int MAX_DEPTH = 3;
//	private Map<HusBoardState, Double> transmutations = new HashMap<HusBoardState, Double>();

	public AlphaBetaSearch(HusBoardState board_state, int my_id, int op_id, HashMap<String, Integer> weights) {
		this.board_state = board_state;
		this.my_id = my_id;
		this.op_id = op_id;
		this.heuristic = new LinearEval(my_id, weights);
	}

	public HusMove decide(){
		startTime = System.nanoTime();
		this.expandedNodes = 0;
		int max_depth;
		HusMove result = new HusMove();
		double resultValue = Double.NEGATIVE_INFINITY;

		ArrayList<HusMove> moves = this.board_state.getLegalMoves();
		Collections.shuffle(moves);

		for(max_depth = 1; max_depth < MAX_DEPTH; max_depth++) {
			for(HusMove move : moves) {
				HusBoardState moveResult = (HusBoardState) this.board_state.clone();
				moveResult.move(move);

				double value = min(moveResult, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1, max_depth);
				if(value > resultValue) {
					result = move;
					resultValue = value;
				}
			}

			if(System.nanoTime() - startTime > allowedTime)
				break;
		}

		System.out.println("Alpha " + resultValue + " / Depth " + max_depth + " / Expanded " + this.expandedNodes + " nodes");

		return result;
	}

	public double max(HusBoardState state, double alpha, double beta, int depth, int max_depth) {
		this.expandedNodes++;

		if(state.gameOver() && state.getWinner() == this.my_id)
			return Double.MAX_VALUE;

		if(depth >= max_depth || System.nanoTime() - startTime > allowedTime)
			return heuristic.eval(state.getPits()[this.my_id], state.getPits()[this.op_id]);

		double value = Double.NEGATIVE_INFINITY;
		ArrayList<HusMove> moves = state.getLegalMoves();
		Collections.shuffle(moves);
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) state.clone();
			moveResult.move(move);

//			if(transmutations.containsKey(moveResult)) {
//				value = transmutations.get(moveResult);
//			} else {
				value = Math.max(value, this.min(moveResult, alpha, beta, depth + 1, max_depth));
//				transmutations.put(moveResult, value);
//			}

			if(value >= beta)
				return value;

			alpha = Math.max(alpha, value);
		}

		return value;
	}

	public double min(HusBoardState state, double alpha, double beta, int depth, int max_depth) {
		this.expandedNodes++;

		if(state.gameOver() && state.getWinner() == this.my_id)
			return Double.MAX_VALUE;

		if(depth >= max_depth || System.nanoTime() - startTime > allowedTime)
			return heuristic.eval(state.getPits()[this.my_id], state.getPits()[this.op_id]);

		double value = Double.POSITIVE_INFINITY;
		ArrayList<HusMove> moves = state.getLegalMoves();
		Collections.shuffle(moves);
		for(HusMove move : moves) {
			HusBoardState moveResult = (HusBoardState) state.clone();
			moveResult.move(move);

//			if(transmutations.containsKey(moveResult)) {
//				value = transmutations.get(moveResult);
//			} else {
				value = Math.min(value, this.max(moveResult, alpha, beta, depth + 1, max_depth));
//				transmutations.put(moveResult, value);
//			}

			if(value <= alpha)
				return value;

			beta = Math.min(beta, value);
		}

		return value;
	}
}
