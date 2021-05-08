package Tree;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;

public class PersonInfo extends Info {
    private ArrayList <Integer> childrenIds = new ArrayList<Integer>();
    private ArrayList <Integer> parentIds = new ArrayList<Integer>(2);
    private int spouseId = -1;

    public PersonInfo (String firstName, String lastName, int ID, LocalDate birthDate, String birthCity) {
        super(firstName, lastName, ID, birthDate, birthCity);
    }

    public void printValues() {
        System.out.println(firstName + " " + lastName + " " +  id + " " + birthCity + " " + birthDate);
        System.out.println("Children: " + childrenIds.size());
        for (int i = 0; i < childrenIds.size(); i++) {
            System.out.print(childrenIds.get(i) + " ");
        }
        System.out.println();
    }
}
