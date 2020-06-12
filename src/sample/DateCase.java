package sample;

public class DateCase {
    private String date;
    private int deaths;
    private int cases;

    DateCase(String date, int cases, int deaths){
        this.date=date;
        this.deaths=deaths;
        this.cases=cases;
    }
    public void addCase(int cases,int deaths){
        this.cases+=cases;
        this.deaths+=deaths;
    }
    public int getCases() {
        return cases;
    }

    public int getDeaths() {
        return deaths;
    }

    public String getDate() {
        return date;
    }

}
