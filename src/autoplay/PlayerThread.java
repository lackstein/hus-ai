package autoplay;

import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClient;
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory;

public class PlayerThread extends Thread {
	Map<String, String> input;
	PlayerThread(Map<String, String> input) {
        this.input = input;
    }

    public void run() {
    	AWSLambdaClient lambda = new AWSLambdaClient();
        lambda.configureRegion(Regions.EU_CENTRAL_1);
        
        PlayerService playerService = LambdaInvokerFactory.build(PlayerService.class, lambda);
        playerService.playGame(this.input);
    }
}
