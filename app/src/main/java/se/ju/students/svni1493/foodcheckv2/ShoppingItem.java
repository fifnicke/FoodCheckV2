package se.ju.students.svni1493.foodcheckv2;

/**
 * Created by fifnicke on 2017-12-06.
 */

public class ShoppingItem {
    private String itemId;
    private String itemName;

    public ShoppingItem(){
    }
    public ShoppingItem(String itemId, String itemName){
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }
}
