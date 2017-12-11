package se.ju.students.svni1493.foodcheckv2;

/**
 * Created by fifnicke on 2017-12-10.
 */

public class MealPlanItem {
    private String monday;
    private String tuesday;
    private String wednesday;
    private String thursday;
    private String friday;
    private String saturday;
    private String sunday;

    public MealPlanItem(){}

    public MealPlanItem(String monday, String tuesday,String wednesday,String thursday,String friday,String saturday,String sunday){
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public String getMonday() {
        return monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public String getFriday() {
        return friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public String getSunday() {
        return sunday;
    }
}
