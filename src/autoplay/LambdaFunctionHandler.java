package autoplay;

import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import boardgame.Client;
import boardgame.Player;

public class LambdaFunctionHandler implements RequestHandler<Map<String,String>, String> {

    @Override
    public String handleRequest(Map<String,String> input, Context context) {
        String genome = input.get("genome");
        String server = input.get("server");
        int port = Integer.parseInt(input.get("port"));
        
        Player husplayer = new LambdaPlayer(genome);
        Client client = new Client(husplayer, server, port);
        
        client.run();
        return "success";
    }

}
