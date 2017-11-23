package se.ju.students.svni1493.foodcheckv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.sql.Blob;

/**
 * Created by fifnicke on 2017-11-15.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "foodcheckDB";

    //Shopping table stuff
    private static final String TABLE_SHOPPING = "shopping_table";
    private static final String COL0 = "ID";
    private static final String COL1 = "name";

    //Meal table stuff
    private static final String TABLE_MEAL = "meal_table";
    private static final String KEY_ID = "ID";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_INGREDIENTS = "ingredients";
    private static final String KEY_INSTRUCTIONS = "instructions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    //Shopping list table create statement
    private static final String CREATE_TABLE_SHOPPING = "CREATE TABLE " + TABLE_SHOPPING + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL1 + " TEXT)";
    private static final String CREATE_TABLE_MEAL = "CREATE TABLE " + TABLE_MEAL + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME + " TEXT, " + KEY_IMAGE +" BLOB, "+ KEY_INGREDIENTS + " TEXT, " + KEY_INSTRUCTIONS + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String createTable = "CREATE TABLE " + TABLE_SHOPPING + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL1 + " TEXT)";
        //create tables
        db.execSQL(CREATE_TABLE_SHOPPING);
        db.execSQL(CREATE_TABLE_MEAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_SHOPPING);
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_MEAL);
        onCreate(db);
    }


    //Add data to shopping table
    public boolean addData(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, item);

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_SHOPPING);

        long result = db.insert(TABLE_SHOPPING, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    //Add data to meal table
   /* private String name;
    private String instructions;
    private byte[] image;
    private String ingredients;*/


    public boolean addDataMeal(String name, String instructions, byte [] image, String ingredients) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NAME, name);
        contentValues.put(KEY_IMAGE, image);
        contentValues.put(KEY_INGREDIENTS, ingredients);
        contentValues.put(KEY_INSTRUCTIONS, instructions);

        long result = db.insert(TABLE_MEAL, null, contentValues);
        if (result == -1){
            return false;
        }else {
            return true;
        }

    }

    //Returns all the data from the database shopping table
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SHOPPING;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getAllMealData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MEAL;
        Cursor data = db.rawQuery(query, null);
        return data;
    }


    //Returns only the ID that matches the name passed in shopping table
    public Cursor getItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COL0 + " FROM " + TABLE_SHOPPING + " WHERE " + COL1 + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //Returns only the ID that matches the name passed in meal table
    public Cursor getMealItemID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + KEY_ID + " FROM " + TABLE_MEAL + " WHERE " + KEY_NAME + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    //Returns a meal from an id
    public Cursor getMealItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_MEAL + " WHERE " + KEY_ID + " = '" + id + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    /*
 * get single todo
 */
    public MealItem getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_MEAL + " WHERE "
                + KEY_ID + " = " + id;

        //Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        String name = c.getString(c.getColumnIndex(KEY_NAME));
        String ingredients =c.getString(c.getColumnIndex(KEY_INGREDIENTS));
        String instructions =c.getString(c.getColumnIndex(KEY_INSTRUCTIONS));
        byte[] image = c.getBlob(c.getColumnIndex(KEY_IMAGE));
        MealItem item = new MealItem(name,ingredients,image,instructions);

        return item;
    }

    //updates the name field of shopping table

    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_SHOPPING + " SET " + COL1 + " = '" + newName + "' WHERE " + COL0 + " = '" + id + "'" + " AND " + COL1 + " = '" + oldName + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting name to : " + newName);
        db.execSQL(query);
    }

    //Delete from shopping database
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_SHOPPING + " WHERE " + COL0 + " = '" + id + "'" + " AND " + COL1 + " = '" + name + "'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting: " + name + " from database.");
        db.execSQL(query);
    }

    //delete from meals table
    public void deleteMeal(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_MEAL + " WHERE " + KEY_ID + " = '" + id + "'" + " AND " + KEY_NAME + " = '" + name + "'";
        Log.d(TAG, "deleteMeal: query: " + query);
        Log.d(TAG, "deleteMeal: Deleting: " + name + " from database.");
        db.execSQL(query);
    }

    //Should update the whole meal of  meals table
    public void updateMeal(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_SHOPPING + " SET " + COL1 + " = '" + newName + "' WHERE " + COL0 + " = '" + id + "'" + " AND " + COL1 + " = '" + oldName + "'";
        Log.d(TAG, "updateMeal: query: " + query);
        Log.d(TAG, "updateMeal: Setting name to : " + newName);
        db.execSQL(query);
    }

}
