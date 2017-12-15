package se.ju.students.svni1493.foodcheckv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fifnicke on 2017-12-09.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private String userID;

    Context context;
    List<Meal> meals;
    List<String> testLista;
    List<String> weekDays;

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
    //actions for clicking items in recycleview
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Meal meal = meals.get(position);

        holder.textViewName.setText(meal.getMealName());

        Glide.with(context).load(meal.getMealImageUrl()).into(holder.imageView);

        holder.linearLayoutRecycleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recipeDetailsIntent = new Intent(context, RecipeDetailsActivity.class);
                recipeDetailsIntent.putExtra("id", meal.getMealId());
                recipeDetailsIntent.putExtra("name", meal.getMealName());
                context.startActivity(recipeDetailsIntent);
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
                alertDialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        userID = user.getUid();

                        meals.clear();
                        testLista = new ArrayList<>();
                        weekDays = new ArrayList<String>();
                        weekDays.add("Friday");
                        weekDays.add("Monday");
                        weekDays.add("Saturday");
                        weekDays.add("Sunday");
                        weekDays.add("Thursday");
                        weekDays.add("Tuesday");
                        weekDays.add("Wednesday");

                        Log.d("Dagar", weekDays.toString());
                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Mealplan");
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // This method is called once with the initial value and again
                                // whenever data at this location is updated.
                                Log.d("Recycler", "There are: " + dataSnapshot.getChildrenCount() + " items");

                                testLista.clear();

                                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                                    String vals = mealSnapshot.getValue().toString();
                                    Log.d("Skit", vals.toString());
                                    testLista.add(vals);
                                }
                                int testIndex = 0;
                                for (String a : testLista){
                                    if(testLista.contains(meal.getMealId())){
                                        testIndex = testLista.indexOf(meal.getMealId());
                                        testLista.set(testIndex, "Select Meal");
                                        Log.d("Index", String.valueOf(testIndex));
                                        DatabaseReference MealPlanIdRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Mealplan/"+ weekDays.get(testIndex));
                                        MealPlanIdRef.setValue("Select Meal");
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("Recycler", "Failed to read value.", error.toException());
                            }
                        });

                    }
                });
                alertDialog.setNeutralButton("EDIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
    //setting the items in the recycleview
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