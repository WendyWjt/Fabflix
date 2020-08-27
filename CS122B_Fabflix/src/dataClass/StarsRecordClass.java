package dataClass;

public class StarsRecordClass {
    public String name = null;
    public int birthYear = 0;

    public StarsRecordClass(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public StarsRecordClass(){

    }

    @Override
    public String toString(){
        return "dataClass.StarsRecordClass: " + " | " + this.name + " | " + this.birthYear ;
    }

}
