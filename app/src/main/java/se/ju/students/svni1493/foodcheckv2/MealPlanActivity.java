package se.ju.students.svni1493.foodcheckv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealPlanActivity extends AppCompatActivity {

    private static final String TAG = "MealPlanActivity";
    private static final String ITEM_ID = "se.ju.students.svni1493.foodcheckv2.itemid";
    private static final String ITEM_NAME = "se.ju.students.svni1493.foodcheckv2.itemname";

    List<MealPlanItem> mealPlanItems;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myPlanRef;
    private String userID;


    private List<Meal> meals;

    private TextView mondayText, tuesdayText,wednesdayText,thursdayText,fridayText,saturdayText,sundayText;
    private ImageView mondayImage, tuesdayImage,wednesdayImage,thursdayImage, fridayImage, saturdayImage,sundayImage;

    private String gotId;
    private String gotName;
    private String day;
    private List<Meal> dailyMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myPlanRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Mealplan" );
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID);

        mondayText = (TextView)findViewById(R.id.mondayName);
        tuesdayText = (TextView)findViewById(R.id.tuesdayName);
        wednesdayText = (TextView)findViewById(R.id.wednesdayName);
        thursdayText = (TextView)findViewById(R.id.thursdayName);
        fridayText = (TextView)findViewById(R.id.fridayName);
        saturdayText = (TextView)findViewById(R.id.saturdayName);
        sundayText = (TextView)findViewById(R.id.sundayName);

        mondayImage = (ImageView)findViewById(R.id.mondayImage);
        tuesdayImage = (ImageView)findViewById(R.id.tuesdayImage);
        wednesdayImage = (ImageView)findViewById(R.id.wednesdayImage);
        thursdayImage = (ImageView)findViewById(R.id.thursdayImage);
        fridayImage = (ImageView)findViewById(R.id.fridayImage);
        saturdayImage = (ImageView)findViewById(R.id.saturdayImage);
        sundayImage = (ImageView)findViewById(R.id.sundayImage);


        mealPlanItems = new ArrayList<>();

        //actions for the different days in the mealplan
        LinearLayout app_layer_monday = (LinearLayout) findViewById (R.id.linearMonday);
        app_layer_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Monday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });
        LinearLayout app_layer_tuesday = (LinearLayout) findViewById (R.id.linearTuesday);
        app_layer_tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Tuesday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });
        LinearLayout app_layer_wednesday = (LinearLayout) findViewById (R.id.linearWednesday);
        app_layer_wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Wednesday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });
        LinearLayout app_layer_thursday = (LinearLayout) findViewById (R.id.linearThursday);
        app_layer_thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Thursday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });
        LinearLayout app_layer_friday = (LinearLayout) findViewById (R.id.linearFriday);
        app_layer_friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Friday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });
        LinearLayout app_layer_saturday = (LinearLayout) findViewById (R.id.linearSaturday);
        app_layer_saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Saturday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });
        LinearLayout app_layer_sunday = (LinearLayout) findViewById (R.id.linearSunday);
        app_layer_sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Sunday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });

        Intent recivedIntent = getIntent();
        //get itemId
        if(recivedIntent.hasExtra("id")){
            gotId = recivedIntent.getStringExtra("id");
            addMealItem();
        }
        if(recivedIntent.hasExtra("name")){
            gotName = recivedIntent.getStringExtra("name");
        }
        //store the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        day = prefs.getString("selectedDay", "no id");

        meals = new ArrayList<>();
        dailyMeals = new ArrayList<>();

        //check uf user is valid
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MealPlanActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        final List<String> testMealPlan = new ArrayList<String>();
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                meals.clear();
                testMealPlan.clear();

                for(DataSnapshot mealSnapshot: dataSnapshot.child("Recipes").getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }
                List<String> mealPlanInOrder = new ArrayList<String>();

                for(DataSnapshot mealSnapshot: dataSnapshot.child("Mealplan").getChildren()){
                    testMealPlan.add(mealSnapshot.getValue().toString());
                    mealPlanInOrder.add(mealSnapshot.getKey());
                }

                Meal dummyMeal = new Meal("", "Select a meal", "", null, "https://firebasestorage.googleapis.com/v0/b/foodcheckproject.appspot.com/o/foodcheck_logo.png?alt=media&token=c181e99a-441e-4e56-ab11-0df9fceb33e1","", "");
                for(int i = 0; i < testMealPlan.size(); i++){
                    int k = 0;
                    for(Meal meal: meals){
                        k++;
                        if(testMealPlan.get(i).equals("Select Meal")){
                            dailyMeals.add(dummyMeal);
                            break;
                        }else {
                            if(testMealPlan.contains(meal.getMealId())){
                                if(k <= meals.size()) {
                                    if(meal.getMealId().equals(testMealPlan.get(i))){
                                        dailyMeals.add(meal);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(dailyMeals.get(1) != null){
                    mondayText.setText(dailyMeals.get(1).getMealName());
                    Glide.with(getApplicationContext()).load(dailyMeals.get(1).getMealImageUrl()).into(mondayImage);
                }
                if(dailyMeals.get(5) != null){
                    tuesdayText.setText(dailyMeals.get(5).getMealName());
                    Glide.with(getApplicationContext()).load(dailyMeals.get(5).getMealImageUrl()).into(tuesdayImage);
                }
                if(dailyMeals.get(6) != null){
                    wednesdayText.setText(dailyMeals.get(6).getMealName());
                    Glide.with(getApplicationContext()).load(dailyMeals.get(6).getMealImageUrl()).into(wednesdayImage);
                }
                if(dailyMeals.get(4) != null){
                    thursdayText.setText(dailyMeals.get(4).getMealName());
                    Glide.with(getApplicationContext()).load(dailyMeals.get(4).getMealImageUrl()).into(thursdayImage);
                }
                if(dailyMeals.get(0) != null){
                    fridayText.setText(dailyMeals.get(0).getMealName());
                    Glide.with(getApplicationContext()).load(dailyMeals.get(0).getMealImageUrl()).into(fridayImage);
                }
                if(dailyMeals.get(2) != null){
                    saturdayText.setText(dailyMeals.get(2).getMealName());
                    Glide.with(getApplicationContext()).load(dailyMeals.get(2).getMealImageUrl()).into(saturdayImage);
                }
                if(dailyMeals.get(3) != null){
                    sundayText.setText(dailyMeals.get(3).getMealName());
                    Glide.with(getApplicationContext()).load(dailyMeals.get(3).getMealImageUrl()).into(sundayImage);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        }
        //add meal item
        private void addMealItem(){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String testDag = prefs.getString("selectedDay", "no id"); //no id: default value
            myPlanRef.child(testDag).setValue(gotId);

        }
        @Override
        public void onStart() {
            super.onStart();
            mAuth.addAuthStateListener(mAuthListener);
        }

        @Override
        public void onStop() {
            super.onStop();
            if (mAuthListener != null) {
                mAuth.removeAuthStateListener(mAuthListener);
            }
        }

        //toastmessage function
        private void toastMessage(String message){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
    }

