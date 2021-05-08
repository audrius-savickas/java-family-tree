package Tree;

import java.time.LocalDate;
import java.util.ArrayList;

abstract class Info {
    final String firstName;
    final String lastName;
    final int id;
    final LocalDate birthDate;
    final String birthCity;
    int spouseId = -1;
    ArrayList<Integer> childrenIds = new ArrayList<Integer>();
    ArrayList <Integer> parentIds = new ArrayList<Integer>(2);

    Info (String firstName, String lastName, int ID, LocalDate birthDate, String birthCity){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = ID;
        this.birthDate = birthDate;
        this.birthCity = birthCity;
    }

    public void addChildren(int id) {
        childrenIds.add(id);
    }
    public void addChildren(ArrayList <Integer> ids) {
        childrenIds.addAll(ids);
    }
    public void addParent(int id) {
        parentIds.add(id);
    }
    public void addSpouse(int id) {
        this.spouseId = id;
    }

    public String getFirstName(){
        return this.firstName;
    }
    public String getLastName(){
        return this.lastName;
    }
    public int getId(){
        return this.id;
    }
    public LocalDate getBirthDate(){
        return this.birthDate;
    }
    public String getBirthCity(){
        return this.birthCity;
    }
    public ArrayList <Integer> getChildren(){
        return this.childrenIds;
    }
    public ArrayList <Integer> getParents(){
        return this.parentIds;
    }
    public int getSpouseId(){
        return this.spouseId;
    }

    public void removeChild(int id) {
        for (int i = 0; i < childrenIds.size(); i++) {
            if (childrenIds.get(i) == id) {
                childrenIds.remove(i);
                break;
            }
        }
    }

    public void removeParent(int id) {
        for (int i = 0; i < parentIds.size(); i++) {
            if (parentIds.get(i) == id) {
                parentIds.remove(i);
                break;
            }
        }
    }

    public void removeSpouse() { this.spouseId = -1; }

    abstract public void printValues();
}
