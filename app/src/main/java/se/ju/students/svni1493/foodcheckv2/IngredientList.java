package se.ju.students.svni1493.foodcheckv2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fifnicke on 2017-12-07.
 */


public class IngredientList extends ArrayAdapter<IngredientItem> {
    private Activity context;
    List<IngredientItem> ingredientItems;

    public IngredientList(Activity context, List<IngredientItem> ingredientItems){
        super(context, R.layout.layout_ingredient_list, ingredientItems);
        this.context = context;
        this.ingredientItems = ingredientItems;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_shopping_item_list, null, true);

        TextView ingredientListName = (TextView) listViewItem.findViewById(R.id.ingredientListName);

        IngredientItem i = ingredientItems.get(position);
        ingredientListName.setText(i.getItemName());

        return listViewItem;
    }

}