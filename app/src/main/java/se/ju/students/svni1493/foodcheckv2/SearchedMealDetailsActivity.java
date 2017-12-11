package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

public class SearchedMealDetailsActivity extends AppCompatActivity {

    public static final String TAG = "RecipeDetailsActivity";
    Button btnSearchedRecipieDetailsCancel, btnSearchedRecipieDetailsEdit;
    ListView searchedRecipeDetailsIngredientListView;
    TextView searchedRecipeDetailsName, searchedRecipeDetailsinstructionslabel,searchedRecipeDetailsInstructions,searchedRecipeDetailsingredientsLabel;
    ImageView searchedRecipeDetailsImage;


    private String instructions;
    private String ingredients;
    private String[] ingredientArray;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;
    List<IngredientItem> ingredientsItems;
    private ArrayList<String> testList = new ArrayList<>();
    private DatabaseReference listRef;

    private String selectedName;
    private String selectedID;
    private String selectedImage;
    private String selectedHref;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_meal_details);

        searchedRecipeDetailsName = (TextView) findViewById(R.id.searchedRecipeDetailsName);
        searchedRecipeDetailsinstructionslabel = (TextView) findViewById(R.id.searchedRecipeDetailsinstructionslabel);
        searchedRecipeDetailsInstructions = (TextView) findViewById(R.id.searchedRecipeDetailsInstructions);
        searchedRecipeDetailsingredientsLabel = (TextView) findViewById(R.id.searchedRecipeDetailsingredientsLabel);

        searchedRecipeDetailsImage = (ImageView) findViewById(R.id.searchedRecipeDetailsImage);

        btnSearchedRecipieDetailsCancel = (Button) findViewById(R.id.btnSearchedRecipieDetailsCancel);
        btnSearchedRecipieDetailsEdit = (Button) findViewById(R.id.btnSearchedRecipieDetailsEdit);

        searchedRecipeDetailsIngredientListView = (ListView) findViewById(R.id.searchedRecipeDetailsIngredientListView);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes" );
        //myRef = mFirebaseDatabase.getReference();

        ingredientsItems = new ArrayList<>();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(SearchedMealDetailsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        btnSearchedRecipieDetailsCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSearchedRecipieDetailsEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String id = myRef.push().getKey();
                String name = selectedName;
                String instructions = selectedHref;
                String day = "placeholderDay";
                String href = selectedHref;
                String image = selectedImage;
                if(!TextUtils.isEmpty(name)){
                    Meal meal = new Meal(id, name, instructions, arrayList,image, day, href);
                    myRef.child(id).setValue(meal);
                    //toastMessage("Added "+ name + " to firebase!");
                    finish();
                }else {
                    //toastMessage("Something went wrong!");
                }
            }
        });


        searchedRecipeDetailsIngredientListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        //get the intent extras
        Intent recivedIntent = getIntent();
        //get itemId
        selectedID = recivedIntent.getStringExtra("id");// -1 is just the default value
        //toastMessage(String.valueOf(selectedID));
        //get name
        selectedName = recivedIntent.getStringExtra("name");
        //toastMessage(selectedName);

        selectedHref = recivedIntent.getStringExtra("href");

        selectedImage = recivedIntent.getStringExtra("imageUrl");


        arrayList = getIntent().getStringArrayListExtra("list");
        //toastMessage(arrayList.toString());


        //myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes/" +selectedID+"" );

        //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, testList);
        //searchedRecipeDetailsIngredientListView.setAdapter(arrayAdapter);
        updateView();

    }
    private void updateView(){
        searchedRecipeDetailsName.setText(selectedName);
        searchedRecipeDetailsInstructions.setText(selectedHref);
        Glide.with(getApplicationContext()).load(selectedImage).into(searchedRecipeDetailsImage);

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);
        searchedRecipeDetailsIngredientListView.setAdapter(adapter);

    }
    //toast
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
