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
}
