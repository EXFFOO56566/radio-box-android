package pro.simpleapp.radiobox.helpers;

import java.util.Random;



public class Rstring {

    private static final String ALLOWED_CHARACTERS = "ABCDIFGHIJKLMNOPRSYQRSTUVWqwertyuiopasdfghjklzxcvbnm";


    public String random(final int sizeOfRandomString) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for (int i = 0; i < sizeOfRandomString; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }


}
