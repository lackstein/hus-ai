#!/home/hus/.rvm/rubies/ruby-2.3.0/bin/ruby

require 'open3'
require 'logger'

log_name = "#{Time.now.to_i}.txt"
logger = Logger.new "/var/www/html/logs/#{log_name}"
data = {:out => [], :err => []}
port = rand(1000) + 20000
cmd = "cd ~/hus-ai/; LOGNAME=#{log_name} java -cp bin boardgame.Server -ng -p #{port}"

puts <<~START
You can connect to the server by running:
  java -cp bin boardgame.Client student_player.StudentPlayer hus.lackstein.com #{port}
A log of the game is available at:
  http://hus.lackstein.com/logs/#{log_name}
START

# see: http://stackoverflow.com/a/1162850/83386
Open3.popen3(cmd) do |stdin, stdout, stderr, thread|
  # read each stream from a new thread
  { :out => stdout, :err => stderr }.each do |key, stream|
    Thread.new do
      until (raw_line = stream.gets).nil? do
        parsed_line = Hash[:timestamp => Time.now, :line => "#{raw_line}"]
        data[key].push parsed_line
                
        logger.info raw_line
        puts raw_line
      end
    end
  end

  # Trap ^C 
  Signal.trap("INT") { 
    thread.exit
  }

  # Trap `Kill `
  Signal.trap("TERM") {
    thread.exit
  }
  
  Thread.new do
    loop do
      if Time.now - 900 > data[:out].last[:timestamp] || !thread.alive?
        thread.exit
        break
      end
    end
  end
  
  thread.join # don't exit until the external process is done
end