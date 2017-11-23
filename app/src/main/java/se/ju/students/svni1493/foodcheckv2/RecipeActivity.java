package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {

    public static final String TAG = "RecipeActivity";
    DatabaseHelper mDatabaseHelper;
    Button btnRecipeAdd, btnSearch;
    ListView recipeListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        btnRecipeAdd = (Button) findViewById(R.id.btnRecipeAdd);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        recipeListView = (ListView) findViewById(R.id.recipeListView);
        mDatabaseHelper = new DatabaseHelper(this);



        populateListView();

        btnRecipeAdd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(RecipeActivity.this, AddRecipeActivity.class));
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
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
                showMessage("Data: ", buffer.toString());
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        populateListView();
    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private void populateListView() {
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
    }

    //toast
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }

}
