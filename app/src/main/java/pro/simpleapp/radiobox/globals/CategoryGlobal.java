package pro.simpleapp.radiobox.globals;

import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.Radio;



public class CategoryGlobal {

    private Category category;
    private static CategoryGlobal instance;


    private CategoryGlobal() {
        category = new Category();
    }


    public static CategoryGlobal getInstance() {
        if (instance == null) {
            instance = new CategoryGlobal();
        }
        return instance;
    }


    public Category getCategory() {
        return category;
    }


    public void setCategory(Category category) {
        this.category = category;
    }


}
