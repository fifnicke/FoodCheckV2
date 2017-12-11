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

    private Button btnBack, btnAddToDatabase;
    private EditText foodText;
    private ListView listView;



    List<MealPlanItem> mealPlanItems;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myPlanRef;
    private String userID;

    //Test för recycleview med glide och skit
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;
    private List<Meal> meals;
    private DatabaseReference mDatabase;
    //Test för recycleview med glide och skit

    //Ny skit för mealplan
    private TextView mondayText, tuesdayText,wednesdayText,thursdayText,fridayText,saturdayText,sundayText;
    private TextView mondayLabel, tuesdayLabel,wednesdayLabel,thursdayLabel,fridayLabel,saturdayLabel,sundayLabel;
    private ImageView mondayImage, tuesdayImage,wednesdayImage,thursdayImage, fridayImage, saturdayImage,sundayImage;
    //Ny skit för mealplan

    private String gotId;
    private String gotName;
    private String day;
    private List<Meal> dailyMeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myPlanRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Mealplan" );
        //myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes" );
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID);

        //Ny skit för mealplan
        mondayText = (TextView)findViewById(R.id.mondayName);
        tuesdayText = (TextView)findViewById(R.id.tuesdayName);
        wednesdayText = (TextView)findViewById(R.id.wednesdayName);
        thursdayText = (TextView)findViewById(R.id.thursdayName);
        fridayText = (TextView)findViewById(R.id.fridayName);
        saturdayText = (TextView)findViewById(R.id.saturdayName);
        sundayText = (TextView)findViewById(R.id.sundayName);

        mondayLabel = (TextView)findViewById(R.id.mondayLabel);
        tuesdayLabel = (TextView)findViewById(R.id.tuesdayLabel);
        wednesdayLabel = (TextView)findViewById(R.id.wednesdayLabel);
        thursdayLabel = (TextView)findViewById(R.id.thursdayLabel);
        fridayLabel = (TextView)findViewById(R.id.fridayLabel);
        saturdayLabel = (TextView)findViewById(R.id.saturdayLabel);
        sundayLabel = (TextView)findViewById(R.id.sundayLabel);

        mondayImage = (ImageView)findViewById(R.id.mondayImage);
        tuesdayImage = (ImageView)findViewById(R.id.tuesdayImage);
        wednesdayImage = (ImageView)findViewById(R.id.wednesdayImage);
        thursdayImage = (ImageView)findViewById(R.id.thursdayImage);
        fridayImage = (ImageView)findViewById(R.id.fridayImage);
        saturdayImage = (ImageView)findViewById(R.id.saturdayImage);
        sundayImage = (ImageView)findViewById(R.id.sundayImage);

        //Ny skit för mealplan

        mealPlanItems = new ArrayList<>();





        LinearLayout app_layer_monday = (LinearLayout) findViewById (R.id.linearMonday);
        app_layer_monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "Monday";

                Intent selectMealIntent = new Intent(getApplicationContext(), SelectMealPlanActivity.class);
                //selectMealIntent.putExtra("id", meal.getMealId());
                //selectMealIntent.putExtra("name", meal.getMealName());
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
                //selectMealIntent.putExtra("id", meal.getMealId());
                //selectMealIntent.putExtra("name", meal.getMealName());
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
                //selectMealIntent.putExtra("id", meal.getMealId());
                //selectMealIntent.putExtra("name", meal.getMealName());
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
                //selectMealIntent.putExtra("id", meal.getMealId());
                //selectMealIntent.putExtra("name", meal.getMealName());
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
                //selectMealIntent.putExtra("id", meal.getMealId());
                //selectMealIntent.putExtra("name", meal.getMealName());
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
                //selectMealIntent.putExtra("id", meal.getMealId());
                //selectMealIntent.putExtra("name", meal.getMealName());
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
                //selectMealIntent.putExtra("id", meal.getMealId());
                //selectMealIntent.putExtra("name", meal.getMealName());
                selectMealIntent.putExtra("day", day);
                getApplicationContext().startActivity(selectMealIntent);
            }
        });

        Intent recivedIntent = getIntent();
        //get itemId
        if(recivedIntent.hasExtra("id")){
            gotId = recivedIntent.getStringExtra("id");// -1 is just the default value
            addShoppingItem();
        }
        if(recivedIntent.hasExtra("name")){
            gotName = recivedIntent.getStringExtra("name");// -1 is just the default value
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        day = prefs.getString("selectedDay", "no id"); //no id: default value

        /*
        btnBack = (Button) findViewById(R.id.mealplan_back);
        btnAddToDatabase = (Button) findViewById(R.id.mealplan_add_button);
        foodText = (EditText) findViewById(R.id.mealplan_text);
        listView = (ListView) findViewById(R.id.mealplan_shoppinglist);*/



        //Test för recycleview med glide och skit
        /*
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));*/

        //progressDialog = new ProgressDialog(this);
        meals = new ArrayList<>();
        dailyMeals = new ArrayList<>();

        //progressDialog.setMessage("Please wait...");
        //progressDialog.show();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Test för recycleview med glide och skit


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
        myPlanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //progressDialog.dismiss();
                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount()+ " items");

                //mealPlanItems.clear();
                //testMealPlan.clear();
                //testMealPlan.clear();
                //dataSnapshot.child("Monday").getValue();
                //toastMessage(testMealPlan.toString());
                for(DataSnapshot mealSnapshot: dataSnapshot.getChildren()){
                    //String bah = dataSnapshot.child("Monday").getValue().toString();
                    //toastMessage(bah);
                    //testMealPlan.add(mealSnapshot.getValue().toString());

                    //MealPlanItem pItem = mealSnapshot.getValue(MealPlanItem.class);
                    //mealPlanItems.add(pItem);
                    //toastMessage(pItem.getMonday().toString());
                }
                //toastMessage(testMealPlan.toString());
                //adapter = new RecyclerViewAdapter(getApplicationContext(), meals);
                //recyclerView.setAdapter(adapter);

                //ShoppingList shoppingAdapter = new ShoppingList(MealPlanActivity.this, shoppingItems);
                //listView.setAdapter(shoppingAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //progressDialog.dismiss();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //progressDialog.dismiss();
                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount()+ " items");

                meals.clear();
                testMealPlan.clear();

                for(DataSnapshot mealSnapshot: dataSnapshot.child("Recipes").getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }
                String bv = String.valueOf(meals.size());
                List<String> mealPlanInOrder = new ArrayList<String>();

                for(DataSnapshot mealSnapshot: dataSnapshot.child("Mealplan").getChildren()){
                    testMealPlan.add(mealSnapshot.getValue().toString());
                    mealPlanInOrder.add(mealSnapshot.getKey());
                }
                for(int i = 0; i < mealPlanInOrder.size(); i++){
                        String dagensMeal = testMealPlan.get(i);
                        //mondayText.setText(mealPlanInOrder.get(i));
                }
                for(int i = 0; i < testMealPlan.size(); i++){
                    for(Meal meal: meals){
                        Log.d(TAG, "MealID: "+ meal.getMealId());
                        Log.d(TAG, "testMealID: " +testMealPlan.get(i));
                        if(meal.getMealId().equals(testMealPlan.get(i))){
                            Log.d(TAG, "Kanske funkar" + meal.getMealId() + mealPlanInOrder.get(i));
                            dailyMeals.add(meal);
                        }
                        //Log.d(TAG,meal.getMealName());
                        Log.d(TAG, "Mealplan all meals: " + dailyMeals.size());
                        //Lägg till ett dummy meal så att hela arrayn blir full
                        //Eller typ skapa en array med 2 värden per plats, så dagens namn och mealid finns
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

















                //mondayImage.setIm(dailyMeals.get(1).getMealName());




                Log.d(TAG, mealPlanInOrder.toString());
                Log.d(TAG, testMealPlan.toString());
                String asd = String.valueOf(testMealPlan.size());

                for(int i = 0; i < testMealPlan.size(); i++){
                    String mId = testMealPlan.get(i);
                    for(Meal d : meals){
                        if(d.getMealId() != null && d.getMealId().contains(mId)){

                            //toastMessage("Yay " + d.getMealId());

                        }
                        //something here
                    }
                    /*int indexOfTheMeal = meals.indexOf(mId);
                    String blalaslda = String.valueOf(indexOfTheMeal);
                    toastMessage(blalaslda);*/
                    //meals.add(dataSnapshot.child(mId).getValue(Meal.class));
                }



                //toastMessage(meals.toString());
                /*adapter = new RecyclerViewAdapter(getApplicationContext(), meals);
                recyclerView.setAdapter(adapter);*/

                //ShoppingList shoppingAdapter = new ShoppingList(MealPlanActivity.this, shoppingItems);
                //listView.setAdapter(shoppingAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //progressDialog.dismiss();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        }

    private void addShoppingItem(){
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

        //toast
        private void toastMessage(String message){
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
        }
    }

