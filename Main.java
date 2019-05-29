import java.io.*;
import java.util.*;

/**
 * The main class of the program.
 * Email: gmparmpa@csd.auth.gr
 * AEM: 3108
 * @author Gregory Barmpas
 */
public class Main {

    private static HashMap <Integer, Data> hashMap;
    private static PriorityQueue<Distance> pq;
    private static List<List<Distance>> graphs;
    private static int size=0;

    private static HashMap <Integer, ArrayList<Proposal>> redMap;
    private static HashMap <Integer, ArrayList<Proposal>> blackMap;
    private static HashMap <Integer, Proposal> proposalMap;

    private static HashMap<Integer, Integer> res = new HashMap<>();
    private static List<List<Hash_Object>> levels = new ArrayList<>();
    private static HashMap<Integer, Integer> weightsFrequency = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Algorithm Project");
        hashMap = new HashMap<>();
        blackMap = new HashMap<>();
        redMap = new HashMap<>();
        proposalMap = new HashMap<>();
        readData(args[0]);

        int i, j;
        double dist;

        for (i=0; i<hashMap.size();i++){
            size = size + hashMap.size()-(i+1);
        }
        //Filling up the "table" of distances for each ant with all the others.
        pq = new PriorityQueue<>(size, Comparator.comparing(Distance::getWeight));
        for (i=1; i<hashMap.size()+1; i++){
            for (j=i+1; j<hashMap.size()+1; j++){
                dist = euclideanDistance(hashMap.get(i).getCoor(0), hashMap.get(i).getCoor(1), hashMap.get(j).getCoor(0), hashMap.get(j).getCoor(1));
                pq.add(new Distance(i, j, dist));
            }
        }

        long startTime, endTime;

        System.out.println();
        System.out.println("MODE A:");
        System.out.println("Finding Minimum Spanning Tree using Kruskla + Union-Find...");
        startTime = System.currentTimeMillis();
        MST();
        endTime = System.currentTimeMillis();
        graphs.clear();
        System.out.println("Executed in " + (endTime - startTime) + " milliseconds.");

        //Filling up the "tables" of matching for both black & red ants.
        for (i=1; i<hashMap.size()+1; i++){
            for (j=1; j<hashMap.size()+1; j++){
                if(i%2==0){
                    if(j%2!=0){
                        dist = euclideanDistance(hashMap.get(i).getCoor(0), hashMap.get(i).getCoor(1), hashMap.get(j).getCoor(0), hashMap.get(j).getCoor(1));
                        blackMap.get(i).add(new Proposal(j, dist));
                    }
                }else{
                    if(j%2==0) {
                        dist = euclideanDistance(hashMap.get(i).getCoor(0), hashMap.get(i).getCoor(1), hashMap.get(j).getCoor(0), hashMap.get(j).getCoor(1));
                        redMap.get(i).add(new Proposal(j, dist));
                    }
                }
            }
        }

        //Sorting the ArrayLists in both blackMap and redMap.
        for (Integer key: blackMap.keySet()){
            blackMap.get(key).sort(Comparator.comparing(Proposal::getWeight));
        }

        for (Integer key: redMap.keySet()){
            redMap.get(key).sort(Comparator.comparing(Proposal::getWeight));
        }

        System.out.println();
        System.out.println("MODE B:");
        System.out.println("Calculating Best Matches (Black-Red) using Gale - Shapley...");
        startTime = System.currentTimeMillis();
        Gale_Shapley();
        endTime = System.currentTimeMillis();
        System.out.println("Executed in " + (endTime - startTime) + " milliseconds.");

        System.out.println();
        System.out.println("MODE C:");
        System.out.println("Calculating the Frequency of Seeds needed to fill containers...");
        startTime = System.currentTimeMillis();
        Dynamic_Programming();
        endTime = System.currentTimeMillis();
        System.out.println("Executed in " + (endTime - startTime) + " milliseconds.");

    }

    //Extract the data from the file "data.txt" into a hash-map.
    private static void readData(String filename){
        int color;
        double coor1;
        double coor2;
        int capacity;
        int size1;
        int size2;
        int size3;
        int size4;
        int size5;
        Scanner scan;
        File file = new File(filename);
        try {
            scan = new Scanner(file);
            while(scan.hasNextDouble())
            {
                Data ant;
                color = (int) scan.nextDouble();
                if ((color%2)==0){
                    coor1 = scan.nextDouble();
                    coor2 = scan.nextDouble();
                    size1 = (int) scan.nextDouble();
                    size2 = (int) scan.nextDouble();
                    size3 = (int) scan.nextDouble();
                    size4 = (int) scan.nextDouble();
                    size5 = (int) scan.nextDouble();
                    ant = new Data(color, coor1, coor2, size1, size2, size3, size4, size5);
                    //Adding the black ants in two hash_maps that will help us for the stable matching problem.
                    blackMap.put(color, new ArrayList<>());
                    proposalMap.put(color, new Proposal());
                }
                else{
                    coor1 = scan.nextDouble();
                    coor2 = scan.nextDouble();
                    capacity = (int) scan.nextDouble();
                    ant = new Data(color, coor1, coor2, capacity);
                    ////Adding the red ants in a different hash_map that will help us for the stable matching problem.
                    redMap.put(color,  new ArrayList<>());
                }
                hashMap.put(color,ant);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    //Euclidean distance calculator
    private static double euclideanDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt(((x2-x1)*(x2-x1))+((y2-y1)*(y2-y1)));
    }

    //Searches the array-list of graphs for a specific edge.
    private static int find(int x){
        for(int i=0; i<graphs.size();i++){
            for(Distance dist : graphs.get(i)){
                if(dist.getSource()==x || dist.getDestination()==x){
                    return i;
                }
            }
        }
        return -1;
    }

    //Searches to find if our graph contains all the edges.
    private static boolean allFind(){
        boolean existAll = false;
        for (List<Distance> graph : graphs) {
            if (graph.size() == hashMap.size() - 1) {
                existAll = true;
            }
        }
        return existAll;
    }

    //Unite two array-lists by copying all the data from the one to the other.
    private static void union(List<Distance> list1, List<Distance> list2){
        list1.addAll(list2);
        list2.clear();
    }

    /*
     * Kruskal Minimum Spanning Tree.
     * Being sorted, we begin with adding the link(small graph) with the smallest weight in the list of graphs. Then we
     * proceed by adding the other links with ascending order. As we do so we always check if there is a graph in the
     * list of graphs that have either one or two of the edges of the link we want to add. If there is a graph with only
     * one then we add that link to that list (graphs is a list of lists). If there are both edges but in different
     * graphs then we unite the two graph-lists and if there are in the same graph then we ignore that link because it
     * wil create a cycle in out total graph. After all that we end up with a list of lists of graphs that only one is
     * the graph we want, and that is the one that has size equal of the hashMap that we originally put out data.
     * At last we print the results in a .txt file.
     */
    private static void MST(){
        Distance temp;
        int keeper1, keeper2;
        graphs = new ArrayList<>();
        graphs.add(new ArrayList<>());
        graphs.get(0).add(pq.remove());
        while(!allFind()){
            temp = pq.remove();
            keeper1 = find(temp.getSource());
            keeper2 = find(temp.getDestination());
            if (keeper1 == -1 && keeper2 == -1){
                graphs.add(new ArrayList<>());
                graphs.get(graphs.size()-1).add(temp);
            } else if (keeper1 == -1 && keeper2 != -1){
                graphs.get(keeper2).add(temp);
            } else if (keeper1 != -1 && keeper2 == -1){
                graphs.get(keeper1).add(temp);
            } else if (keeper1 != -1 && keeper2 != -1 && keeper1 == keeper2){
                //ignore
            } else if (keeper1 != -1 && keeper2 != -1 && keeper1 != keeper2){
                union(graphs.get(keeper1), graphs.get(keeper2));
                graphs.get(keeper1).add(temp);
            }
        }
        PriorityQueue<Distance> new_pq = new PriorityQueue<>(size, Comparator.comparing(Distance::getSource).thenComparing(Distance::getDestination));
        int pos=0;
        /*
         * Finding the list of links that is out final graph.
         */
        for(int i=0; i<graphs.size(); i++){
            if(graphs.get(i).size()==hashMap.size() - 1){
                pos=i;
            }
        }
        new_pq.addAll(graphs.get(pos));
        double sum = 0;
        //Calculate the sum of the graph's weight.
        for(Distance distance : new_pq){
            sum = sum + distance.getWeight();
        }
        //Writing the data of the new_pq to a .txt file.
        try{
            PrintWriter pw = new PrintWriter("Minimum_Spanning_Tree.txt");
            pw.println(sum);
            for (int i=0; i<hashMap.size()-1;i++){
                temp = new_pq.remove();
                pw.println(temp.getSource() + "   " + temp.getDestination());
            }
            pw.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    //Returns the position of an integer in a list of Proposal items.
    private static int position(ArrayList<Proposal> arrayList, int i){
        for(int j=0; j<arrayList.size();j++){
            if(arrayList.get(j).getDestination()==i)
                return j;
        }
        return -1;
    }

    //Detects if there is someone single or not.
    private static boolean notFind(int i){
        for(int key : proposalMap.keySet()){
            if(proposalMap.get(key).getDestination()==i){
                return false;
            }
        }
        return true;
    }

    /*
     * Gale Shapley.
     * First we check if there is proposal, if not then we match the two ants and add that proposal to proposalMap.
     * If there is then we check which of the two proposals is more suitable for the black one, as the red ants make
     * the proposals, and if necessary change the two proposals in the proposalMap. The proposalMap contains the final
     * proposals.
     */
    private static void Gale_Shapley(){
        Proposal temp;
        for (int i=1; i<2*redMap.size(); i=i+2){
            if (notFind(i)){
                temp = redMap.get(i).remove(0);
                if (proposalMap.get(temp.getDestination()).getDestination() == 0){
                    proposalMap.get(temp.getDestination()).setDestination(i);
                    proposalMap.get(temp.getDestination()).setWeight(temp.getWeight());
                }else{
                    if(position(blackMap.get(temp.getDestination()), temp.getDestination()) <
                            position(blackMap.get(temp.getDestination()),
                                    proposalMap.get(temp.getDestination()).getDestination())){
                        proposalMap.get(temp.getDestination()).setDestination(i);
                        proposalMap.get(temp.getDestination()).setWeight(temp.getWeight());
                        i = -1;
                    }else{
                        //Reject Proposal.
                    }
                }
            }
        }
        //Inserting the result in priority queue so we can sort them.
        PriorityQueue<Distance> temp_pq = new PriorityQueue<>(Comparator.comparing(Distance::getSource));
        for(int key : proposalMap.keySet()){
            temp_pq.add(new Distance(key, proposalMap.get(key).getDestination(), proposalMap.get(key).getWeight()));
        }
        Distance distance;
        //Writing the result in a .txt file.
        try{
            PrintWriter pw = new PrintWriter("Stable_Matching.txt");
            for (int i=0; i<hashMap.size()/2;i++){
                distance = temp_pq.remove();
                pw.println(distance.getSource() + "   " + distance.getDestination());
            }
            pw.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    //Returns the minimum number of seeds that required to fill up a container or -1 if it is impossible. Also it fills
    // up a hash map with the results. The D table contains the weights and n is the capacity. The function is the
    // implementation of the F(n) = min F(n-dj) +1, for n>0 and F(0) = 0.
    private static int ChangeMaking(int[] D, int n) {
        int[] F = new int[n+1];
        res.put(0,0);
        for (int i = 1; i <= n; i++) {
            int temp = Integer.MAX_VALUE/2;
            //Loop in all currencies yielding a non-negative balance.
            for (int weight : D) {
                if (weight <= i) {
                    if (F[i - weight] < temp) {
                        temp = F[i - weight];
                    }
                }
            }
            F[i] = temp + 1;
            res.put(i, F[i]);
        }
        return F[n] < Integer.MAX_VALUE/2 ? F[n] : -1;
    }

    //Recursive function that fills up a hash map of frequencies of weights (coins) that need to be used so we have the
    // least number of weights (coins) been used.
    private static void whatWeights(int[] D, int n){
        levels.add(new ArrayList<>());
        for(int weight : D){
            if(n>=weight){
                levels.get(levels.size()-1).add(new Hash_Object(n-weight, res.get(n-weight)));
            }
        }
        levels.get(levels.size()-1).sort(Comparator.comparing(Hash_Object::getValue));
        //weightFrequency has weights as keys and the frequency of them as value.
        for(int weight : D){
            if(n-levels.get(levels.size()-1).get(0).getKey() == weight)
                weightsFrequency.put(weight, weightsFrequency.get(weight)+1);
        }
        if(levels.get(levels.size()-1).get(0).getKey()==0){
            //STOP
        }
        else
            whatWeights(D, levels.get(levels.size()-1).get(0).getKey());
    }

    /* Dynamic Algorithm Implementation
     * Function that uses the ChangeMaking and whatWeights in all the pairs (black-red) of ants.
     */
    private static void Dynamic_Programming(){
        try{
            PrintWriter pw = new PrintWriter("Dynamic_Programming.txt");
            for(int i=1;i<=hashMap.size();i=i+2){
                int[] D = new int[5];
                for(int j=0;j<5;j++){
                    D[j] = hashMap.get(i+1).getInfo(j);
                    weightsFrequency.put(D[j], 0);
                }

                int n = hashMap.get(i).getInfo(0);
                int temp = ChangeMaking(D, n);
                if(temp!=-1){
                    whatWeights(D, n);
                    pw.println(i + " " + (i+1) + "    " + weightsFrequency.get(D[0]) + ", " +
                            weightsFrequency.get(D[1]) + ", " + weightsFrequency.get(D[2]) + ", " +
                            weightsFrequency.get(D[3]) + ", " + weightsFrequency.get(D[4]));
                }
                res.clear();
                weightsFrequency.clear();
            }
            pw.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}