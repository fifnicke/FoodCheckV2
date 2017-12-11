package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectMealPlanActivity extends AppCompatActivity {

    public static final String TAG = "SelectMealPlanActivity";

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    //List<Meal> meals;

    private EditText selectRecipeSearch;

    private static final String ITEM_ID = "se.ju.students.svni1493.foodcheckv2.itemid";
    private static final String ITEM_NAME = "se.ju.students.svni1493.foodcheckv2.itemname";

    //Test för recycleview med glide och skit
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    //private ProgressDialog progressDialog;
    private List<Meal> meals;

    private String day;
    private String enteredText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_meal_plan);

        meals = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes" );

        //Test för recycleview med glide och skit
        selectRecipeSearch = (EditText) findViewById(R.id.selectRecipeSearch);

        recyclerView = (RecyclerView) findViewById(R.id.selectRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //progressDialog = new ProgressDialog(this);
        meals = new ArrayList<>();


        Intent recivedIntent = getIntent();
        //get itemId
        day = recivedIntent.getStringExtra("day");// -1 is just the default value
        //toastMessage(day);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selectedDay", day); //InputString: from the EditText
        editor.commit();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SelectMealPlanActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //progressDialog.dismiss();
                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount()+ " items");

                meals.clear();

                for(DataSnapshot mealSnapshot: dataSnapshot.getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }

                enteredText = selectRecipeSearch.getText().toString();
                selectRecipeSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        // filter your list from your input
                        //String ssss = s.toString();
                        List<Meal> temp = new ArrayList();
                        for(Meal d: meals){
                            //or use .equal(text) with you want equal match
                            //use .toLowerCase() for better matches
                            if(d.getMealName().contains(s)){
                                temp.add(d);
                            }
                        }
                        adapter = new RecyclerSelectMealAdapter(getApplicationContext(), temp);
                        recyclerView.setAdapter(adapter);
                        //you can use runnable postDelayed like 500 ms to delay search text
                    }
                });

                if(enteredText.isEmpty()  ){
                    adapter = new RecyclerSelectMealAdapter(getApplicationContext(), meals);
                    recyclerView.setAdapter(adapter);
                }

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
    @Override
    protected void onResume()
    {
        super.onResume();
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
