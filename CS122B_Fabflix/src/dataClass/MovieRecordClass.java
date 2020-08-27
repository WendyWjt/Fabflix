package dataClass;

public class MovieRecordClass {
    public String id = null;
    public String title = null;
    public int year = 0;
    public String director = null;

    public MovieRecordClass(String id, String title, int year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
    }

    public MovieRecordClass(){
    }

    @Override
    public String toString(){
        return "dataClass.MovieRecordClass: " + this.id + " | " + this.title + " | " + this.year + " | " + this.director;
    }

}
