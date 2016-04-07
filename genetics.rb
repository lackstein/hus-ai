# Adapted from http://mattmazur.com/2013/08/18/a-simple-genetic-algorithm-written-in-ruby/
require 'thread'
require 'thwait'
require 'timeout'
require 'csv'

POPULATION_SIZE = 20
NUM_GENERATIONS = 100
CROSSOVER_RATE = 0.7
MUTATION_RATE = 0.002
AUTOPLAY_GAMES = 1

CLASS_PATH = "bin:../aws-java-sdk-1.10.67/lib/aws-java-sdk-1.10.67.jar:../aws-java-sdk-1.10.67/lib/aws-java-sdk-flow-build-tools-1.10.67.jar:../aws-java-sdk-1.10.67/lib/aws-lambda-java-core.jar:../aws-java-sdk-1.10.67/third-party/lib/aspectjrt-1.8.2.jar:../aws-java-sdk-1.10.67/third-party/lib/aspectjweaver.jar:../aws-java-sdk-1.10.67/third-party/lib/commons-codec-1.6.jar:../aws-java-sdk-1.10.67/third-party/lib/commons-logging-1.1.3.jar:../aws-java-sdk-1.10.67/third-party/lib/freemarker-2.3.9.jar:../aws-java-sdk-1.10.67/third-party/lib/httpclient-4.3.6.jar:../aws-java-sdk-1.10.67/third-party/lib/httpcore-4.3.3.jar:../aws-java-sdk-1.10.67/third-party/lib/jackson-annotations-2.5.0.jar:../aws-java-sdk-1.10.67/third-party/lib/jackson-core-2.5.3.jar:../aws-java-sdk-1.10.67/third-party/lib/jackson-databind-2.5.3.jar:../aws-java-sdk-1.10.67/third-party/lib/jackson-dataformat-cbor-2.5.3.jar:../aws-java-sdk-1.10.67/third-party/lib/javax.mail-api-1.4.6.jar:../aws-java-sdk-1.10.67/third-party/lib/joda-time-2.8.1.jar:../aws-java-sdk-1.10.67/third-party/lib/spring-beans-3.0.7.RELEASE.jar:../aws-java-sdk-1.10.67/third-party/lib/spring-context-3.0.7.RELEASE.jar:../aws-java-sdk-1.10.67/third-party/lib/spring-core-3.0.7.RELEASE.jar:../aws-java-sdk-1.10.67/third-party/lib/spring-test-3.0.7.RELEASE.jar"

module HMap
  refine Hash do
    def hmap(&block)
      result = Hash.new
      self.keys.each do |key|
        hash = block.call(key, self[key])

        result[hash.keys.first] = hash[hash.keys.first]
      end

      result
    end
  end
end

class Fitness
  using HMap

  def initialize
    @scores = Hash.new { |hsh, key| hsh[key] = Hash.new { |h,k| h[k] = 0 } }
  end

  def add_game(winner:, loser:)
    @scores[loser][winner]
    @scores[winner][loser] += 1
  end

  def wins(player)
    @scores[player].values.reduce(0) { |acc, v| acc + v }
  end

  def losses(player)
    @scores.map { |k, v| v[player] if v.has_key?(player) }.compact.reduce(0) { |acc, v| acc + v }
  end

  def total_games(player)
    wins(player) + losses(player)
  end

  def fitness(player)
    wins(player) / total_games(player).to_f
  end

  def fitness_values
    @scores.hmap { |key, val| { key => fitness(key) } }
  end
end

class Chromosome

  attr_accessor :genes

  def initialize(genes = [])
    if genes.size == 0
      self.genes = (1..6).map do
        value = rand(200) - 100
        8.downto(0).map { |bit| value[bit] }.join
      end
    else
      raise("Must have 6 genes") unless genes.size == 6
      self.genes = genes
      # self.genes = genes.map do |gene|
      #   8.downto(0).map { |bit| gene.to_i[bit] }.join
      # end
    end
  end

  def to_s
    self.genes.map { |gene| from_twos(gene) }.join(' ')
  end

  def inspect
    "Chromosome<" + self.genes.map { |gene| from_twos(gene) }.join(' ') + ">"
  end

  def from_twos(val)
    sign = val[0]

    if sign == "0"
      return val.to_i(2)
    end

    ~val[1..-1].split('').map { |bit| bit == "0" ? "1" : "0" }.join.to_i(2)
  end

  def count
    genes.size
  end

  def mutate!
    self.genes = self.genes.map do |gene|
      mutated = ""

      gene.split('').each do |allele|
        if rand <= MUTATION_RATE
          mutated += (allele == "0") ? "1" : "0"
        else
          mutated += allele
        end
      end

      mutated
    end

    self
  end

  def &(other)
    locus = rand(genes.length)

    child1 = genes[0, locus] + other.genes[locus, other.genes.length]
    child2 = other.genes[0, locus] + genes[locus, other.genes.length]

    return [
      Chromosome.new(child1),
      Chromosome.new(child2),
    ]
  end
end

class Population

  attr_accessor :chromosomes, :fitness

  def initialize
    self.chromosomes = Array.new
    self.fitness = Fitness.new
  end

  def inspect
    chromosomes.join(" ")
  end

  def seed!
    chromosomes = Array.new
    1.upto(POPULATION_SIZE).each do
      chromosomes << Chromosome.new
    end

    self.chromosomes = chromosomes
  end

  def count
    chromosomes.count
  end

  def fitness_values
    self.fitness.fitness_values
  end

  def total_fitness
    fitness_values.values.inject(0) {|total, value| total + value }
  end

  def max_fitness
    fitness_values.values.max
  end

  def average_fitness
    total_fitness.to_f / chromosomes.length.to_f
  end

  def fittest(n = 1)
    fitness_values.max(n) { |a,b| a[1] <=> b[1] }
  end

  def select
    rand_selection = rand(0..total_fitness)

    total = 0
    index = 0
    fitness_values.each do |chromosome, value|
      total += value
      index += 1
      return chromosome if total > rand_selection || index == chromosomes.count
    end
  end

  def find(str)
    chromosomes.select { |c| c.to_s == str }.first
  end

  def battle!
    combinations = chromosomes.combination(2).to_a

    combinations.each_slice(100) do |slice|

      threads = []
      slice.each_with_index do |(a, b), i|
        threads << Thread.new(a, b, i) do |alpha, beta, index|
          env_vars = %Q(ALPHA_GENOME="#{alpha.to_s}" BETA_GENOME="#{beta.to_s}") # INDEX=#{index}
          begin
            Timeout::timeout(10 + 1 * 60 * AUTOPLAY_GAMES) {
              `#{env_vars} java -cp "#{CLASS_PATH}" autoplay.Autoplay #{AUTOPLAY_GAMES}`
            }
          rescue Timeout::Error
            combinations.push([alpha, beta])
            next
          end
        end

        sleep 1.5
      end
      threads.each { |thread| thread.join }

    end

    results = CSV.read 'logs/outcomes.txt'

    results.each do |result|
      # If the game ended for some random reason, let's skip it
      next unless result[7].nil?

      player1, player2 = find(result[1].gsub(/.*: /, '')), find(result[2].gsub(/.*: /, ''))
      winner_text = result[4].gsub(/.*: /, '')

      next if player1.nil? || player2.nil? || ![player1.to_s, player2.to_s].include?(winner_text)

      winner, loser = winner_text == player1.to_s ? [player1, player2] : [player2, player1]

      fitness.add_game winner: winner, loser: loser
    end

    # Empty the outcomes file for the next run
    File.open('logs/outcomes.txt', 'w') { |file| file.write '' }
  end

  def self.run!
    population = Population.new
    population.seed!

    1.upto(NUM_GENERATIONS).each do |generation|
      population.battle!

      offspring = Population.new
      n = (population.count.to_f / 10).ceil
      offspring.chromosomes.concat population.fittest(n).collect(&:first)


      while offspring.count < population.count
        parent1 = population.select
        parent2 = population.select

        if rand <= CROSSOVER_RATE
          child1, child2 = parent1 & parent2
        else
          child1 = parent1
          child2 = parent2
        end

        child1.mutate!
        child2.mutate!

        offspring.chromosomes << [child1, child2].sample
      end

      while offspring.count < POPULATION_SIZE
        offspring.chromosomes << Chromosome.new
      end

      puts "Generation #{generation} - Average: #{population.average_fitness.round(2)} - Max: #{population.max_fitness}"
      puts "Population: " + population.fitness_values.sort { |a, b| b[1] <=> a[1] }.to_s

      population = offspring unless generation == NUM_GENERATIONS
    end

    puts "Final population: " + population.fitness_values.sort { |a, b| b[1] <=> a[1] }.to_s
  end
end

Population.run! if __FILE__==$0
