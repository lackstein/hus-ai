package student_player.eval_funcs;

import java.util.HashMap;
import java.util.Map;

public class LinearEval {
	
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
			if(i + my_pits[i] > op_pits.length/2 && i + my_pits[i] <= op_pits.length - 1 && my_pits[i + my_pits[i]] >= 1)
				my_steal_sum += op_pits[i + my_pits[i]] + op_pits[i + my_pits[i] - op_pits.length/2];
			
			// Points for seeds I can lose to my opponent
			if(i + op_pits[i] > my_pits.length/2 && i + op_pits[i] <= my_pits.length - 1 && op_pits[i + op_pits[i]] >= 1)
				op_steal_sum += my_pits[i + op_pits[i]] + my_pits[i + op_pits[i] - my_pits.length/2];
		}
		
		return 36 * my_inner_sum + 40 * my_outer_sum + 20 * my_steal_sum + 
				-65 * op_inner_sum + -30 * op_outer_sum + -66 * op_steal_sum;
	}
}
