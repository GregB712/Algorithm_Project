import java.util.ArrayList;

/**
 * The class that represents the ants.
 * All the methods can be private because they belong in the same package.
 * Email: gmparmpa@csd.auth.gr
 * AEM: 3108
 * @author Gregory Barmpas
 */
public class Data {
    private int color;
    private double[] coordinates;
    private ArrayList<Integer> info;

    //Constructor for the red ants.
    Data(int color, double coor1, double coor2, int capacity){
        this.color = color;
        coordinates = new double[]{coor1, coor2};
        info = new ArrayList<>();
        info.add(capacity);
    }

    //Constructor for the black ants.
    Data(int color, double coor1, double coor2, int size1, int size2, int size3, int size4, int size5){
        this.color = color;
        coordinates = new double[]{coor1, coor2};
        info = new ArrayList<>();
        info.add(size1);
        info.add(size2);
        info.add(size3);
        info.add(size4);
        info.add(size5);
    }

    @Override
    public String toString() {
        if(color%2==0){
            return color + "    " + getInfo(0) +", " + getInfo(1) +", "+ getInfo(2) +", "+ getInfo(3) +", "+ getInfo(4);
        }
        return color + "    " + getInfo(0);
    }

    //Getters for the variables.
    double getCoor(int i) {return coordinates[i];}

    int getInfo(int i) {return info.get(i);}
}