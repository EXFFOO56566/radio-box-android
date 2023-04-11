package pro.simpleapp.radiobox.items;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;



@ParseClassName("Complaint")
public class Complaint extends ParseObject {

    public final static String C_TYPE = "type";
    public final static String C_RADIO = "radio";
    public final static String C_USER = "user";


    public static ParseQuery<Complaint> getAllComplaint() {
        return new ParseQuery<Complaint>(Complaint.class);
    }


    public void setRadio(Radio radio) {
        put(C_RADIO, radio);
    }


    public Radio getRadio() {
        return (Radio) this.getParseObject(C_RADIO);
    }


    public void setcType(String type) {
        put(C_TYPE, type);
    }


    public String getType() {
        return this.getString(C_TYPE);
    }


    public void setLUser(User user) {
        put(C_USER, user);
    }


    public User getRUser() {
        return (User) this.getParseUser(C_USER);
    }


}
