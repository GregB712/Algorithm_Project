/**
 * Items of this class helps the algorithm of dynamic programming for the lesser "coin" change.
 * Email: gmparmpa@csd.auth.gr
 * AEM: 3108
 * @author Gregory Barmpas
 */
class Hash_Object {

    private int key, value;

    //Constructor.
    Hash_Object(int key, int value){
        this.key = key;
        this.value = value;
    }

    //Getters for the variables.
    int getKey() {
        return key;
    }

    int getValue() {
        return value;
    }
}