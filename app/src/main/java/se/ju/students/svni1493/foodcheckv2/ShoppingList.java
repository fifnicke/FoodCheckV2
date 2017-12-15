package se.ju.students.svni1493.foodcheckv2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fifnicke on 2017-12-06.
 */

public class ShoppingList extends ArrayAdapter<ShoppingItem> {
    private Activity context;
    List<ShoppingItem> shoppingItems;

    public ShoppingList(Activity context, List<ShoppingItem> shoppingItems){
        super(context, R.layout.layout_shopping_item_list, shoppingItems);
        this.context = context;
        this.shoppingItems = shoppingItems;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_shopping_item_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);

        ShoppingItem si = shoppingItems.get(position);
        textViewName.setText(si.getItemName());

        return listViewItem;
    }

}
