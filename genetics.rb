# Adapted from http://mattmazur.com/2013/08/18/a-simple-genetic-algorithm-written-in-ruby/
require 'thread'
require 'thwait'
require 'CSV'

POPULATION_SIZE = 6
NUM_GENERATIONS = 5
CROSSOVER_RATE = 0.7
MUTATION_RATE = 0.002

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

f = Fitness.new
f.add_game winner: 1, loser: 2
f.add_game winner: 1, loser: 3
f.add_game winner: 3, loser: 2

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
    end
  end

  def to_s
    self.genes.map { |gene| from_twos(gene).to_s(2) }.join(' ')
  end
  
  def inspect
    "Chromosome<" + self.genes.map { |gene| from_twos(gene) }.join(', ') + ">"
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
    fitness_values.values.inject{|total, value| total + value }
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
    rand_selection = rand(total_fitness)

    total = 0
    index = 0
    fitness_values.each do |chromosome, value|
      total += value
      index += 1
      return chromosome if total > rand_selection || index == chromosomes.count
    end
  end
  
  def battle!
    chromosomes.each_with_index do |alpha, index|
      threads = []
      chromosomes[index+1..-1].each_with_index do |beta, b_index|
        threads << Thread.new(b_index) do |thread|
          env_vars = %Q(ALPHA_GENOME="#{alpha.to_s}" BETA_GENOME="#{beta.to_s}" INDEX=#{thread})
          `#{env_vars} java -cp bin autoplay.Autoplay 2`
          results = `tail -n 10 logs/outcomes-#{thread}.txt`
        
          results = CSV.parse results
          results.each do |result|
            winner = results[4] == "AlphaPlayer" ? alpha : beta
            loser = winner == alpha ? beta : alpha
          
            self.fitness.add_game winner: winner, loser: loser
          end
        end
      end
      
      ThreadsWait.all_waits(*threads)
    end
  end
  
end

population = Population.new
population.seed!

1.upto(NUM_GENERATIONS).each do |generation|
  population.battle!

  offspring = Population.new
  offspring.chromosomes.concat population.fittest(2).collect(&:first)


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

    if POPULATION_SIZE.even?
      offspring.chromosomes << child1 << child2
    else
      offspring.chromosomes << [child1, child2].sample
    end
  end

  puts "Generation #{generation} - Average: #{population.average_fitness.round(2)} - Max: #{population.max_fitness}"

  population = offspring
end

puts "Final population: " + population.fittest(5)