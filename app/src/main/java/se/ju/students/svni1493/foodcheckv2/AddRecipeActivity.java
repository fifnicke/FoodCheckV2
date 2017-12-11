package se.ju.students.svni1493.foodcheckv2;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

    DatabaseHelper mDatabaseHelper;

    EditText addRecipeName,addRecipeInstructions, addIngredientText;
    ImageView addRecipeImage;
    Button addRecipeImageButton, btnRecipeCancel,btnRecipeSave, btnIngredientAdd;
    ListView ingredientListView;

    //arraylist and adapter for ingredients
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;

    //List<ShoppingItem> shoppingItems;

    public static final String TAG = "AddRecipeActivity";
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    List<Meal> meals;

    private static final String ITEM_ID = "se.ju.students.svni1493.foodcheckv2.itemid";
    private static final String ITEM_NAME = "se.ju.students.svni1493.foodcheckv2.itemname";



    final int REQUEST_CODE_GALLERY = 999;
    Uri imageUri;
    private StorageReference mStorageRef;

    int PICK_IMAGE_REQUEST = 111;
    Uri filePath;
    ProgressDialog pd;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String imagePath;

    private String selectedName;
    private String selectedID;
    private String selectedUrl;
    private Boolean editMode = false;
    private DatabaseReference myMealRef;
    private DatabaseReference listRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        mDatabaseHelper = new DatabaseHelper(this);

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
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes" );

        mStorageRef = FirebaseStorage.getInstance().getReference();

        meals = new ArrayList<>();
        imageUri = null;

        arrayList = new ArrayList<String>();

        //get the intent extras

        Intent recivedIntent = getIntent();
        //get itemId
        selectedID = recivedIntent.getStringExtra("id");// -1 is just the default value
        //toastMessage(String.valueOf(selectedID));
        //get name
        selectedName = recivedIntent.getStringExtra("name");

        selectedUrl = recivedIntent.getStringExtra("url");
        if(recivedIntent.hasExtra("edit")){
            editMode = recivedIntent.getExtras().getBoolean("edit");
        }


        //toastMessage(selectedName);


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

        /* Options for edit and delete in listview
        ingredientListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingItem shoppingItem = shoppingItems.get(i);
                displayEditDialog(shoppingItem.getItemId(), shoppingItem.getItemName());
                return true;
            }
        });*/
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
                    //arrayList = dataSnapshot.getChildren("mealIngredients");
                    Glide.with(getApplicationContext()).load(selectedUrl).into(addRecipeImage);
                    //toastMessage("Receptnamn: "+ namnet);
                }

                /*
                for(DataSnapshot mealSnapshot: dataSnapshot.getChildren()){
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }
                toastMessage("Det funka nog");
                if(editMode){

                }*/

                //ShoppingList shoppingAdapter = new ShoppingList(ShoppingListActivity.this, shoppingItems);
                //listView.setAdapter(shoppingAdapter);*/

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        btnRecipeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Attempting to add to database.");
                addMealItem();

            }
        });
        btnRecipeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        onbtnIngredientAddClick();

        adapter = new ArrayAdapter<String>(AddRecipeActivity.this, android.R.layout.simple_list_item_1,arrayList);
        ingredientListView.setAdapter(adapter);

        // Read from the database
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



        btnRecipeCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });

        addRecipeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ActivityCompat.checkSelfPermission(AddRecipeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddRecipeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
                    } else {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                        //Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Intent intent = new Intent();
                //intent.setType("image/*");
                //intent.setAction(Intent.ACTION_PICK);
                //startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });
    }


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
                    //System.out.println(imagePath);
                    String id = myRef.push().getKey();
                    String image = "placeholder url";
                    String day = "placeholderDay";
                    //String id = myRef.push().getKey();
                    String name = addRecipeName.getText().toString();
                    String instructions = addRecipeInstructions.getText().toString();
                    String href = "www";

                    if(!TextUtils.isEmpty(name)){

                        Meal meal = new Meal(id, name, instructions, arrayList, taskSnapshot.getDownloadUrl().toString(),day, href);
                        myRef.child(id).setValue(meal);
                        addRecipeName.setText("");
                        //Message("Added "+ name + " to firebase!");
                        finish();
                    }else {
                        toastMessage("You need to add a name!");
                    }
                    //Toast.makeText(AddRecipeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(AddRecipeActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }else if(editMode){
            if(filePath == null){
                id = selectedID;
                String name = addRecipeName.getText().toString();
                String instructions = addRecipeInstructions.getText().toString();
                String day = "placeholderDay";
                String href = "www";
                if(!TextUtils.isEmpty(name)){

                    Meal meal = new Meal(id, name, instructions, arrayList,selectedUrl, day, href);
                    myRef.child(id).setValue(meal);
                    addRecipeName.setText("");
                    //toastMessage("Added "+ name + " to firebase!");
                    finish();
                }else {
                    //toastMessage("You need to add a name!");
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
                        //System.out.println(imagePath);
                        String id = selectedID;
                        String image = "placeholder url";
                        String day = "placeholderDay";
                        //String id = myRef.push().getKey();
                        String name = addRecipeName.getText().toString();
                        String instructions = addRecipeInstructions.getText().toString();
                        String href = "www";

                        if(!TextUtils.isEmpty(name)){

                            Meal meal = new Meal(id, name, instructions, arrayList, taskSnapshot.getDownloadUrl().toString(),day, href);
                            myRef.child(id).setValue(meal);
                            addRecipeName.setText("");
                            //toastMessage("Added "+ name + " to firebase!");
                            finish();
                        }else {
                            //toastMessage("You need to add a name!");
                        }
                        //Toast.makeText(AddRecipeActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Toast.makeText(AddRecipeActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                    }
                });

            }


        } else{
            toastMessage("Select an image");
        }

        //System.out.println(imagePath);
        //Uri kiss = imageUri;
        //String bajs = kiss.toString();
        //System.out.println(bajs);
/*
        if(!TextUtils.isEmpty(name)){

            Meal meal = new Meal(id, name, instructions, arrayList, image);
            myRef.child(id).setValue(meal);
            addRecipeName.setText("");
            toastMessage("Added "+ name + " to firebase!");
            finish();
        }else {
            toastMessage("You need to add a name!");
        }*/
        /*
        storageRef.child("users/me/profile.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/

    }


    //func to convert image to byte[]
    public static byte[] imageToByte(ImageView image){
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //Code for getting image from gallery
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);*/
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST  && resultCode == RESULT_OK && data != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                addRecipeImage.setImageBitmap(bitmap);

                //Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //addRecipeImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //function to convert ingredientArray to a string so it can be stored in DB
    public static String strSeparator = "__,__";
    public static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

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