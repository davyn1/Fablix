import java.util.ArrayList;
import java.util.List;

public class Movies {
    private String title;
    private String dir;
    private int year;
    private List<String> genre;
    public Movies(String title, String dir, int year, List<String> genre) {
        this.title = title;
        this.dir = dir;
        this.year  = year;
        this.genre = genre;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public List<String> getGenres() {
        return genre;
    }

    public void setGenre(String genres) {
        // change the genre to equal the right one
        String g = genres;
        if(genres.equalsIgnoreCase("Dram")){
            g = "Drama";
        } else if(genres.equalsIgnoreCase("Advt")){
            g = "Adventure";
        } else if(genres.equalsIgnoreCase("Susp")){
            g = "Thriller";
        } else if(genres.equalsIgnoreCase("Horr")){
            g = "Horror";
        } else if(genres.equalsIgnoreCase("Docu")){
            g = "Documentary";
        } else if(genres.equalsIgnoreCase("Actn")){
            g = "Action";
        } else if(genres.equalsIgnoreCase("Comd")){
            g = "Comedy";
        } else if(genres.equalsIgnoreCase("Disa")){
            g = "Disaster";
        } else if(genres.equalsIgnoreCase("Noir")){
            g = "Black";
        } else if(genres.equalsIgnoreCase("ScFi")){
            g = "Sci-Fi";
        } else if(genres.equalsIgnoreCase("West")){
            g = "Western";
        } else if(genres.equalsIgnoreCase("Cart")){
            g = "Cartoon";
        } else if(genres.equalsIgnoreCase("Faml")){
            g = "Family";
        } else if(genres.equalsIgnoreCase("Musc")){
            g = "Musical";
        } else if(genres.equalsIgnoreCase("Porn")){
            g = "Pornography";
        } else if(genres.equalsIgnoreCase("Surl")){
            g = "Sureal";
        } else if(genres.equalsIgnoreCase("AvGa")){
            g = "Avant Garde";
        } else if(genres.equalsIgnoreCase("CnR")){
            g = "Cops and Robbers";
        } else if(genres.equalsIgnoreCase("Hist")){
            g = "History";
        } else if(genres.equalsIgnoreCase("Myst")){
            g = "Mystery";
        } else if(genres.equalsIgnoreCase("Romt")){
            g = "Romance";

        } else if(genres.equalsIgnoreCase("Fant")){
            g = "Fantasy";
        }
        if(g != genres){
            this.genre.add(g);
        }
    }

    public int equalMovies(Movies a, Movies b){
        String aT = a.getTitle();
        List<String> aG = a.getGenres();
        String aD = a.getDir();
        int aY = a.getYear();

        String bT = b.getTitle();
        List<String> bG = b.getGenres();
        String bD = b.getDir();
        int bY = b.getYear();

        if(!aT.equalsIgnoreCase(bT)){
            return 0;
        } else if(!aD.equalsIgnoreCase(bD)){
            return 0;
        } else if(aG.equals(bG)){
            return 0;
        } else if(aY != bY){
            return 0;
        }
        return 1;
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("title:" + getTitle());
        sb.append(", ");
        sb.append("dir:" + getDir());
        sb.append(", ");
        sb.append("year:" + getYear());
        sb.append(", ");
        sb.append("genres:" + getGenres());
        sb.append(".");

        return sb.toString();
    }
}
