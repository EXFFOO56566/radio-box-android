package pro.simpleapp.radiobox;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.appizona.yehiahd.fastsave.FastSave;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.onesignal.OneSignal;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import in.myinnos.customfontlibrary.TypefaceUtil;
import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.Complaint;
import pro.simpleapp.radiobox.items.Like;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.Rating;
import pro.simpleapp.radiobox.items.User;



public class Apps extends Application {

    String GMS_ID = "000000";


    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        FastSave.init(getApplicationContext());
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Radio.class);
        ParseObject.registerSubclass(Category.class);
        ParseObject.registerSubclass(Rating.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Complaint.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                // .enableLocalDataStore()
                .build());
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", GMS_ID);
        installation.saveInBackground();
        ParsePush.subscribeInBackground("global", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("push", "failed to subscribe for push", e);
                }
            }
        });
        TypefaceUtil.overrideFont(getApplicationContext(), "SANS", "font/lineto-circular-black.ttf");
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "font/lineto-circular-pro-medium.ttf");
        TypefaceUtil.overrideFont(getApplicationContext(), "MONOSPACE", "font/lineto-circular-pro-book.ttf");
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(false);
        Picasso.setSingletonInstance(built);
      /*  OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init(); */
        // ParseUser.logOut();
        // FirebaseAuth.getInstance().signOut();
    }


}
