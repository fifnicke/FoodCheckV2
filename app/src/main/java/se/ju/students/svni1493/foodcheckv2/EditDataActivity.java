package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditDataActivity extends AppCompatActivity {

    private static final String TAG = "EditDataActivity";

    private Button btnSave,btnDelete;
    private EditText editable_item;

    DatabaseHelper mDatabaseHelper;

    private String selectedName;
    private int selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        editable_item = (EditText) findViewById(R.id.editable_item);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        mDatabaseHelper = new DatabaseHelper(this);

        //get the intent extras
        Intent recivedIntent = getIntent();
        //get itemId
        selectedID = recivedIntent.getIntExtra("id", -1);// -1 is just the default value
        //get name
        selectedName = recivedIntent.getStringExtra("name");

        //set the text to show the current selected name
        editable_item.setText(selectedName);

        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String item = editable_item.getText().toString();
                if(!item.equals("")){
                    mDatabaseHelper.updateName(item,selectedID,selectedName);
                    Intent backIntent = new Intent(EditDataActivity.this, ShoppingListActivity.class);
                    backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(backIntent);
                }else {
                    toastMessage("You must enter a name!");
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mDatabaseHelper.deleteName(selectedID,selectedName);
                editable_item.setText("");
                toastMessage("Removed from database.");
                Intent backIntent = new Intent(EditDataActivity.this, ShoppingListActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
            }
        });


    }
    //toast
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
