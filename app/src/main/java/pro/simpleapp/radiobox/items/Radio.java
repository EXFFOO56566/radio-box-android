package pro.simpleapp.radiobox.items;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;



@ParseClassName("Radio")
public class Radio extends ParseObject {

    public static final String R_LOGO = "logo";
    public static final String R_STREAM = "stream";
    public static final String R_NAME = "name";
    public static final String R_USER = "user";
    public static final String R_RARING = "rating";
    public static final String R_VOITING = "voiting";
    public static final String R_CATEGORY = "category";
    public static final String R_APPROVED = "approved";
    public static final String R_ALLRATING = "allrating";


    public static ParseQuery<Radio> getAllPost() {
        return new ParseQuery<Radio>(Radio.class);
    }


    public void setLogo(ParseFile image) {
        put(R_LOGO, image);
    }


    public ParseFile getLogo() {
        return this.getParseFile(R_LOGO);
    }


    public void setpStream(String txt) {
        put(R_STREAM, txt);
    }


    public String getStram() {
        return this.getString(R_STREAM);
    }


    public void setName(String txt) {
        put(R_NAME, txt);
    }


    public String getName() {
        return this.getString(R_NAME);
    }


    public void setRUser(User user) {
        put(R_USER, user);
    }


    public User getRUser() {
        return (User) this.getParseUser(R_USER);
    }


    public void setCategory(Category category) {
        put(R_CATEGORY, category);
    }


    public Category getCategory() {
        return (Category) this.getParseObject(R_CATEGORY);
    }


    public void setRating(int rating) {
        put(R_RARING, rating);
    }


    public int getRating() {
        return this.getInt(R_RARING);
    }


    public void setVoiting(int voite) {
        put(R_VOITING, voite);
    }


    public int getVoiting() {
        return this.getInt(R_VOITING);
    }


    public void setApproved(Boolean approved) {
        put(R_APPROVED, approved);
    }


    public Boolean getApproved() {
        return this.getBoolean(R_APPROVED);
    }

    public void setrAllrating(int allrating){

        put(R_ALLRATING,allrating);
    }

    public int getAllRating(){

        return this.getInt(R_ALLRATING);
    }


}
