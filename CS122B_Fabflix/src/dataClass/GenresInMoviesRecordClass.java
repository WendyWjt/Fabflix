package dataClass;

public class GenresInMoviesRecordClass {
    public String genreName = null;
    public String movieId = null;
    public String movieName = null;

    public GenresInMoviesRecordClass(String genreName, String movieId, String movieName) {
        this.genreName = genreName;
        this.movieId = movieId;
        this.movieName = movieName;
    }

    public GenresInMoviesRecordClass(){

    }

    @Override
    public String toString(){
        return "dataClass.GenresInMoviesRecordClass: " + this.genreName + " | " + this.movieId + " | "  + this.movieName;
    }

}
