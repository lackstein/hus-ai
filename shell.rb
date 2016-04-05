#!/home/hus/.rvm/rubies/ruby-2.3.0/bin/ruby

require 'open3'

port = rand(1000) + 20000
data = {:out => [], :err => []}
server_started = false

intro_text =  <<~INTRO
Available commands:
start server: starts a hus server
start random: starts the random player
start player: starts Noah's player
INTRO

main()

def start_server
  server_started = true
  
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
          data[key].push parsed_line

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
end

def start_player(player)
  start_server unless server_started
  
  cmd = "cd ~/hus-ai/; java -cp bin boardgame.Client #{player} localhost #{port}"
  Open3.popen3(cmd) do |stdin, stdout, stderr, thread|
    # read each stream from a new thread
    { :out => stdout, :err => stderr }.each do |key, stream|
      Thread.new do
        until (raw_line = stream.gets).nil? do
          parsed_line = Hash[:timestamp => Time.now, :line => "#{raw_line}"]
          # append new lines
          data[key].push parsed_line

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
end

def main
  puts intro_text
  
  while line = gets.chomp
    case line
    when 'start server'
      start_server
    when 'start random'
      start_player 'hus.RandomHusPlayer'
    when 'start player'
      start_player 'student_player.StudentPlayer'
    else
      puts "Invalid command."
      puts intro_text
    end
  end
end