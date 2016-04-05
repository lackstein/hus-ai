package student_player.eval_funcs;

import java.util.HashMap;
import java.util.Map;

public class LinearEval {
	private HashMap<String, Double> weights = new HashMap<String, Double>();
	private int player_id;
	
	public LinearEval(int player_id) {
		this.player_id = player_id;
		
		weights.put("MY_INNER_WEIGHT", 0.75);
		weights.put("MY_OUTER_WEIGHT", 1.1);
		weights.put("MY_STEAL_WEIGHT", 0.5);
		weights.put("OP_INNER_WEIGHT", 0.0);
		weights.put("OP_OUTER_WEIGHT", 0.0);
		weights.put("OP_STEAL_WEIGHT", -0.1);
	}
	
	public LinearEval(int player_id, HashMap<String, Double> given_weights) {
		this.player_id = player_id;
		
		for(Map.Entry<String, Double> weight : given_weights.entrySet()) {
			weights.put(weight.getKey(), weight.getValue());
		}
	}
	
	public double eval(int[] my_pits, int[] op_pits) {
		int my_outer_sum = 0;
		int my_inner_sum = 0;
		int my_steal_sum = 0;
		int op_outer_sum = 0;
		int op_inner_sum = 0;
		int op_steal_sum = 0;
		
		for(int i = 0; i < my_pits.length; i++) {
			// Points for outer and inner row of pits
			if(i < 16) {
				my_outer_sum += my_pits[i];
				op_outer_sum += op_pits[i];
			} else {
				my_inner_sum += my_pits[i];
				op_inner_sum += op_pits[i];
			}
			
			// Points for seeds I can steal from my opponent
			if(i + my_pits[i] > op_pits.length/2 && i + my_pits[i] <= op_pits.length - 1)
				my_steal_sum += op_pits[i + my_pits[i]];
			
			// Points for seeds I can lose to my opponent
			if(i + op_pits[i] > my_pits.length/2 && i + op_pits[i] <= my_pits.length - 1)
				op_steal_sum += my_pits[i + op_pits[i]];
		}
			
		if(player_id == 0) {
			//op_steal_sum = 0;
		}
		
		return this.weights.getOrDefault("MY_INNER_WEIGHT", 0d) * my_inner_sum + this.weights.getOrDefault("MY_OUTER_WEIGHT", 0d) * my_outer_sum +
				this.weights.getOrDefault("MY_STEAL_WEIGHT", 0d) * my_steal_sum + 
				this.weights.getOrDefault("OP_INNER_WEIGHT", 0d) * op_inner_sum + this.weights.getOrDefault("OP_OUTER_WEIGHT", 0d) * op_outer_sum +
				this.weights.getOrDefault("OP_STEAL_WEIGHT", 0d) * op_steal_sum;
	}
}
