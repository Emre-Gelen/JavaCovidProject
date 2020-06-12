package sample;

import java.util.ArrayList;

public class Regions {
    private String regionName;
    private ArrayList<DateCase> casesOfRegions=new ArrayList<>();

    Regions(String regionName){
        this.regionName=regionName;
    }

    public void addCasesinDays(String date,int cases,int deaths){//Her bir kayıt okunduğunda hangi bölgede ise o bölgeye gün gün veri eklenmesini sağlıyor.
        if(deaths<0){//İspanyadaki hatadan dolayı böyle bir kontrol koymak zorunda kaldım.Eğer ölüm negatif ise pozitife çeviriliyor.
            deaths*=-1;
        }
        if(!dateControl(date)){//Eğer o güne hiç kayıt eklenmemişse ilk önce o gün kayıt ediliyor.
            casesOfRegions.add(new DateCase(date,cases,deaths));
        }
        casesOfRegions.get(dateIndex(date)).addCase(cases,deaths);//Eğer o gün ekli sie gelen veri ile birlikte güncelleniyor.
    }

    public int dateIndex(String date){//Parametre olarak alınan günün hangi indiste olduğunu bize geri dönderiyor.
        int index=-1;
        for(int i=0;i<casesOfRegions.size();i++){
            if(casesOfRegions.get(i).getDate().equals(date)){
                return i;
            }
        }
        return index;
    }

    public boolean dateControl(String date){//Parametre olarak alınan günün daha önceden eklenip eklenmediğini kontrol ediyor.
        boolean var=false;
        for(int i=0;i<casesOfRegions.size();i++){
            if(casesOfRegions.get(i).getDate().equals(date)){
                return true;
            }
        }
        return var;
    }

    public ArrayList<DateCase> getCasesOfRegions() {
        return casesOfRegions;
    }

    public String getRegionName() {
        return regionName;
    }
}
