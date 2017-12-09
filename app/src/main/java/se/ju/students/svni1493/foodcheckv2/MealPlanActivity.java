package se.ju.students.svni1493.foodcheckv2;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.Toast;

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

    List<ShoppingItem> shoppingItems;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    //Test för recycleview med glide och skit
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressDialog progressDialog;
    private List<Meal> meals;
    private DatabaseReference mDatabase;
    //Test för recycleview med glide och skit


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_plan);


        shoppingItems = new ArrayList<>();

        btnBack = (Button) findViewById(R.id.mealplan_back);
        btnAddToDatabase = (Button) findViewById(R.id.mealplan_add_button);
        foodText = (EditText) findViewById(R.id.mealplan_text);
        listView = (ListView) findViewById(R.id.mealplan_shoppinglist);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes" );

        //Test för recycleview med glide och skit
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //progressDialog = new ProgressDialog(this);
        meals = new ArrayList<>();

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

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //progressDialog.dismiss();
                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount()+ " items");

                shoppingItems.clear();

                for(DataSnapshot mealSnapshot: dataSnapshot.getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }
                adapter = new RecyclerViewAdapter(getApplicationContext(), meals);
                recyclerView.setAdapter(adapter);

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


        btnAddToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add to database.");
                addShoppingItem();

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        }

        private void addShoppingItem(){
            String name = foodText.getText().toString();
            if(!TextUtils.isEmpty(name)){
                String id = myRef.push().getKey();
                ShoppingItem shoppingItem = new ShoppingItem(id, name);
                myRef.child(id).setValue(shoppingItem);
                foodText.setText("");
                toastMessage("Added "+ name + " to firebase!");
            }else {
                toastMessage("You need to add a name!");
            }

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

