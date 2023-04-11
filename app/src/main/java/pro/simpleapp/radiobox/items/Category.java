package pro.simpleapp.radiobox.items;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;



@ParseClassName("Category")
public class Category extends ParseObject {

    public static final String C_NAME = "name";


    public static ParseQuery<Category> getAllCategory() {
        return new ParseQuery<Category>(Category.class);
    }


    public void setCategory(String category) {
        put(C_NAME, category);
    }


    public String getCategory() {
        return this.getString(C_NAME);
    }


}
