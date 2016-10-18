/**
 * Created by abhandar on 9/10/16.
 */

import java.io.*;
import java.util.*;

public class SequentialPageRank {
    // adjacency matrix read from file
    private HashMap<Integer, ArrayList<Integer>> adjMatrix = new HashMap<Integer, ArrayList<Integer>>();
    // input file name
    private String inputFile = "";
    // output file name
    private String outputFile = "";
    // number of iterations
    private int iterations = 10;
    // damping factor
    private double df = 0.85;
    // number of URLs
    private int size = 0;
    // calculating rank values
    private HashMap<Integer, Double> rankValues = new HashMap<Integer, Double>();
    // number of urls
    private float numberOfUrls;

    /**
     * Parse the command line arguments and update the instance variables. Command line arguments are of the form
     * <input_file_name> <output_file_name> <num_iters> <damp_factor>
     *
     * @param args arguments
     */
    public void parseArgs(String[] args) {

        if (args.length == 0) {
            System.out.println("No cmdline arguments provided, exiting program !");
            System.exit(0);
        } else if (args.length <= 2 && args.length > 4) {
            System.out.println("Invalid cmdline arguments provided, exiting program !");
            System.exit(0);
        }
        this.inputFile = args[0];
        this.outputFile = args[1];
        if (args.length == 3) {
            try {
                this.iterations = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number format provided for iteration count" + nfe.getMessage());
            }
        } else {
            try {
                this.iterations = Integer.parseInt(args[2]);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number format provided for iteration count" + nfe.getMessage());
            }
            try {
                this.df = Double.parseDouble(args[3]);
            } catch (NumberFormatException nfe) {
                System.out.println("Invalid number format provided for damping factor" + nfe.getMessage());
            }
        }

    }

    /**
     * Read the input from the file and populate the adjacency matrix
     * <p>
     * The input is of type
     * <p>
     * 0
     * 1 2
     * 2 1
     * 3 0 1
     * 4 1 3 5
     * 5 1 4
     * 6 1 4
     * 7 1 4
     * 8 1 4
     * 9 4
     * 10 4
     * The first value in each line is a URL. Each value after the first value is the URLs referred by the first URL.
     * For example the page represented by the 0 URL doesn't refer any other URL. Page
     * represented by 1 refer the URL 2.
     *
     * @throws java.io.IOException if an error occurs
     */
    public void loadInput() throws IOException {
        try {
            FileReader reader = new FileReader(new File(this.inputFile));
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            this.adjMatrix = new HashMap<>();
            while ((line = bufferedReader.readLine()) != null) {
                Scanner nodes = new Scanner(line);
                ArrayList<Integer> referred = new ArrayList<>();
                // pull out the first node to form the hashMap key
                int node = nodes.nextInt();
                // pull out the subsequent nodes to form the hashMap values
                while (nodes.hasNextInt()) {
                    referred.add(nodes.nextInt());
                }
                adjMatrix.put(node, referred);
                nodes.close();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found, " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid number found, " + e.getMessage());
        }
    }

    /**
     * Do fixed number of iterations and calculate the page rank values. You may keep the
     * intermediate page rank values in a hash table.
     */
    public void calculatePageRank() {


        this.numberOfUrls = Collections.max(this.adjMatrix.keySet());
        Double initializePR = (double) (1 / (this.numberOfUrls + 1));
        //Initialize the Page rank for all the nodes
        for (Integer key : adjMatrix.keySet()) {
            this.rankValues.put(key, initializePR);
            if (adjMatrix.get(key).size() == 0) {
                ArrayList<Integer> list = new ArrayList<>();
                //Check if there any nodes with zero outgoing links,
                // if so, update the respective node with virtual links
                for (int i = 0; i < this.numberOfUrls; i++) {
                    if (i == key) {
                        continue;
                    } else {
                        list.add(i);
                    }
                }
                adjMatrix.put(key, list);
            }
        }
        // computes Page rank with damping factors
        for (int itr = 1; itr <= this.iterations; itr++) {
            for (int page = 0; page <= this.numberOfUrls; page++) {
                Double dampingFactor = ((1 - this.df) / this.numberOfUrls);
                Double intPageRank = 0.0;
                for (Integer key : adjMatrix.keySet()) {
                    if (adjMatrix.get(key).contains(page)) {
                        intPageRank += (this.rankValues.get(key) / this.adjMatrix.get(key).size());
                    }
                }
                intPageRank = dampingFactor + (this.df * intPageRank);
                this.rankValues.put(page, intPageRank);
            }
        }

    }

    /**
     * Print the pagerank values. Before printing you should sort them according to decreasing order.
     * Print all the values to the output file. Print only the first 10 values to console.
     *
     * @throws IOException if an error occurs
     */
    public void printValues() {
        List<Double> listPageRank = new ArrayList<Double>(this.rankValues.values());
        Collections.sort(listPageRank);
        Collections.reverse(listPageRank);
        listPageRank = listPageRank.subList(0, 10);
        System.out.println("Number of Iterations : " + this.iterations);
        for (Double key : listPageRank) {
            for (Map.Entry<Integer, Double> e : rankValues.entrySet()) {
                if (e.getValue() == key) {
                    System.out.println("Page Url: " + e.getKey() + "\t" + "PageRank: " + key);
                }
            }
        }
        try {
            //write output to file
            PrintWriter writer = new PrintWriter(this.outputFile);
            for (int page = 0; page <= this.numberOfUrls; page++) {
                writer.println(page + "\t" + this.rankValues.get(page));
            }
            writer.close();
        } catch (IOException ex) {
            System.out.println("File can't be written, " + ex.getMessage());
        }

    }

    public static void main(String[] args) throws IOException {
        SequentialPageRank sequentialPR = new SequentialPageRank();

        sequentialPR.parseArgs(args);
        sequentialPR.loadInput();
        sequentialPR.calculatePageRank();
        sequentialPR.printValues();
    }
}
