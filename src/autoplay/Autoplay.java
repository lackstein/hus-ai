package autoplay;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Author: Lilly Tong, Eric Crawford
//
// Assumes all the code in ``src`` has been compiled, and the resulting
// class files were stored in ``bin``.
//
// From the root directory of the project, run
//
//     java -cp bin autoplay.Autoplay n_games
//
// Note: The script is currently set up to have the StudentPlayer play against
// RandomHusPlayer. In order to have different players participate, you need
// to change the variables ``client1_line`` and ``client2_line``. Make sure
// that in those lines, the classpath and the class name is set appropriately
// so that java can find and run the compiled code for the agent that you want
// to test. For example to have StudentPlayer play against itself, you would
// change ``client2_line`` to be equal to ``client1_line``.
//
public class Autoplay
{
	public static final boolean LAMBDA = false;
	
    public static void main(String args[])
    {
        int n_games;
        try{
            n_games = Integer.parseInt(args[0]);
            if(n_games < 1) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.err.println(
                "First argument to Autoplay must be a positive int "
                + "giving the number of games to play.");
            return;
        }

        try {
        	String index = System.getenv("INDEX");
        	if(index == null)
        		index = "0";
        	String port = Integer.toString(8123 + Integer.parseInt(index));
        	        	
//            ProcessBuilder server_pb = new ProcessBuilder(
//                "java", "-cp", "bin",  "boardgame.Server", "-ng", "-k", "-p", port, "-ft", "60000");
//            server_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//
//            Process server = server_pb.start();
            
//            if(!LAMBDA) {
	            ProcessBuilder client1_pb = new ProcessBuilder(
	                "java", "-cp", "bin", "-Xms520m", "-Xmx520m", "boardgame.Client", "alpha_player.AlphaPlayer", "localhost", port);
	            client1_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
	            client1_pb.environment().put("ALPHA_GENOME", System.getenv("ALPHA_GENOME"));
	            client1_pb.environment().put("BETA_GENOME", System.getenv("BETA_GENOME"));
	
	            ProcessBuilder client2_pb = new ProcessBuilder(
	                "java", "-cp", "bin", "-Xms520m", "-Xmx520m", "boardgame.Client", "beta_player.BetaPlayer", "localhost", port);
	            client2_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
	            client2_pb.environment().put("ALPHA_GENOME", System.getenv("ALPHA_GENOME"));
	            client2_pb.environment().put("BETA_GENOME", System.getenv("BETA_GENOME"));
//            }
            
            Map<String,String> l_client1 = new HashMap<String,String>();
            Map<String,String> l_client2 = new HashMap<String,String>();
            if (LAMBDA) {
	            
	            l_client1.put("server", "52.28.211.135");
	            l_client1.put("port", port);
	            
	            
	            l_client2.putAll(l_client1);
	            
	            l_client1.put("genome", System.getenv("ALPHA_GENOME"));
	            l_client2.put("genome", System.getenv("BETA_GENOME"));
            }
            
            for (int i=0; i < n_games; i++) {
                System.out.println("Game "+i);
                
                if(LAMBDA) {
	                PlayerThread c1 = new PlayerThread(l_client1);
	                PlayerThread c2 = new PlayerThread(l_client2);
	                
	                try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
	                
	                c1.start();
	                c2.start();
	                
	                try {
	//					Thread.sleep(70000);
						c1.join();
						c2.join();
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
                }
                
                if(!LAMBDA) {
	                try {
	                    Thread.sleep(500);
	                } catch(InterruptedException ex) {
	                    Thread.currentThread().interrupt();
	                }
	
	                Process client1 = ((i % 2 == 0) ? client1_pb.start() : client2_pb.start());
	
	                try {
	                    Thread.sleep(500);
	                } catch(InterruptedException ex) {
	                    Thread.currentThread().interrupt();
	                }
	
	                Process client2 = ((i % 2 == 0) ? client2_pb.start() : client1_pb.start());
	
	                try{
	                    client1.waitFor();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	
	                try{
	                    client2.waitFor();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
                }
            }

//            server.destroy();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}