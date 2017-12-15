package se.ju.students.svni1493.foodcheckv2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    EditText addRecipeName,addRecipeInstructions, addIngredientText;
    ImageView addRecipeImage;
    Button addRecipeImageButton, btnRecipeCancel,btnRecipeSave, btnIngredientAdd;
    ListView ingredientListView;

    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    public static final String TAG = "AddRecipeActivity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    List<Meal> meals;

    Uri imageUri;

    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String imagePath;

    private String selectedName;
    private String selectedID;
    private String selectedUrl;
    private Boolean editMode = false;
    private DatabaseReference myMealRef;
    private DatabaseReference listRef;

    private int STORAGE_PERMISSION_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);


        addRecipeName = (EditText) findViewById(R.id.addRecipeName);
        addRecipeInstructions = (EditText) findViewById(R.id.addRecipeInstructions);
        addRecipeImageButton = (Button) findViewById(R.id.addRecipeImageButton);
        btnRecipeCancel = (Button) findViewById(R.id.btnRecipeCancel);
        btnRecipeSave = (Button) findViewById(R.id.btnRecipeSave);
        addRecipeImage = (ImageView) findViewById(R.id.addRecipeImage);
        addIngredientText = (EditText) findViewById(R.id.addIngredientText);
        btnIngredientAdd = (Button) findViewById(R.id.btnIngredientAdd);
        ingredientListView = (ListView) findViewById(R.id.ingredientListView);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes" );
        meals = new ArrayList<>();
        imageUri = null;
        arrayList = new ArrayList<String>();

        //get the intent extras
        Intent recivedIntent = getIntent();
        //get itemId
        selectedID = recivedIntent.getStringExtra("id");
        //get name
        selectedName = recivedIntent.getStringExtra("name");
        //get url
        selectedUrl = recivedIntent.getStringExtra("url");
        if(recivedIntent.hasExtra("edit")){
            editMode = recivedIntent.getExtras().getBoolean("edit");
        }
        //check if user is logged in
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(AddRecipeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        //fix for scrolling
        ingredientListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        myMealRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes/"+selectedID );

        // Read from the database
        myMealRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount()+ " items");
                meals.clear();
                if(editMode){
                    String mName = dataSnapshot.child("mealName").getValue(String.class);
                    String mInstructions= dataSnapshot.child("mealInstructions").getValue(String.class);
                    addRecipeName.setText(mName);
                    addRecipeInstructions.setText(mInstructions);
                    Glide.with(getApplicationContext()).load(selectedUrl).into(addRecipeImage);

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //save button action
        btnRecipeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add to database.");
                addMealItem();

            }
        });
        //cancel button action
        btnRecipeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        onbtnIngredientAddClick();

        //adding ingredients to the view
        adapter = new ArrayAdapter<String>(AddRecipeActivity.this, android.R.layout.simple_list_item_1,arrayList);
        ingredientListView.setAdapter(adapter);

        //actions for longclick in the list
        ingredientListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String item = arrayList.get(i);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle(item);
                alertDialog.setMessage("Delete " + item+"?");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayList.remove(item);
                        adapter.notifyDataSetChanged();
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
                return true;
            }
        });


        // Read ingredients from the database
        listRef = myMealRef.child("mealIngredients");
        listRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(editMode){
                    String value = dataSnapshot.getValue(String.class);
                    arrayList.add(value);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        //add recipe action
        addRecipeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat. checkSelfPermission(AddRecipeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE ) == PackageManager.PERMISSION_GRANTED){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                }else {
                    requestStoragePermission();
                }
            }
        });
    }
    //request permission for storage
    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed!")
                    .setMessage("This permission is needed so that you can get images from your phone and save it to a recipe.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(AddRecipeActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //add ingredient action
    public void onbtnIngredientAddClick() {
        btnIngredientAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
               String result = addIngredientText.getText().toString();
                arrayList.add(result);
                adapter.notifyDataSetChanged();
                addIngredientText.setText("");
            }
        });
    }
    //save the meal
    private void addMealItem(){

        String id = myRef.push().getKey();
        if(filePath != null && !editMode){
            StorageReference childRef = storageRef.child(id);

            UploadTask uploadTask = childRef.putFile(filePath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    imageUri = downloadUri;
                    imagePath = downloadUri.toString();
                    String id = myRef.push().getKey();
                    String image = "placeholder url";
                    String day = "placeholderDay";
                    String name = addRecipeName.getText().toString();
                    String instructions = addRecipeInstructions.getText().toString();
                    String href = "";

                    if(!TextUtils.isEmpty(name)){

                        Meal meal = new Meal(id, name, instructions, arrayList, taskSnapshot.getDownloadUrl().toString(),day, href);
                        myRef.child(id).setValue(meal);
                        addRecipeName.setText("");
                        toastMessage("Recipe "+ name + " added!");
                        finish();
                    }else {
                        toastMessage("You need to add a name!");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddRecipeActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }else if(editMode){
            if(filePath == null){
                id = selectedID;
                String name = addRecipeName.getText().toString();
                String instructions = addRecipeInstructions.getText().toString();
                String day = "placeholderDay";
                String href = "";
                if(!TextUtils.isEmpty(name)){

                    Meal meal = new Meal(id, name, instructions, arrayList,selectedUrl, day, href);
                    myRef.child(id).setValue(meal);
                    addRecipeName.setText("");
                    toastMessage("Recipe "+ name + " added!");
                    finish();
                }else {
                    toastMessage("You need to add a name!");
                }
            }else{
                id = myRef.push().getKey();
                StorageReference childRef = storageRef.child(id);

                UploadTask uploadTask = childRef.putFile(filePath);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        imageUri = downloadUri;
                        imagePath = downloadUri.toString();
                        String id = selectedID;
                        String image = "placeholder url";
                        String day = "placeholderDay";
                        String name = addRecipeName.getText().toString();
                        String instructions = addRecipeInstructions.getText().toString();
                        String href = "";

                        if(!TextUtils.isEmpty(name)){

                            Meal meal = new Meal(id, name, instructions, arrayList, taskSnapshot.getDownloadUrl().toString(),day, href);
                            myRef.child(id).setValue(meal);
                            addRecipeName.setText("");
                            toastMessage("Recipie "+ name + " added!");
                            finish();
                        }else {
                            toastMessage("You need to add a name!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddRecipeActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else{
            toastMessage("Select an image");
        }
    }
    //response for image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST  && resultCode == RESULT_OK && data != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                addRecipeImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //toastmessage function
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}