package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String TAG = "RecipeDetailsActivity";
    Button btnRecipeDetailsCancel, btnRecipeDetailsEdit, btnRecipeDetailsDelete;
    ListView recipeDetailsIngredientListView;
    TextView recipeDetailsInstructions, recipeDetailsName;
    ImageView recipeDetailsImage;

    private String selectedName;
    private String selectedID;
    private String instructions;
    private String ingredients;
    private Blob image;
    private String[] ingredientArray;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    List<IngredientItem> ingredientsItems;

    private ArrayList<String> testList = new ArrayList<>();

    private DatabaseReference listRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipeDetailsInstructions = (TextView) findViewById(R.id.recipeDetailsInstructions);
        recipeDetailsName = (TextView) findViewById(R.id.recipeDetailsName);
        recipeDetailsImage = (ImageView) findViewById(R.id.recipeDetailsImage);
        btnRecipeDetailsEdit = (Button) findViewById(R.id.btnRecipeDetailsEdit);
        btnRecipeDetailsCancel = (Button) findViewById(R.id.btnRecipeDetailsCancel);
        btnRecipeDetailsDelete = (Button) findViewById(R.id.btnRecipeDetailsDelete);
        recipeDetailsIngredientListView = (ListView) findViewById(R.id.recipeDetailsIngredientListView);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        ingredientsItems = new ArrayList<>();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(RecipeDetailsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        //get the intent extras
        Intent recivedIntent = getIntent();
        //get itemId
        selectedID = recivedIntent.getStringExtra("id");// -1 is just the default value
        //get name
        selectedName = recivedIntent.getStringExtra("name");
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes/" +selectedID+"" );
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount()+ " items");

                if(dataSnapshot.getChildrenCount() != 0){
                    Meal meal = dataSnapshot.getValue(Meal.class);
                    recipeDetailsName.setText(meal.getMealName());
                    Glide.with(getApplicationContext()).load(meal.getMealImageUrl()).into(recipeDetailsImage);
                    recipeDetailsInstructions.setText(meal.getMealInstructions());
                }

                //meals.clear();

               /* for(DataSnapshot mealSnapshot: dataSnapshot.getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    //meals.add(meal);
                }*/

                //RecipeList recipeAdapter = new RecipeList(RecipeActivity.this, meals);
                //recipeListView.setAdapter(recipeAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        recipeDetailsIngredientListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testList);
        recipeDetailsIngredientListView.setAdapter(arrayAdapter);

        // Read from the database
        listRef = myRef.child("mealIngredients");
        listRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String value = dataSnapshot.getValue(String.class);
                testList.add(value);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        btnRecipeDetailsCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });
        btnRecipeDetailsDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //write code for deletion of recipe
                //mDatabaseHelper.deleteMeal(selectedID,selectedName);
                finish();
            }
        });
        btnRecipeDetailsEdit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //write code for editing of recipe
            }
        });
    }

    public void updateInfo(){
/*
        recipeDetailsName.setText(meal.getName());
        recipeDetailsInstructions.setText(meal.getIngredients());//gets instructions for some reason
        ingredients = meal.getInstructions();
        ingredientArray = convertStringToArray(ingredients);
        Bitmap convertImage = BitmapFactory.decodeByteArray(meal.getImage(), 0, meal.getImage().length);
        recipeDetailsImage.setImageBitmap(convertImage);*/
    }

    private void populateListView() {

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ingredientArray);
        recipeDetailsIngredientListView.setAdapter(adapter);

    }
    public static String strSeparator = "__,__";
    //function to convert the string of ingredients to an array again
    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }

    //toast
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
