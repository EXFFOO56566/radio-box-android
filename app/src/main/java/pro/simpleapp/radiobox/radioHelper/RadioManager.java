package pro.simpleapp.radiobox.radioHelper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

public class RadioManager {

    private static RadioManager instance = null;
    private static RadioService service;
    private Context context;
    private boolean serviceBound;


    private RadioManager(Context context) {
        this.context = context;
        serviceBound = false;
    }


    public static RadioManager with(Context context) {
        instance = new RadioManager(context);
        if (instance == null) {
            Log.d("lol", "instance==null");

        }
        return instance;
    }


    public static RadioService getService() {
        return service;
    }


    public void playOrPause(String streamUrl) {
        service.playOrPause(streamUrl);
    }


    public boolean isPlaying() {
        return service.isPlaying();
    }

    public void pause(){

        service.pause();
    }


    public void playResume(String streamUrl) {
        if (streamUrl == null)
            service.pause();
        else
            service.playOrPause(streamUrl);
    }


    public void bind() {
        Log.d("lol", "RadioManager.bind");

        Intent intent = new Intent(context, RadioService.class);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if (service != null)
            EventBus.getDefault().post(service.getStatus());
    }


    public void unbind() {
        Log.d("lol", "RadioManager.unbind");

        context.unbindService(serviceConnection);
    }


    public void stopPlayer() {
        service.stopTS();

    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {

            service = ((RadioService.LocalBinder) binder).getService();
            Log.d("lol", "onServiceConnected");

            serviceBound = true;

        }


        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("lol", "onServiceDisConnected");

            serviceBound = false;
        }
    };

}
