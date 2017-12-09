package se.ju.students.svni1493.foodcheckv2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fifnicke on 2017-12-07.
 */

public class Ingredients {
    List<String> items = new ArrayList<>();

    public Ingredients(){}

    public Ingredients(ArrayList<String> items){
        this.items = items;

    }

    public List<String> getItems() {
        return items;
    }
}
