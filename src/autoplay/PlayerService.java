package autoplay;

import java.util.Map;

import com.amazonaws.services.lambda.invoke.LambdaFunction;

public interface PlayerService {
	  @LambdaFunction(functionName="HusPlayer")
	  String playGame(Map<String,String> input);
}