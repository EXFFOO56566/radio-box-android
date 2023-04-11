package pro.simpleapp.radiobox.globals;

import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.User;



public class UserGlobal {

    private User user;
    private static UserGlobal instance;


    private UserGlobal() {
        user = new User();
    }


    public static UserGlobal getInstance() {
        if (instance == null) {
            instance = new UserGlobal();
        }
        return instance;
    }


    public User getUser() {
        return user;
    }


    public void setUser(User user) {
        this.user = user;
    }


}
