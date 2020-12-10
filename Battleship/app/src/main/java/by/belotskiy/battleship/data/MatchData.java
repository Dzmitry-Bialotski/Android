package by.belotskiy.battleship.data;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MatchData {
    private String date;
    private String result;
    private Integer shipRemains;


    public MatchData(){
        date = "";
        result = "";
        shipRemains = 0;
    }


    public void setDate(String date){
        this.date = date;
    }
    public void setDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.date = dateFormat.format(Calendar.getInstance().getTime());
    }
    public void setResult(String result){
        this.result = result;
    }
    public void setShipRemains(Integer shipRemains){
        this.shipRemains = shipRemains;
    }

    public String getDate(){
        return date;
    }
    public String getResult(){
        return result;
    }
    public Integer getShipRemains(){
        return shipRemains;
    }
}

