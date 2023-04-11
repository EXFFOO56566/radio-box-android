package pro.simpleapp.radiobox.prfs;

import android.graphics.Bitmap;



public class MessageEvent {

    public final String message;
    public final Bitmap bitmap;
    public final String radioName;


    public MessageEvent(String message, Bitmap bitmap, String radioName) {
        this.message = message;
        this.bitmap = bitmap;
        this.radioName = radioName;

    }


}
