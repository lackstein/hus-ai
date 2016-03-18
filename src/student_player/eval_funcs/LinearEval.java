package student_player.eval_funcs;

import hus.HusBoard;

public class LinearEval {
	public static final double MY_INNER_WEIGHT = 0.5;
	public static final double MY_OUTER_WEIGHT = 1.0;
	public static final double OP_INNER_WEIGHT = 0.25;
	public static final double OP_OUTER_WEIGHT = 0.0;
	
	@SuppressWarnings("unused")
	public static double eval(int[] my_pits, int[] op_pits) {
		int my_outer_sum = 0;
		int my_inner_sum = 0;
		int op_outer_sum = 0;
		int op_inner_sum = 0;
		
		for(int i = 0; i < my_pits.length; i++) {
			if(i < 16)
				my_outer_sum += my_pits[i];
			else
				my_inner_sum += my_pits[i];
		}
		
		for(int i = 0; i < op_pits.length; i++) {
			if(i < 16 && OP_OUTER_WEIGHT != 0.0)
				op_outer_sum += op_pits[i];
			if(i >= 16)
				op_inner_sum += op_pits[i];
		}
		
		return MY_INNER_WEIGHT * my_inner_sum + MY_OUTER_WEIGHT * my_outer_sum +
				OP_INNER_WEIGHT * op_outer_sum + OP_OUTER_WEIGHT * op_outer_sum;
	}
}
