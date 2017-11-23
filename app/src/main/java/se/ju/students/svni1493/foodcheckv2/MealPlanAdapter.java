package se.ju.students.svni1493.foodcheckv2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by fifnicke on 2017-11-13.
 */

public class MealPlanAdapter extends ArrayAdapter<MealItem> {
    private static final String TAG = "MealPlanAdapter";

    private Context mContext;
    int mResource;

    public MealPlanAdapter(Context context, int resource, ArrayList<MealItem> objects) {
        super(context, resource, objects);
        mContext = mContext;
        mResource = resource;
    }
/*
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get meal info
        String name = getItem(position).getName();
        String instructions = getItem(position).getInstructions();
        String image = getItem(position).getImage();
        String [] ingredients = getItem(position).getIngredients();

        //create the mealItem object with the info
        MealItem mealItem = new MealItem(name,instructions,image,ingredients);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView tvName = (TextView) convertView.findViewById(R.id.recipeName);
        ImageView ivImage = (ImageView) convertView.findViewById(R.id.mealplanRecipeImage);

        tvName.setText(name);
        //ivImage.setImageDrawable(Drawable.createFromPath(image));

        return convertView;


    }*/
}
