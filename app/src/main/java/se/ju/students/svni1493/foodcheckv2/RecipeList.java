package se.ju.students.svni1493.foodcheckv2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by fifnicke on 2017-12-07.
 */

public class RecipeList extends  ArrayAdapter<Meal> {
    private Activity context;
    List<Meal> mealItems;
    Bitmap testImage;

    public RecipeList(Activity context, List<Meal> mealItems){
        super(context, R.layout.layout_recipe_list, mealItems);
        this.context = context;
        this.mealItems = mealItems;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_recipe_list, null, true);

        TextView recipeListName = (TextView) listViewItem.findViewById(R.id.recipeListName);
        final ImageView recipeListImage = (ImageView) listViewItem.findViewById(R.id.recipeListImage);

        Meal m = mealItems.get(position);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(m.getMealId());

        //getting images and setting them to view
        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    recipeListImage.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {}

        recipeListName.setText(m.getMealName());

        return listViewItem;
    }
}
