package pro.simpleapp.radiobox.globals;

import java.util.ArrayList;
import java.util.List;

import pro.simpleapp.radiobox.items.Category;



public class CatsGlobal {

    List<Category> categories = new ArrayList<>();
    private static CatsGlobal instance;


    private CatsGlobal() {
        categories = new ArrayList<>();
    }


    public static CatsGlobal getInstance() {
        if (instance == null) {
            instance = new CatsGlobal();
        }
        return instance;
    }


    public List<Category> getAllCategory() {
        return categories;
    }


    public void setCategory(List<Category> category) {
        this.categories = category;
    }


}
