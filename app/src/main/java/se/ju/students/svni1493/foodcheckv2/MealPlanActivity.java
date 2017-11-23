package se.ju.students.svni1493.foodcheckv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class MealPlanActivity extends AppCompatActivity {
    private static final String TAG = "MealPlanActivity";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);



        Log.d(TAG, "OnCreate: Startred.");
        String[] testForIngredients = {"Korv", "Bröd", "Mjölk"};
        ListView mealPlanList = (ListView) findViewById(R.id.meal_plan_list_view);

        //Test for putting placeholder stuff in the mealplan
        /*
        MealItem test = new MealItem("Korv med Bröd", "Heat korv. Put korv in bun", "@drawable/ic_menu_recipes", testForIngredients);
        MealItem test2 = new MealItem("mat2", "Heat korv. Put korv in bun", "@drawable/ic_menu_recipes", testForIngredients);
        MealItem test3 = new MealItem("Mat3", "Heat korv. Put korv in bun", "@drawable/ic_menu_recipes", testForIngredients);
        MealItem test4 = new MealItem("Mat4", "Heat korv. Put korv in bun", "@drawable/ic_menu_recipes", testForIngredients);
        MealItem test5 = new MealItem("Mat5", "Heat korv. Put korv in bun", "@drawable/ic_menu_recipes", testForIngredients);
        MealItem test6 = new MealItem("Mat6", "Heat korv. Put korv in bun", "@drawable/ic_menu_recipes", testForIngredients);
        MealItem test7 = new MealItem("Mat7", "Heat korv. Put korv in bun", "@drawable/ic_menu_recipes", testForIngredients);

        //Add the mealitem objects to an arraylist
        ArrayList<MealItem> mealPlan = new ArrayList<>();
        mealPlan.add(test);
        mealPlan.add(test2);
        mealPlan.add(test3);
        mealPlan.add(test4);
        mealPlan.add(test5);
        mealPlan.add(test6);
        mealPlan.add(test7);

        MealPlanAdapter adapter = new MealPlanAdapter(this, R.layout.mealplan_list_view_item, mealPlan);
        mealPlanList.setAdapter(adapter);
    }*/
        /*public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            //test for mealplan view
            Log.d(TAG, "OnCreate: Startred.");
            String[] testForIngredients = {"Korv", "Bröd", "Mjölk"};
            ListView mealPlanList = (ListView) findViewById(R.id.meal_plan_list_view);

            //Test for putting placeholder stuff in the mealplan
            MealItem test = new MealItem("Korv med Bröd", "Heat korv. Put korv in bun", "@drawable/ham", testForIngredients);
            MealItem test2 = new MealItem("mat2", "Heat korv. Put korv in bun", "@drawable/ham", testForIngredients);
            MealItem test3 = new MealItem("Mat3", "Heat korv. Put korv in bun", "@drawable/ham", testForIngredients);
            MealItem test4 = new MealItem("Mat4", "Heat korv. Put korv in bun", "@drawable/ham", testForIngredients);
            MealItem test5 = new MealItem("Mat5", "Heat korv. Put korv in bun", "@drawable/ham", testForIngredients);
            MealItem test6 = new MealItem("Mat6", "Heat korv. Put korv in bun", "@drawable/ham", testForIngredients);
            MealItem test7 = new MealItem("Mat7", "Heat korv. Put korv in bun", "@drawable/ham", testForIngredients);

            //Add the mealitem objects to an arraylist
            ArrayList<MealItem> mealPlan = new ArrayList<>();
            mealPlan.add(test);
            mealPlan.add(test2);
            mealPlan.add(test3);
            mealPlan.add(test4);
            mealPlan.add(test5);
            mealPlan.add(test6);
            mealPlan.add(test7);

            MealPlanAdapter adapter = new MealPlanAdapter(this, R.layout.mealplan_list_view_item, mealPlan);
            mealPlanList.setAdapter(adapter);

*/
        }
    }

