package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final String TAG = "MainActivity";
    private ListView mListView;
    TextView mUserText, dailyMeal;
    ImageView dailyMealImage;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private DatabaseReference myMealRef;
    private DatabaseReference myWeekRef;
    private String userID;

    private List<Meal> meals;
    List<ShoppingItem> shoppingItems;
    private List<String> dailyMeals;
    Meal dM = null;
    String todayM = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = (ListView) findViewById(R.id.shoppingListList);
        shoppingItems = new ArrayList<>();

        dailyMeal = (TextView) findViewById(R.id.meal);
        dailyMealImage = (ImageView) findViewById(R.id.dailyMealImage);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/ShoppingList" );
        myMealRef = FirebaseDatabase.getInstance().getReference("users/"+ userID +"/Recipes" );
        myWeekRef = FirebaseDatabase.getInstance().getReference("users/"+ userID);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        meals = new ArrayList<>();
        dailyMeals = new ArrayList<>();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        mUserText = (TextView)headerView.findViewById(R.id.userName);
        mUserText.setText(user.getEmail());
        final LinearLayout dailyMealMain = (LinearLayout) findViewById (R.id.dailyMealMain);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

                ShoppingList shoppingAdapter = new ShoppingList(MainActivity.this, shoppingItems);
                mListView.setAdapter(shoppingAdapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Read from the database
        myMealRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                meals.clear();

                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        myWeekRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("Mealplan")) {

                }
                else{
                    myWeekRef.child("Mealplan").child("Friday").setValue("Select Meal");
                    myWeekRef.child("Mealplan").child("Monday").setValue("Select Meal");
                    myWeekRef.child("Mealplan").child("Saturday").setValue("Select Meal");
                    myWeekRef.child("Mealplan").child("Sunday").setValue("Select Meal");
                    myWeekRef.child("Mealplan").child("Thursday").setValue("Select Meal");
                    myWeekRef.child("Mealplan").child("Tuesday").setValue("Select Meal");
                    myWeekRef.child("Mealplan").child("Wednesday").setValue("Select Meal");
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //progressDialog.dismiss();
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        // Read from the database
        myWeekRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                dailyMeals.clear();

                for (DataSnapshot mealSnapshot : dataSnapshot.child("Mealplan").getChildren()) {
                    dailyMeals.add(mealSnapshot.getValue().toString());
                }

                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);

                if(dailyMeals.size() == 7){
                    switch (day) {
                        case Calendar.SUNDAY:
                            todayM = dailyMeals.get(3);
                            break;
                        case Calendar.MONDAY:
                            todayM = dailyMeals.get(1);
                            break;
                        case Calendar.TUESDAY:
                            todayM = dailyMeals.get(5);
                            break;
                        case Calendar.WEDNESDAY:
                            todayM = dailyMeals.get(6);
                            break;
                        case Calendar.THURSDAY:
                            todayM = dailyMeals.get(4);
                            break;
                        case Calendar.FRIDAY:
                            todayM = dailyMeals.get(0);
                            break;
                        case Calendar.SATURDAY:
                            todayM = dailyMeals.get(2);
                            break;
                    }
                }

                meals.clear();

                for (DataSnapshot mealSnapshot : dataSnapshot.child("Recipes").getChildren()) {
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }

                for(Meal meal: meals){
                    if(meal.getMealId().equals(todayM)){
                        dM = meal;
                        break;
                    }
                }
                if(dM != null){
                    dailyMealMain.setEnabled(true);
                    dailyMeal.setText(dM.getMealName());
                    Glide.with(getApplicationContext()).load(dM.getMealImageUrl()).into(dailyMealImage);
                }else {
                    dailyMealMain.setEnabled(false);
                    dailyMeal.setText("No meal selected for today");
                    dailyMealImage.setImageResource(R.mipmap.foodcheck_logo);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        dailyMealMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recipeDetailsIntent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
                recipeDetailsIntent.putExtra("id", dM.getMealId());
                recipeDetailsIntent.putExtra("name", dM.getMealName());
                getApplicationContext().startActivity(recipeDetailsIntent);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.nav_mealplan) {
            Intent intent = new Intent(this, MealPlanActivity.class);
            startActivity(intent);
        }  else if (id == R.id.nav_recipes) {
            Intent intent = new Intent(this, RecipeActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_shoppinglist) {
            Intent intent = new Intent(this, ShoppingListActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_maps) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, ToolsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_sign_out) {
            mAuth.signOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    //toast
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
