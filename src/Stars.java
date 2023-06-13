import java.util.ArrayList;
import java.util.List;

public class Stars {
    private String stagename;
    private int year;

    public Stars(String stagename, int year) {
        this.stagename = stagename;
        this.year  = year;
    }
    public String getStagename() {
        return stagename;
    }

    public void setStagename(String stagename) {
        this.stagename = stagename;
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Star Details - ");
        sb.append("stagename:" + getStagename());
        sb.append(", ");
        int year = getYear();
        if (year == 0){
            sb.append("year: null");
        } else{
            sb.append("year:" + year);
        }

        return sb.toString();
    }
}
