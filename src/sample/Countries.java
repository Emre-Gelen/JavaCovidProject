package sample;

import java.util.ArrayList;


public class Countries {
    private String countryName;
    private String countryTag;
    private ArrayList<DateCase> casesByDay=new ArrayList<>();
    private ArrayList<DateCase> totalCasesbyDay=new ArrayList<>();
    private String region;
    private int totalCases=0;
    private int newCases;
    private int totalDeaths=0;
    private int newDeaths;
    private long population;
    private double mortality;
    private double attackRate;

    Countries(String countryName,String countryTag,String region,long population){
        this.countryName=countryName;
        this.countryTag=countryTag;
        this.region=region;
        this.population=population;

    }
    public void calculateMortAR(){
        this.mortality=(double)totalDeaths/totalCases;
        this.attackRate=(double)totalCases/population;


    }
    public void setTotalCasesbyDay(){//O güne kadar olan vakaların ve ölümlerin oluşturulan diziye eklenmesini sağlıyor.
        for(int i=0;i<casesByDay.size();i++){
            int totcase=0;
            int totdeaths=0;
            String date;
            date=casesByDay.get(i).getDate();
            for(int j=casesByDay.size()-1;j>=i;j--){//O güne kadar olan vakaları ve ölümleri topluyor.Mesela bugünün verisi için ilk veri girilen tarihten itibaren bugüne kadar geliyor ve üstünden geçtiği her indisteki vaka ve ölümelri topluyor.
                totcase+=casesByDay.get(j).getCases();
                totdeaths+=casesByDay.get(j).getDeaths();
            }
            totalCasesbyDay.add(new DateCase(date,totcase,totdeaths));
        }
    }
    public void addData(String date,int cases,int deaths){//Gün gün vaka ve ölümler bu fonksiyon yardımı ile ekleniyor.Eklenen her veri ile toplam ölüm toplam vaka sayısıda güncelleniyor.
        casesByDay.add(new DateCase(date,cases,deaths));
        totalCases+=cases;
        totalDeaths+=deaths;
        newCases=casesByDay.get(0).getCases();
        newDeaths=casesByDay.get(0).getDeaths();
    }

    public ArrayList<DateCase> getCasesByDay() {
        return casesByDay;
    }

    public int getNewCases() {
        return newCases;
    }

    public int getNewDeaths() {
        return newDeaths;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public String getRegion() {
        return region;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryTag() {
        return countryTag;
    }

    public ArrayList<DateCase> getTotalCasesbyDay() {
        return totalCasesbyDay;
    }

    public double getAttackRate() {
        return attackRate;
    }

    public double getMortality() {
        return mortality;
    }

    public long getPopulation() {
        return population;
    }
}
