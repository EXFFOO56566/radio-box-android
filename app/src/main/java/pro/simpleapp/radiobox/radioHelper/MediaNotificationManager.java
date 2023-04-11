package pro.simpleapp.radiobox.radioHelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import pro.simpleapp.radiobox.MainActivity;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.prfs.Prfs;

public class MediaNotificationManager implements Prfs {

    public static final int NOTIFICATION_ID = 555;
    private final String PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID";
    private final String PRIMARY_CHANNEL_NAME = "PRIMARY";
    private RadioService service;
    private Resources resources;
    private NotificationManagerCompat notificationManager;
    private Bitmap largeIcon;
    private String artist;
    private String radio;
    private String playbackStatus;
    NotificationCompat.Builder builder;


    public MediaNotificationManager(RadioService service) {

        this.service = service;
        this.resources = service.getResources();

        notificationManager = NotificationManagerCompat.from(service);
    }


    public void startNotify(String playbackStatus) {
        this.playbackStatus = playbackStatus;

        startNotify();
    }


    public void startNotify(Bitmap notifyIcon, String artist, String radio) {

        this.largeIcon = notifyIcon;
        this.artist = artist;
        this.radio = radio;
        startNotify();
    }


    public void startNotify() {

        if (playbackStatus == null) return;

        if (largeIcon == null)
            largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logo);

        int icon = R.drawable.pauses;//ic_pause_black;
        Intent playbackAction = new Intent(service, RadioService.class);
        playbackAction.setAction(RadioService.ACTION_PAUSE);
        PendingIntent action = PendingIntent.getService(service, 1, playbackAction, 0 | PendingIntent.FLAG_IMMUTABLE);

        if (playbackStatus.equals(PlaybackStatus.PAUSED)) {

            icon = R.drawable.plays;//ic_play_arrow_black;
            playbackAction.setAction(RadioService.ACTION_PLAY);
            action = PendingIntent.getService(service, 2, playbackAction, 0 | PendingIntent.FLAG_IMMUTABLE);
            this.largeIcon = BitmapFactory.decodeResource(resources, R.drawable.logo);

        }

        Intent stopIntent = new Intent(service, RadioService.class);
        stopIntent.setAction(RadioService.ACTION_STOP);
        PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, 0 | PendingIntent.FLAG_IMMUTABLE);

        Intent intent = new Intent(service, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0 | PendingIntent.FLAG_IMMUTABLE);

        notificationManager.cancel(NOTIFICATION_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            manager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(service, PRIMARY_CHANNEL)
                .setAutoCancel(false)
                .setContentTitle(radio)
                .setContentText(artist)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(android.R.drawable.btn_radio)
                .addAction(icon, "pause", action)
                .addAction(R.drawable.stops, "stop", stopAction)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(service.getMediaSession().getSessionToken())
                        .setShowActionsInCompactView(0, 1)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(stopAction)
                );

        service.startForeground(NOTIFICATION_ID, builder.build());

    }


    public void cancelNotify() {

        service.stopForeground(true);

    }

}
