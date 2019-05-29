/**
 * Class that its objects represents links between edges with their weight(distance).
 * All the methods can be private because they belong in the same package.
 * Email: gmparmpa@csd.auth.gr
 * AEM: 3108
 * @author Gregory Barmpas
 */
public class Distance {
    private int source;
    private int destination;
    private double weight;

    //Constructor.
    Distance(int source, int destination, double weight){
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public String toString(){
        return "{Source :: " + getSource() + " Destination :: " + getDestination() + " Weight :: " + getWeight() + "}";
    }

    //Getters for the variables.
    int getSource() {
        return source;
    }

    int getDestination() {
        return destination;
    }

    double getWeight() {
        return weight;
    }
}