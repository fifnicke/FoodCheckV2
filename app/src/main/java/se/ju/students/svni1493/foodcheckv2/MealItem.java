package se.ju.students.svni1493.foodcheckv2;

/**
 * Created by fifnicke on 2017-11-13.
 */

public class MealItem {
    private String name;
    private String instructions;
    private byte[] image;
    private String ingredients;

    public MealItem(String name, String instructions, byte[] image, String ingredients) {
        this.name = name;
        this.instructions = instructions;
        this.image = image;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }
}
