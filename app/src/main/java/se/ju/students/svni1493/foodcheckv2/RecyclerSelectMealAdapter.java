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
 * Created by fifnicke on 2017-12-10.
 */

public class RecyclerSelectMealAdapter extends RecyclerView.Adapter<RecyclerSelectMealAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private String userID;

    Context context;
    List<Meal> meals;

    public RecyclerSelectMealAdapter(Context context, List<Meal> meals) {

        this.meals = meals;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_select_meal, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);;

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
                Intent backIntent = new Intent(context, MealPlanActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                backIntent.putExtra("id", meal.getMealId());
                backIntent.putExtra("name", meal.getMealName());
                context.startActivity(backIntent);
            }
        });
        holder.linearLayoutRecycleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle(meal.getMealName());
                alertDialog.setMessage("Do you want to edit or delete " + meal.getMealName()+"?");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.setNeutralButton("CHANGE MEAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
    //setting the items in the recycleview
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;
        public LinearLayout linearLayoutRecycleView;

        public ViewHolder(View itemView) {

            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.selectMealtextViewName);

            imageView = (ImageView) itemView.findViewById(R.id.selectMealimageView);

            linearLayoutRecycleView = (LinearLayout) itemView.findViewById(R.id.linearLayoutSelectMealRecycleView);
        }
    }
}
