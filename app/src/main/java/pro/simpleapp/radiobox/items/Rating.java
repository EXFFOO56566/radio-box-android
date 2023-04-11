package pro.simpleapp.radiobox.items;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;



@ParseClassName("Rating")
public class Rating extends ParseObject {

    public static String RT_USER = "user";
    public static String RT_RADIO = "radio";


    public static ParseQuery<Rating> getParseRadioRating() {
        return new ParseQuery<Rating>(Rating.class);
    }


    public void setRadioRating(Radio ccRadio) {
        put(RT_RADIO, ccRadio);
    }


    public Radio getRadioRating() {
        return (Radio) this.getParseObject(RT_RADIO);
    }


    public void setRatingUser(User user) {
        put(RT_USER, user);
    }


    public User getRatingUser() {
        return (User) this.getParseUser(RT_USER);
    }


}
