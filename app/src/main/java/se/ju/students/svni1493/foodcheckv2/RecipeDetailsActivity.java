package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Array;
import java.sql.Blob;
import java.util.ArrayList;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String TAG = "RecipeDetailsActivity";
    DatabaseHelper mDatabaseHelper;
    Button btnRecipeDetailsCancel, btnRecipeDetailsEdit, btnRecipeDetailsDelete;
    ListView recipeDetailsIngredientListView;
    TextView recipeDetailsInstructions, recipeDetailsName;
    ImageView recipeDetailsImage;

    private String selectedName;
    private int selectedID;
    private String instructions;
    private String ingredients;
    private Blob image;
    private String[] ingredientArray;
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
        mDatabaseHelper = new DatabaseHelper(this);




        //get the intent extras
        Intent recivedIntent = getIntent();
        //get itemId
        selectedID = recivedIntent.getIntExtra("id", -1);// -1 is just the default value
        //get name
        selectedName = recivedIntent.getStringExtra("name");

        updateInfo();
        populateListView();

        btnRecipeDetailsCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });
        btnRecipeDetailsDelete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                //write code for deletion of recipe
                mDatabaseHelper.deleteMeal(selectedID,selectedName);
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
        MealItem meal = mDatabaseHelper.getItem(selectedID);
        recipeDetailsName.setText(meal.getName());
        recipeDetailsInstructions.setText(meal.getIngredients());//gets instructions for some reason
        ingredients = meal.getInstructions();
        ingredientArray = convertStringToArray(ingredients);
        Bitmap convertImage = BitmapFactory.decodeByteArray(meal.getImage(), 0, meal.getImage().length);
        recipeDetailsImage.setImageBitmap(convertImage);
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
