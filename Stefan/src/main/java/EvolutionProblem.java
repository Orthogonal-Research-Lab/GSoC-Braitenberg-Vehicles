import java.util.*;

/**
 * Training example evolution problem.
 */
public class EvolutionProblem {

    private static final Random r = new Random();
    private static char[] genes = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ".toCharArray();
    private static String solution = "";
    private static int optimalFitness;
    private static int initialPopulation = 100;
    private static TreeMap<String, Integer> population;
    private static float mutationRate = 0.05F;
    private static float crossoverRate = 0.8F;
    private static float rateEliteSelected = 0.8F;
    private static float rateLuckySelected = 0.01F;
    private static long startTime = 0;
    private static long endTime = 0;

    public static void main(String[] args) {
        if (args.length < 1) {
            args = new String[1];
            args[0] = "Hello world"; //default "password"
        }
        run(args[0]);
    }

    /**
     * Algo entry point
     */
    private static void run(String arg) {
        for (int i = 0; i < arg.length(); i++) {
            if (!isGene(arg.charAt(i)))
                System.out.println("Target solution contains symbols which are not genes of current GA!");
        }
        startTime = System.currentTimeMillis();
        solution = arg;
        optimalFitness = solution.length(); //require to guess the whole word
        population = new TreeMap<>();
        // start GA
        for (int i = 0; i < initialPopulation; i++) {
            String chromosome = randomGeneSet();
            population.put(chromosome, fitness(chromosome));
        }
        System.out.println("Created " + initialPopulation + " pseudorandom chromosomes as starting population.");
        population.forEach((chr, fit) -> System.out.println("Chromosome " + chr + " with fitness " + fit));
        int maxEpochs = 500;
        int ep = 1;
        while (ep < maxEpochs) {
            mutate(population);
            crossover(population);
            population = select(population);
            if (solutionAchieved(population)) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                System.out.println("Achieved optimal fitness in " + ep + " epochs! Parameters: ");
                System.out.println("Initial population: " + initialPopulation);
                System.out.println("Mutation rate: " + mutationRate);
                System.out.println("Rate individuals mating: " + crossoverRate);
                System.out.println("Elitarism: " + rateEliteSelected);
                System.out.println("Running time (s): " + elapsedTime / 1000.0);
                System.exit(0);
            } else ep++;


        }
        System.out.println("Couldn't reach answer in " + maxEpochs + " epochs!");
    }

    private static boolean solutionAchieved(TreeMap<String, Integer> population) {
        for (Map.Entry<String, Integer> individual : population.entrySet()) {
            if (individual.getValue() >= optimalFitness) return true;
        }
        return false;
    }

    private static boolean isGene(char c) {
        for (int i = 0; i < genes.length; i++) {
            if (genes[i] == c) return true;
        }
        return false;
    }

    /**
     * Selection step of GA
     */
    private static TreeMap<String, Integer> select(TreeMap<String, Integer> population) {
        TreeMap<String, Integer> remaining = new TreeMap<>();
        // elitarism
        Map<String, Integer> sortedPop = sortByValues(population);
        Set<Map.Entry<String, Integer>> chromosomes = sortedPop.entrySet();
        Iterator<Map.Entry<String, Integer>> it = chromosomes.iterator();
        for (int i = 0; i < ((int) (chromosomes.size() * rateEliteSelected)); i++) {
            Map.Entry<String, Integer> nextChr = it.next();
            remaining.put(nextChr.getKey(), nextChr.getValue());
        }
        // TODO lucky ones
        System.out.println("Individuals that survived:");
        remaining.forEach((k, v) -> System.out.println("Chromosome: " + k + ", fitness: " + v));
        return remaining;
    }

    private static <K, V extends Comparable<V>> Map<K, V>
    sortByValues(Map<K, V> map) {
        Comparator<K> valueComparator =
                new Comparator<K>() {
                    public int compare(K k1, K k2) {
                        int compare =
                                map.get(k1).compareTo(map.get(k2));
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                };

        Map<K, V> sortedByValues =
                new TreeMap<K, V>(valueComparator.reversed());
        sortedByValues.putAll(map);
        return sortedByValues;
    }


    private static void crossover(TreeMap<String, Integer> population) {
        TreeMap<String, Integer> kids = new TreeMap<>();
        for (int i = 0; i < ((int) (population.size() * crossoverRate * 0.5)); i++) {
            String c1 = selectRandomChr(population);
            String c2 = String.valueOf(c1);
            while (c1.contentEquals(c2))
                c2 = selectRandomChr(population);
            String child = mixGametes(c1, c2);
            System.out.println("Produced child " + child);
            kids.put(child, fitness(child));
        }
        population.putAll(kids);
    }

    /**
     * 1-Point crossover
     */
    private static String mixGametes(String c1, String c2) {
        if (c1.length() != c2.length())
            throw new IllegalArgumentException("Crossing two not equally long chromosomes!");
        int pos = r.nextInt(c1.length());
        String[] possibleCrosses = new String[]{c1.substring(0, pos) + c2.substring(pos),
                c2.substring(0, pos) + c1.substring(pos)};
        return possibleCrosses[r.nextInt(possibleCrosses.length)];
    }

    private static String selectRandomChr(TreeMap<String, Integer> population) {
        List<String> keys = new ArrayList<String>(population.keySet());
        String randomKey = keys.get(r.nextInt(keys.size()));
        return randomKey;
    }

    /**
     * Random mutation.
     */
    private static void mutate(TreeMap<String, Integer> population) {
        Map<String, Integer> mutated = new HashMap<>();
        List<String> original = new LinkedList<>();
        population.forEach((chr, val) -> {
            if (r.nextInt(100) // a 100% cointoss
                    < ((int) (mutationRate * 100))) {
                int pos = r.nextInt(chr.length() - 1) + 1; // random position, not 0
                String mutatedChr = chr.substring(0, pos - 1) + genes[r.nextInt(genes.length)]
                        + chr.substring(pos);
                System.out.println("Mutated " + chr + " to " + mutatedChr);
                original.add(chr);
                mutated.put(mutatedChr, fitness(mutatedChr));
            }
        });
        for (String chr : original) population.remove(chr);
        population.putAll(mutated);
    }

    private static String randomGeneSet() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < solution.length(); i++) {
            sb.append(genes[r.nextInt(genes.length)]);
        }
        return sb.toString();
    }

    private static int fitness(String chromosome) {
        int d = solution.length();
        for (int j = 0; j < chromosome.length(); j++) {
            if (solution.charAt(j) != chromosome.charAt(j)) d--;
        }
        return d;
    }
}
