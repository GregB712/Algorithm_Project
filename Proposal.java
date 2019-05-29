/**
 * Items of this class helps the stable-matching algorithm.
 * Email: gmparmpa@csd.auth.gr
 * AEM: 3108
 * @author Gregory Barmpas
 */
public class Proposal {

    private int destination;
    private double weight;

    Proposal(){
        //Empty Constructor.
    }

    //Constructor.
    Proposal(int destination, double weight){
        this.destination = destination;
        this.weight = weight;
    }

    @Override
    public String toString(){
        return  "{Destination :: " + getDestination() + " Weight :: " + getWeight() + "}";
    }

    //Getters for variables.
    int getDestination() {
        return destination;
    }

    double getWeight() {
        return weight;
    }

    //Setter for the Variables.
    void setDestination(int destination) {
        this.destination = destination;
    }

    void setWeight(double weight) {
        this.weight = weight;
    }
}