package se.ju.students.svni1493.foodcheckv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.List;

/**
 * Created by fifnicke on 2017-12-09.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private String userID;

    Context context;
    List<Meal> meals;

    public RecyclerViewAdapter(Context context, List<Meal> meals) {

        this.meals = meals;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Meal meal = meals.get(position);

        holder.textViewName.setText(meal.getMealName());

        Glide.with(context).load(meal.getMealImageUrl()).into(holder.imageView);

        holder.linearLayoutRecycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "You clicked " + meal.getMealName(), Toast.LENGTH_LONG).show();
                Intent recipeDetailsIntent = new Intent(context, RecipeDetailsActivity.class);
                recipeDetailsIntent.putExtra("id", meal.getMealId());
                recipeDetailsIntent.putExtra("name", meal.getMealName());
                context.startActivity(recipeDetailsIntent);
            }
        });
        holder.linearLayoutRecycleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //Toast.makeText(context, "You Longclicked " + meal.getMealName(), Toast.LENGTH_LONG).show();
                //pass the 'context' here
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle(meal.getMealName());
                alertDialog.setMessage("Do you want to edit or delete " + meal.getMealName()+"?");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(context, "Fix delete func " + meal.getMealName(), Toast.LENGTH_LONG).show();

                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        userID = user.getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes").child(meal.getMealId());
                        databaseReference.removeValue();
                        meals.clear();
                        // DO SOMETHING HERE

                    }
                });
                alertDialog.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Toast.makeText(context, "Edit recipe " + meal.getMealName(), Toast.LENGTH_LONG).show();
                        Boolean edit = true;
                        Intent addRecipeIntent = new Intent(context, AddRecipeActivity.class);
                        addRecipeIntent.putExtra("id", meal.getMealId());
                        addRecipeIntent.putExtra("name", meal.getMealName());
                        addRecipeIntent.putExtra("url", meal.getMealImageUrl());
                        addRecipeIntent.putExtra("edit", true);
                        context.startActivity(addRecipeIntent);
                    }
                });

                AlertDialog dialog = alertDialog.create();
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {

        return meals.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;
        public LinearLayout linearLayoutRecycleView;

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            linearLayoutRecycleView = (LinearLayout) itemView.findViewById(R.id.linearLayoutRecycleView);
        }
    }
}