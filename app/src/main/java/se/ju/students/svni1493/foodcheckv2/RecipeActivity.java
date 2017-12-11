package se.ju.students.svni1493.foodcheckv2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

public class RecipeActivity extends AppCompatActivity {

    public static final String TAG = "RecipeActivity";
    public static String searchWord = null;
    DatabaseHelper mDatabaseHelper;
    Button btnRecipeAdd, btnSearch;
    EditText searchBar;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private String userID;

    public List<Meal> testlista;

    private static final String ITEM_ID = "se.ju.students.svni1493.foodcheckv2.itemid";
    private static final String ITEM_NAME = "se.ju.students.svni1493.foodcheckv2.itemname";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Meal> meals;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        btnRecipeAdd = (Button) findViewById(R.id.btnRecipeAdd);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        mDatabaseHelper = new DatabaseHelper(this);
        searchBar = (EditText) findViewById(R.id.searchBar);

        meals = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference("users/" + userID + "/Recipes");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        meals = new ArrayList<>();
        testlista = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(RecipeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnRecipeAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(RecipeActivity.this, AddRecipeActivity.class));
                //meals.clear();
                //searchBar.setText("");
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                searchWord = searchBar.getText().toString();
                if (!searchWord.equals("")) {
                    new getRecipes().execute();
                }
            }
        });

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "There are: " + dataSnapshot.getChildrenCount() + " items");

                meals.clear();

                for (DataSnapshot mealSnapshot : dataSnapshot.getChildren()) {
                    Meal meal = mealSnapshot.getValue(Meal.class);
                    meals.add(meal);
                }
                searchWord = searchBar.getText().toString();
                searchBar.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        // filter your list from your input
                        //String ssss = s.toString();
                        List<Meal> temp = new ArrayList();
                        for(Meal d: meals){
                            //or use .equal(text) with you want equal match
                            //use .toLowerCase() for better matches
                            if(d.getMealName().contains(s)){
                                temp.add(d);
                            }
                        }
                        adapter = new RecyclerViewAdapter(getApplicationContext(), temp);
                        recyclerView.setAdapter(adapter);
                        //you can use runnable postDelayed like 500 ms to delay search text
                    }
                });

                if(searchWord.isEmpty()  ){
                    adapter = new RecyclerViewAdapter(getApplicationContext(), meals);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void getListFromJson(List<Meal> jsonList) {
        //toastMessage(jsonList.toString());
        testlista = jsonList;
        adapter = new SearchedMealRecycleViewAdapter(getApplicationContext(), testlista);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    class getRecipes extends AsyncTask<Void, Void, String> {
        public String search = RecipeActivity.searchWord;
        public String API_URL = "https://api.edamam.com/search?q=" + search + "&app_id=537abe49&app_key=354a93a038c88fe91d320861c43b78d3&from=0&to=10";
        List<Meal> recipes = new ArrayList<>();
        private Exception exception;


        protected void onPreExecute() {
        }


        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        protected void onPostExecute(String response) {
            if (response == null) {
                response = "ERROR";
            }
            Log.i("info", response);
            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray hits = object.getJSONArray("hits");

                for (int i = 0; i < hits.length(); i++) {
                    JSONObject hitObject = hits.getJSONObject(i);
                    JSONObject recipe = hitObject.getJSONObject("recipe");
                    Meal result = new Meal();
                    result.mealName = (String) recipe.get("label");
                    result.hRef = recipe.getString("url");
                    result.mealImageUrl = recipe.getString("image");
                    JSONArray ingredients = recipe.getJSONArray("ingredientLines");
                    List<String> temp = new ArrayList<String>();
                    if (ingredients != null) {
                        for (int j = 0; j < ingredients.length(); j++) {
                            temp.add(ingredients.getString(j));
                        }
                        result.mealIngredients = temp;
                    }
                    recipes.add(i, result);

                }

                getListFromJson(recipes);

            } catch (JSONException j) {
                j.printStackTrace();
            }

        }

    }
}
