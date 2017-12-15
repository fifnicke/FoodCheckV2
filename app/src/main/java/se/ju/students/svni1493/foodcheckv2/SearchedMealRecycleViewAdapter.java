package se.ju.students.svni1493.foodcheckv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fifnicke on 2017-12-11.
 */

public class SearchedMealRecycleViewAdapter extends RecyclerView.Adapter<SearchedMealRecycleViewAdapter.ViewHolder>{
    private FirebaseAuth mAuth;
    private String userID;
    Context context;
    List<Meal> meals;

    public SearchedMealRecycleViewAdapter(Context context, List<Meal> meals){
        this.meals = meals;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_searched_meal, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    //actions for clicking items in recycleview
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Meal meal = meals.get(position);

        holder.textViewName.setText(meal.getMealName());

        Glide.with(context).load(meal.getMealImageUrl()).into(holder.imageView);

        holder.linearLayoutRecycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recipeDetailsIntent = new Intent(context, SearchedMealDetailsActivity.class);
                recipeDetailsIntent.putExtra("id", meal.getMealId());
                recipeDetailsIntent.putExtra("name", meal.getMealName());
                recipeDetailsIntent.putExtra("imageUrl", meal.getMealImageUrl());
                recipeDetailsIntent.putExtra("href", meal.gethRef());
                ArrayList<String> tmp = ((ArrayList<String>)meal.getMealIngredients());
                recipeDetailsIntent.putExtra("list", tmp);
                context.startActivity(recipeDetailsIntent);
            }
        });
    }

    //ammount of items
    @Override
    public int getItemCount() {
        return meals.size();
    }

    //setting name and image to card
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;
        public LinearLayout linearLayoutRecycleView;

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.searchedMealtextViewName);

            imageView = (ImageView) itemView.findViewById(R.id.searchedMealimageView);

            linearLayoutRecycleView = (LinearLayout) itemView.findViewById(R.id.linearLayoutSearchedMealRecycleView);
        }
    }

}
