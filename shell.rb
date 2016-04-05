#!/home/hus/.rvm/rubies/ruby-2.3.0/bin/ruby

require 'open3'

port = rand(1000) + 20000
data = {:out => [], :err => []}

puts <<~START
You can connect to the server by running:
  java -cp bin boardgame.Client student_player.StudentPlayer hus.lackstein.com #{port}
START

# see: http://stackoverflow.com/a/1162850/83386
Open3.popen3("cd ~/hus-ai/; java -cp bin boardgame.Server -ng -p #{port}") do |stdin, stdout, stderr, thread|
  # read each stream from a new thread
  { :out => stdout, :err => stderr }.each do |key, stream|
    Thread.new do
      until (raw_line = stream.gets).nil? do
        parsed_line = Hash[:timestamp => Time.now, :line => "#{raw_line}"]
        # append new lines
        self.data[key].push parsed_line

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

  thread.join # don't exit until the external process is done
end