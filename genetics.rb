# Adapted from http://mattmazur.com/2013/08/18/a-simple-genetic-algorithm-written-in-ruby/

POPULATION_SIZE = 24
NUM_GENERATIONS = 1000
CROSSOVER_RATE = 0.7
MUTATION_RATE = 0.002

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
    genes.to_s
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

  def fitness
    from_twos(genes[0]) - from_twos(genes[1])
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

  attr_accessor :chromosomes

  def initialize
    self.chromosomes = Array.new
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
    chromosomes.collect(&:fitness)
  end

  def total_fitness
    fitness_values.inject{|total, value| total + value }
  end

  def max_fitness
    fitness_values.max
  end

  def average_fitness
    total_fitness.to_f / chromosomes.length.to_f
  end

  def fittest(n = 1)
    chromosomes.max(n) { |a,b| a.fitness <=> b.fitness }
  end

  def select
    rand_selection = rand(total_fitness)

    total = 0
    chromosomes.each_with_index do |chromosome, index|
      total += chromosome.fitness
      return chromosome if total > rand_selection || index == chromosomes.count - 1
    end
  end
end

population = Population.new
population.seed!

1.upto(NUM_GENERATIONS).each do |generation|

  offspring = Population.new
  offspring.chromosomes.concat population.fittest(2)


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

puts "Final population: " + population.inspect
