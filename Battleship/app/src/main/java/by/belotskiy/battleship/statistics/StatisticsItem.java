package by.belotskiy.battleship.statistics;

public class StatisticsItem {
    private String curUsername;
    private String opponentUsername;
    private String result;
    private String time;

    public StatisticsItem(String curUsername, String opponentUsername, String result, String time) {
        this.curUsername = curUsername;
        this.opponentUsername = opponentUsername;
        this.result = result;
        this.time = time;
    }

    public StatisticsItem() { }

    public String getCurUsername() {
        return curUsername;
    }

    public void setCurUsername(String curUsername) {
        this.curUsername = curUsername;
    }

    public String getOpponentUsername() {
        return opponentUsername;
    }

    public void setOpponentUsername(String opponentUsername) {
        this.opponentUsername = opponentUsername;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
