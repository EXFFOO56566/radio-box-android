package pro.simpleapp.radiobox.items;

import android.text.TextUtils;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;



@ParseClassName("_User")
public class User extends ParseUser {

    public static final String USER_NAME = "username";
    public static final String USER_OBJECTID = "ibjectId";
    public static final String USER_INSTALLATION = "installation";
    public static final String USER_NIKNAME = "nickname";
    public static final String USER_ADMIN = "is_Admin";
    public static final String USER_ONESIGNAL = "player_id";
    public static final String USER_PHOTO = "photo";


    @Override
    public boolean equals(Object o) {
        if (TextUtils.equals(this.getObjectId(), ((User) o).getObjectId())) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public int hashCode() {
        return this.getObjectId().hashCode();
    }


    @Override
    public String getUsername() {
        String username = getString(USER_NAME);
        if (username != null) {
            return username;
        } else {
            return "<undefined>";
        }
    }


    public void setUserName(String userName) {
        put(USER_NAME, userName);
    }


    public void setUserNikname(String nikname) {
        put(USER_NIKNAME, nikname);
    }


    public String getUserNikname() {
        String nick = getString(USER_NIKNAME);
        return nick;
    }


    public void setUserAsAdmin(Boolean adm) {
        put(USER_ADMIN, adm);
    }


    public Boolean getUserAsAdmin() {
        return this.getBoolean(USER_ADMIN);
    }


    public void setInstallation(ParseInstallation installation) {
        put(USER_INSTALLATION, installation);
    }


    public ParseInstallation getInstallation() {
        return (ParseInstallation) get("installation");
    }


    public static ParseQuery<User> getUserQuery() {
        return ParseQuery.getQuery(User.class);
    }


    public String getPlayerId() {
        return this.getString(USER_ONESIGNAL);
    }


    public void setPlayerId(String id) {
        put(USER_ONESIGNAL, id);
    }


    public ParseFile getUserPhoto() {
        return this.getParseFile(USER_PHOTO);
    }


    public void setUaerPhoto(ParseFile photo) {
        put(USER_PHOTO, photo);
    }


}
