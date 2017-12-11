package se.ju.students.svni1493.foodcheckv2;

import java.util.List;

/**
 * Created by fifnicke on 2017-12-07.
 */

public class Meal {
    private String mealId;
    private String mealName;
    private String mealInstructions;
    private List<String> mealIngredients;
    private String mealImageUrl;
    private String day;
    //private String ingredients;
    //private byte[] image;

    public Meal(){
    }
    public Meal(String mealId, String mealName, String mealInstructions, List<String> mealIngredients, String mealImageUrl, String day){
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealInstructions = mealInstructions;
        this.mealIngredients = mealIngredients;
        this.mealImageUrl = mealImageUrl;
        this.day = day;
    }

    public String getMealId() {
        return mealId;
    }

    public List<String> getMealIngredients() {
        return mealIngredients;
    }

    public String getMealImageUrl() {
        return mealImageUrl;
    }

    public String getMealName() {
        return mealName;
    }

    public String getMealInstructions() {
        return mealInstructions;
    }

    public String getDay() {
        return day;
    }
}
