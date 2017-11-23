package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListActivity extends AppCompatActivity {

    public static final String TAG = "ShoppingListActivity";
    DatabaseHelper mDatabaseHelper;
    private Button btnAdd, btnViewData;
    private EditText editText;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        editText = (EditText) findViewById(R.id.editText);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnViewData = (Button) findViewById(R.id.btnView);
        mListView = (ListView) findViewById(R.id.shoppinglistView);
        mDatabaseHelper = new DatabaseHelper(this);

        populateListView();

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String newEntry = editText.getText().toString();
                if(editText.length() != 0){
                    AddData(newEntry);
                    editText.setText("");
                    populateListView();

                }else {
                    toastMessage("You must enter some text!");
                }
            }
        });

        btnViewData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                populateListView();
            }
        });
    }

    public void AddData(String newEntry){
        boolean insertData = mDatabaseHelper.addData(newEntry);
        if (insertData){
            toastMessage("Data Successfully Inserted!");
        }else {
            toastMessage("Something Went Wrong!! Faaaan!");
        }
    }

    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

        //get data and append to a list
        Cursor data = mDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();

        while(data.moveToNext()){
            //get the value from the database in column 1 and add it to the ArrayList
            listData.add(data.getString(1));
        }
        //create a listadapter and set it
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);

        //set a clicklistener to the listview
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
               String name = adapterView.getItemAtPosition(i).toString();
               Log.d(TAG, "onItemClick: You clicked on "+ name);

                Cursor data = mDatabaseHelper.getItemID(name);//returns id of that name
               int itemID = -1;//error handling
               while(data.moveToNext()){
                   itemID = data.getInt(0);
               }
               if(itemID > -1){
                   Log.d(TAG, "onItemClick: The ID is: " + itemID);
                   Intent editScreenIntent = new Intent(ShoppingListActivity.this, EditDataActivity.class);
                   editScreenIntent.putExtra("id", itemID);
                   editScreenIntent.putExtra("name", name);
                   startActivity(editScreenIntent);
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
