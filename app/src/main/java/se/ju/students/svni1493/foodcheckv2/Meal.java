package se.ju.students.svni1493.foodcheckv2;

import java.util.List;

/**
 * Created by fifnicke on 2017-12-07.
 */

public class Meal {
    public String mealId;
    public String mealName;
    public String mealInstructions;
    public List<String> mealIngredients;
    public String mealImageUrl;
    public String day;
    public String hRef;
    //private String ingredients;
    //private byte[] image;

    public Meal(){
    }
    public Meal(String mealId, String mealName, String mealInstructions, List<String> mealIngredients, String mealImageUrl, String day, String hRef){
        this.mealId = mealId;
        this.mealName = mealName;
        this.mealInstructions = mealInstructions;
        this.mealIngredients = mealIngredients;
        this.mealImageUrl = mealImageUrl;
        this.day = day;
        this.hRef = hRef;
    }

    public String gethRef() {
        return hRef;
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
