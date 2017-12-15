package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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

public class ShoppingListActivity extends AppCompatActivity {

    public static final String TAG = "ShoppingListActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);


        shoppingItems = new ArrayList<>();

        btnBack = (Button) findViewById(R.id.btnBack);
        btnAddToDatabase = (Button) findViewById(R.id.btnAdd);
        foodText = (EditText) findViewById(R.id.editText);
        listView = (ListView) findViewById(R.id.shoppinglistView);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/ShoppingList" );

        //check if user is valid
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ShoppingListActivity.this, LoginActivity.class));
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
                shoppingItems.clear();

                for(DataSnapshot shoppingSnapshot: dataSnapshot.getChildren()){
                    ShoppingItem shoppingItem = shoppingSnapshot.getValue(ShoppingItem.class);
                    shoppingItems.add(shoppingItem);
                }

                ShoppingList shoppingAdapter = new ShoppingList(ShoppingListActivity.this, shoppingItems);
                listView.setAdapter(shoppingAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        //actions for longclicking in shopping list
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingItem shoppingItem = shoppingItems.get(i);
                displayEditDialog(shoppingItem.getItemId(), shoppingItem.getItemName());
                return true;
            }
        });

        //add to database button action
        btnAddToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShoppingItem();

            }
        });
        //cancel button action
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //add shopping item
    private void addShoppingItem(){
        String name = foodText.getText().toString();
        if(!TextUtils.isEmpty(name)){
            String id = myRef.push().getKey();
            ShoppingItem shoppingItem = new ShoppingItem(id, name);
            myRef.child(id).setValue(shoppingItem);
            foodText.setText("");
        }else {
            toastMessage("You need to add a name!");
        }

    }

    //updating the shopping item
    private boolean updateName(String id, String name){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/ShoppingList").child(id);
        ShoppingItem shoppingItem = new ShoppingItem(id,name);
        databaseReference.setValue(shoppingItem);
        return true;
    }
    //deleting the shopping item
    private boolean deleteItem(String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/ShoppingList").child(id);
        databaseReference.removeValue();
        return true;
    }

    //alertdialog
    private void displayEditDialog(final String itemID, String itemName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText mEditText = (EditText) dialogView.findViewById(R.id.editName);
        final Button btnUpdate = (Button) dialogView.findViewById(R.id.buttonUpdate);
        final Button btnDelete = (Button) dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle(itemName);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditText.getText().toString().trim();
                if(!TextUtils.isEmpty(name)){
                    updateName(itemID, name);
                    dialog.dismiss();
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(itemID);
                dialog.dismiss();
            }
        });

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

    //toastmessage function
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }



}
