package dataClass;

public class GenresRecordClass {
    public String id = null;
    public String name = null;

    public GenresRecordClass(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public GenresRecordClass(){

    }

    @Override
    public String toString(){
        return "dataClass.GenresRecordClass: " + this.id + " | " + this.name;
    }

}
