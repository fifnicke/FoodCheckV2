package se.ju.students.svni1493.foodcheckv2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class AddRecipeActivity extends AppCompatActivity {

    DatabaseHelper mDatabaseHelper;

    EditText addRecipeName,addRecipeInstructions, addIngredientText;
    ImageView addRecipeImage;
    Button addRecipeImageButton, btnRecipeCancel,btnRecipeSave, btnIngredientAdd;
    ListView ingredientListView;

    //arraylist and adapter for ingredients
    ArrayList<String> arrayList;
    ArrayAdapter<String> adapter;



    final int REQUEST_CODE_GALLERY = 999;

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

        addData();
        onbtnIngredientAddClick();

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(AddRecipeActivity.this, android.R.layout.simple_list_item_1,arrayList);
        ingredientListView.setAdapter(adapter);

        btnRecipeCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                finish();
            }
        });

        addRecipeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        AddRecipeActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY

                );
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

    public void addData() {
        btnRecipeSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] test = arrayList.toArray(new String[0]);
                        String convertedArrayOfIngredients = convertArrayToString(test);

                        //lägg till array med ingredienser senare
                        boolean isInserted = mDatabaseHelper.addDataMeal(addRecipeName.getText().toString(), addRecipeInstructions.getText().toString(), imageToByte(addRecipeImage),convertedArrayOfIngredients);
                        if (isInserted == true){
                            toastMessage("Data inserted!");
                        }else {
                            toastMessage("Data not inserted :(");
                        }
                        finish();
                    }
                }
        );
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
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

        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                addRecipeImage.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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