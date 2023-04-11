package pro.simpleapp.radiobox.items;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;



@ParseClassName("Like")
public class Like extends ParseObject {

    public final static String L_RADIO = "radio";
    public final static String L_USER = "user";


    public static ParseQuery<Like> getAllLike() {
        return new ParseQuery<Like>(Like.class);
    }


    public void setLUser(User user) {
        put(L_USER, user);
    }


    public User getRUser() {
        return (User) this.getParseUser(L_USER);
    }


    public void setRadio(Radio radio) {
        put(L_RADIO, radio);
    }


    public Radio getRadio() {
        return (Radio) this.getParseObject(L_RADIO);
    }


}
