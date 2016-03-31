package student_player.eval_funcs;

public class LinearEval {
	public double MY_INNER_WEIGHT = 0.75;
	public double MY_OUTER_WEIGHT = 1.1;
	public double MY_STEAL_WEIGHT = 0.5;
	public double OP_INNER_WEIGHT = 0;
	public double OP_OUTER_WEIGHT = 0;
	
	public LinearEval() {
		
	}
	
	public LinearEval(double my_inner, double my_outer, double op_inner, double op_outer) {
		this.MY_INNER_WEIGHT = my_inner;
		this.MY_OUTER_WEIGHT = my_outer;
		this.OP_INNER_WEIGHT = op_inner;
		this.OP_OUTER_WEIGHT = op_outer;
	}
	
	public double eval(int[] my_pits, int[] op_pits) {
		int my_outer_sum = 0;
		int my_inner_sum = 0;
		int my_steal_sum = 0;
		int op_outer_sum = 0;
		int op_inner_sum = 0;
		
		for(int i = 0; i < my_pits.length; i++) {
			if(i + my_pits[i] > op_pits.length/2 && i + my_pits[i] <= op_pits.length - 1)
				my_steal_sum += op_pits[i + my_pits[i]];
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
		
		return this.MY_INNER_WEIGHT * my_inner_sum + this.MY_OUTER_WEIGHT * my_outer_sum + this.MY_STEAL_WEIGHT * my_steal_sum +
				this.OP_INNER_WEIGHT * op_inner_sum + this.OP_OUTER_WEIGHT * op_outer_sum;
	}
}
