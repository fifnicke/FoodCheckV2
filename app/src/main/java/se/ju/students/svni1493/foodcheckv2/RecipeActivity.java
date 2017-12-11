package se.ju.students.svni1493.foodcheckv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    public static final String TAG = "RecipeActivity";
    DatabaseHelper mDatabaseHelper;
    Button btnRecipeAdd, btnSearch;
    ListView recipeListView;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    //List<Meal> meals;

    private static final String ITEM_ID = "se.ju.students.svni1493.foodcheckv2.itemid";
    private static final String ITEM_NAME = "se.ju.students.svni1493.foodcheckv2.itemname";

    //Test för recycleview med glide och skit
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    //private ProgressDialog progressDialog;
    private List<Meal> meals;
    private DatabaseReference mDatabase;
    //Test för recycleview med glide och skit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        btnRecipeAdd = (Button) findViewById(R.id.btnRecipeAdd);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        //recipeListView = (ListView) findViewById(R.id.recipeListView);
        mDatabaseHelper = new DatabaseHelper(this);

        meals = new ArrayList<>();

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
                    startActivity(new Intent(RecipeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnRecipeAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(RecipeActivity.this, AddRecipeActivity.class));
                meals.clear();
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                /*
               Cursor data = mDatabaseHelper.getAllMealData();
                if (data.getCount() == 0){
                    toastMessage("DB is empty");
                    return;
                }

                StringBuffer buffer = new StringBuffer();
                while(data.moveToNext()){
                    buffer.append("ID: "+ data.getString(0)+ "\n");
                    buffer.append("Name: "+ data.getString(1)+ "\n");
                    //buffer.append("Instructions: "+ data.getString(2)+ "\n");
                    buffer.append("Image: "+ data.getString(3)+ "\n");
                    buffer.append("Ingredients: "+ data.getString(4)+ "\n");
                }
                showMessage("Data: ", buffer.toString());*/
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
/*
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount()+ " items");

                meals.clear();

                for(DataSnapshot mealSnapshot: dataSnapshot.getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }

                RecipeList recipeAdapter = new RecipeList(RecipeActivity.this, meals);
                recipeListView.setAdapter(recipeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/
/*
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You clicked on "+ name);
                Meal meal = meals.get(i);
                toastMessage(meal.getMealName());
                toastMessage(meal.getMealId().toString());


                Intent recipeDetailsIntent = new Intent(RecipeActivity.this, RecipeDetailsActivity.class);
                recipeDetailsIntent.putExtra("id", meal.getMealId());
                recipeDetailsIntent.putExtra("name", meal.getMealName());
                startActivity(recipeDetailsIntent);

                    //Write code that intents to recipe details activity

            }
        });*/
        /*
        recipeListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingItem shoppingItem = shoppingItems.get(i);
                displayEditDialog(shoppingItem.getItemId(), shoppingItem.getItemName());
                return true;
            }
        });*/
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    /*
    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }*/

    /*private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get data and append to a list
        Cursor data = mDatabaseHelper.getAllMealData();
        ArrayList<String> listData = new ArrayList<>();

        while(data.moveToNext()){
            //get the value from the database in column 1 and add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create a listadapter and set it
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        recipeListView.setAdapter(adapter);

        //set a clicklistener to the listview
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                String name = adapterView.getItemAtPosition(i).toString();
                Log.d(TAG, "onItemClick: You clicked on "+ name);

                Cursor data = mDatabaseHelper.getMealItemID(name);//returns id of that name
                int itemID = -1;//error handling
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID > -1){
                    Log.d(TAG, "onItemClick: The ID is: " + itemID);
                    Intent recipeDetailsIntent = new Intent(RecipeActivity.this, RecipeDetailsActivity.class);
                    recipeDetailsIntent.putExtra("id", itemID);
                    recipeDetailsIntent.putExtra("name", name);
                    startActivity(recipeDetailsIntent);
                    toastMessage("It works!"+ name);
                    //Write code that intents to recipe details activity
                }else {
                    toastMessage("No ID associated with that name");
                }
            }
        });
    }*/


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
