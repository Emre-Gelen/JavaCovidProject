package sample;

import com.sun.security.jgss.GSSUtil;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    @FXML
    private CategoryAxis x;
    @FXML
    private NumberAxis y;
    @FXML
    private CategoryAxis x1;
    @FXML
    private TextField txt;
    @FXML
    private TableView<Countries> tableView;
    @FXML
    private ListView listView;
    @FXML
    private LineChart<String ,Number> lineChart;
    @FXML
    private StackedBarChart<String,Number> stackedBarChart;
    @FXML
    private TableColumn<Countries,String> CountryCol;
    @FXML
    private TableColumn<Countries,Integer> TotalCase;
    @FXML
    private TableColumn<Countries,Integer> NewCase;
    @FXML
    private TableColumn<Countries,Integer> TotalDeath;
    @FXML
    private TableColumn<Countries,Integer> NewDeath;
    @FXML
    private TableColumn<Countries,Double> Mortality;
    @FXML
    private TableColumn<Countries,Double>AttackRate;
    @FXML
    private TableColumn<Countries,Integer> Population;

    private int showDays=25;//Kaç günün grafiklerde gösterileceğini buradan değiştirebilirsiniz.Çok fazla gün olunca grafik biraz karışıyor.

    private ObservableList<Countries> countries= FXCollections.observableArrayList();
    private ArrayList<Regions> regions=new ArrayList<>();
    private ObservableList<Countries> copyCountries=FXCollections.observableArrayList();//TableView'da dizinin sıralamasıda değiştiği için sıraladıktan sonra listView'da seçerken
    //sıkıntı oluyordu.Bunu önlemek için dizinin bir kopyasını oluşturdum.


    public void getData() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(txt.getText());

        Element rootElement=document.getDocumentElement();
        NodeList records=rootElement.getElementsByTagName("record");
        for(int i=0;i<records.getLength();i++){
            Element countryElement=(Element) records.item(i);
            //Verileri parçalıyor ve değişkenlere atılıyor.
            String date=countryElement.getElementsByTagName("dateRep").item(0).getTextContent();
            String countryName=countryElement.getElementsByTagName("countriesAndTerritories").item(0).getTextContent();
            String region=countryElement.getElementsByTagName("continentExp").item(0).getTextContent();
            String countryTag=countryElement.getElementsByTagName("countryterritoryCode").item(0).getTextContent();
            int deaths=Integer.parseInt(countryElement.getElementsByTagName("deaths").item(0).getTextContent());
            int cases=Integer.parseInt(countryElement.getElementsByTagName("cases").item(0).getTextContent());
            String population=(countryElement.getElementsByTagName("popData2018").item(0).getTextContent());
            long pop=0;
            if(!population.isEmpty()){//Bazı ülkelerin popülasyonunun 0 olmasında dolayı bir kontrol yapılıyor.
               pop=Long.parseLong(population);
            }

            if(!regCont(region)){//Her bir bölgenin daha önce eklenip eklenmediği kontrol ediliyor.Eğer eklenmemişse diziye ekleniyor.
                regions.add(new Regions(region));
            }
            regions.get(regIndex(region)).addCasesinDays(date,cases,deaths);//Eğer daha önce eklenmişse sadece o kayıttaki veriler ait olduğu bölgeye ekleniyor.
            if(!countryCont(countryName)){//Her bir ülkenin daha önce eklenip eklenmediği kontrol ediliyor.Eğer eklenmemişse diziye ekleniyor.
                copyCountries.add(new Countries(countryName,countryTag,region,pop));
                countries.add(new Countries(countryName,countryTag,region,pop));

            }
            countries.get(countryIndex(countryName)).addData(date,cases,deaths);
            copyCountries.get(countryIndex(countryName)).addData(date,cases,deaths);
        }
        for(int k=0;k<countries.size();k++){//Her bir ülke için yayılma hızı ve ölümcüllük hesaplanıyor.
            copyCountries.get(k).calculateMortAR();
            copyCountries.get(k).setTotalCasesbyDay();
            countries.get(k).calculateMortAR();
            countries.get(k).setTotalCasesbyDay();
        }
        setTableView();
        setlistView();
    }
    public void setLineChart(ObservableList<Integer> choosenCountries){//Alınan ülkeler lineCharta gösteriliyor.
        lineChart.getData().clear();
        ObservableList<String> Categories=FXCollections.observableArrayList();
        ArrayList<XYChart.Series> xyCharts=new ArrayList<>();
        for(int k=showDays;k>=0;k--){
            Categories.add(countries.get(0).getCasesByDay().get(k).getDate());
        }
        for(int i=0;i<choosenCountries.size();i++){//Gösterilmesi istenen ülkelerin sayısına göre XYChart.Series nesneleri oluşturuluyor ve içleri dolduruluyor.
            xyCharts.add(new XYChart.Series<String ,Number>());
            for(int j=showDays;j>=0;j--){
                xyCharts.get(i).getData().add(new XYChart.Data<>(countries.get(choosenCountries.get(i)).getTotalCasesbyDay().get(j).getDate(),
                        countries.get(choosenCountries.get(i)).getTotalCasesbyDay().get(j).getCases()));
                xyCharts.get(i).setName(countries.get(choosenCountries.get(i)).getCountryName());
            }
            lineChart.getData().add(xyCharts.get(i));
        }
        x1.setCategories(Categories);
    }
    public void setLineChartbyDeaths(ObservableList<Integer> choosenCountries){
        lineChart.getData().clear();
        ObservableList<String> Categories=FXCollections.observableArrayList();
        ArrayList<XYChart.Series> xyCharts=new ArrayList<>();
        for(int k=showDays;k>=0;k--){
            Categories.add(countries.get(0).getCasesByDay().get(k).getDate());
        }
        for(int i=0;i<choosenCountries.size();i++){
            xyCharts.add(new XYChart.Series<String ,Number>());
            for(int j=showDays;j>=0;j--){
                xyCharts.get(i).getData().add(new XYChart.Data<>(countries.get(choosenCountries.get(i)).getTotalCasesbyDay().get(j).getDate(),
                        countries.get(choosenCountries.get(i)).getTotalCasesbyDay().get(j).getDeaths()));
                xyCharts.get(i).setName(countries.get(choosenCountries.get(i)).getCountryName());
            }
            lineChart.getData().add(xyCharts.get(i));
        }
        x1.setCategories(Categories);
    }
    public void setlistView(){//ListView e veri yüklenmesini sağlıyor.Ödevde ülkelerin taglerine göre eklenmesi istenmişti fakat öyle çok karışık duruyordu o yüzden isimleri
        //isimleri ile eklemeyi tercih ettim.
        listView.getItems().clear();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for(int i=0;i<countries.size();i++){
            listView.getItems().add(countries.get(i).getCountryName());
        }
    }
    public void setTableView(){//TableView'e veri yüklenmesini sağlıyor.
        CountryCol.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        TotalCase.setCellValueFactory(new PropertyValueFactory<>("totalCases"));
        NewCase.setCellValueFactory(new PropertyValueFactory<>("newCases"));
        TotalDeath.setCellValueFactory(new PropertyValueFactory<>("totalDeaths"));
        NewDeath.setCellValueFactory(new PropertyValueFactory<>("newDeaths"));
        Population.setCellValueFactory(new PropertyValueFactory<>("population"));
        Mortality.setCellValueFactory(new PropertyValueFactory<>("mortality"));
        AttackRate.setCellValueFactory(new PropertyValueFactory<>("attackRate"));

        tableView.setItems(copyCountries);
    }
    public void setStackedBarChart(){
        stackedBarChart.getData().clear();
        ObservableList<String>Categories=FXCollections.observableArrayList();
        ArrayList<XYChart.Series> XYCharts=new ArrayList<>();

        for(int i=showDays;i>=0;i--){
            Categories.add(regions.get(0).getCasesOfRegions().get(i).getDate());
        }
        for(int j=0;j<regions.size();j++){
            XYCharts.add(new XYChart.Series<String,Number>());
            for(int k=showDays;k>=0;k--){
                XYCharts.get(j).getData().add(new XYChart.Data<>(regions.get(j).getCasesOfRegions().get(k).getDate(),regions.get(j).getCasesOfRegions().get(k).getCases()));
                XYCharts.get(j).setName(regions.get(j).getRegionName());
            }
            stackedBarChart.getData().add(XYCharts.get(j));
        }
        x.setCategories(Categories);
    }
    public void setStackedBarChartbyDeaths(){
        stackedBarChart.getData().clear();
        ObservableList<String>Categories=FXCollections.observableArrayList();
        ArrayList<XYChart.Series> XYCharts=new ArrayList<>();

        for(int i=showDays;i>=0;i--){
            Categories.add(regions.get(0).getCasesOfRegions().get(i).getDate());
        }
        for(int j=0;j<regions.size();j++){
            XYCharts.add(new XYChart.Series<String,Number>());
            for(int k=showDays;k>=0;k--){
                XYCharts.get(j).getData().add(new XYChart.Data<>(regions.get(j).getCasesOfRegions().get(k).getDate(),regions.get(j).getCasesOfRegions().get(k).getDeaths()));
                XYCharts.get(j).setName(regions.get(j).getRegionName());
            }
            stackedBarChart.getData().add(XYCharts.get(j));
        }
        x.setCategories(Categories);
    }
    public boolean countryCont(String countryName){//Gönderilen ülkenin ismine göre var olup olmadığını kontrol ediyor.Varsa true değer döndürüyor.
        boolean var=false;
        for(int i=0;i<countries.size();i++){
            if(countries.get(i).getCountryName().equals(countryName)){
                return true;
            }
        }
        return var;
    }

    public int countryIndex(String countryName){
        int index=-1;
        for(int i=0;i<countries.size();i++){
            if(countries.get(i).getCountryName().equals(countryName)){//Gönderilen ülkenin ismine göre hangi indiste olduğunu buluyor ve o indisi dönderiyor.
                return i;
            }
        }
        return index;
    }

    public boolean regCont(String regionName){//Gönderilen bölgenin ismine göre var olup olmadığını kontrol ediyor.Varsa true değer döndürüyor.
        boolean var=false;
        for(int i=0;i<regions.size();i++){
            if(regions.get(i).getRegionName().equals(regionName)){
                return true;
            }
        }
        return var;
    }

    public int regIndex(String regionName){//Gönderilen bölgenin ismine göre hangi indiste olduğunu buluyor ve o indisi dönderiyor.
        int index=-1;
        for(int i=0;i<regions.size();i++){
            if(regions.get(i).getRegionName().equals(regionName)){
                return i;
            }
        }
        return index;
    }

    public void casesButtonClick() throws IOException, SAXException, ParserConfigurationException {
        ObservableList selectedIndices=listView.getSelectionModel().getSelectedIndices();
        setLineChart(selectedIndices);
    }

    public void deathsButtonClick(){
        ObservableList selectedIndices=listView.getSelectionModel().getSelectedIndices();
        setLineChartbyDeaths(selectedIndices);
    }
}
