import java.util.ArrayList;
import java.util.List;

public class StarsInMovie {
    private String stagename;
    private String title;
    private String director;

    public StarsInMovie(String stagename, String director, String title) {
        this.stagename = stagename;
        this.title = title;
        this.director = director;
    }
    public String getStagename() {
        return stagename;
    }

    public void setStagename(String stagename) {
        this.stagename = stagename;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDir() {
        return director;
    }

    public void setDir(String dir) {
        this.director = dir;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star and Movie Details - ");
        sb.append("title:" + getTitle());
        sb.append(", ");
        sb.append("director:" + getDir());
        sb.append(", ");
        sb.append("star stagename:" + getStagename());;

        return sb.toString();
    }
}
