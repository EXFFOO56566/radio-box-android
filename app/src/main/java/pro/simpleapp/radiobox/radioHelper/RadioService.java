package pro.simpleapp.radiobox.radioHelper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import org.greenrobot.eventbus.EventBus;

import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.prfs.MessageEvent;
import pro.simpleapp.radiobox.prfs.Prfs;
import saschpe.exoplayer2.ext.icy.IcyHttpDataSource;
import saschpe.exoplayer2.ext.icy.IcyHttpDataSourceFactory;

public class RadioService extends Service implements Player.EventListener, Prfs { // AudioManager.OnAudioFocusChangeListener
    public static final String ACTION_PLAY = "red.simpleapp.transistor.r.ACTION_PLAY";
    public static final String ACTION_PAUSE = "red.simpleapp.transistor.r.ACTION_PAUSE";
    public static final String ACTION_STOP = "red.simpleapp.transistor.r.ACTION_STOP";
    private final IBinder iBinder = new LocalBinder();
    private static final String TAG = "XXX";
    private Handler handler;
    private SimpleExoPlayer exoPlayer;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;
    private boolean onGoingCall = false;
    private TelephonyManager telephonyManager;
    private WifiManager.WifiLock wifiLock;
    private AudioManager audioManager;
    private static MediaNotificationManager notificationManager;
    IcyHttpDataSourceFactory icyHttpDataSourceFactory;
    private String status;
    private String strAppName;
    private String strLiveBroadcast;
    private String streamUrl;
    static String radioName;
    final static BitmapFactory.Options options = new BitmapFactory.Options(); //
    static String urlz;
    static String artz;
    static String rd;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {

                    switch (focusChange) {

                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            if (isPlaying())
                                exoPlayer.setVolume(0.1f);
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            if (isPlaying()) pause();
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS):
                            pause();
                            break;
                        case (AudioManager.AUDIOFOCUS_GAIN):
                            exoPlayer.setVolume(0.8f);
                            resume();
                            break;
                        default:
                            break;
                    }
                }
            };

    public class LocalBinder extends Binder {
        public RadioService getService() {
            return RadioService.this;
        }

    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pause();
        }

    };
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (state == TelephonyManager.CALL_STATE_OFFHOOK
                    || state == TelephonyManager.CALL_STATE_RINGING) {

                if (!isPlaying()) return;

                onGoingCall = true;
                stop();

            } else if (state == TelephonyManager.CALL_STATE_IDLE) {

                if (!onGoingCall) return;

                onGoingCall = false;
                resume();

            }
        }

    };
    private MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPause() {
            super.onPause();

            pause();
        }

        @Override
        public void onStop() {
            super.onStop();

            stop();

            Intent intent = new Intent("cover");
            intent.putExtra("stop", "stop");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        }

        @Override
        public void onPlay() {
            super.onPlay();

            resume();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return iBinder;

    }

    private void releaseAudioFocus() {

        audioManager.abandonAudioFocus(focusChangeListener);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        strAppName = getResources().getString(R.string.app_name);
        strLiveBroadcast = getResources().getString(R.string.live_broadcast);

        onGoingCall = false;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.STREAM_MUSIC);

        notificationManager = new MediaNotificationManager(this);

        wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock");

        mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, strAppName)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, strLiveBroadcast)
                .build());
        mediaSession.setCallback(mediasSessionCallback);

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
      //  telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        handler = new Handler();
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);

        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        exoPlayer.addListener(this);

        registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

        status = PlaybackStatus.IDLE;

    }

    private int acquireAudioFocus() {
        //  if (BuildConfig.DEBUG) Log.d(TAG, "acquiring audio focus.");

        int result = audioManager.requestAudioFocus(focusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG, "acquiring audio focus failed!");
        }

        return result;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        if (TextUtils.isEmpty(action))
            return START_NOT_STICKY;

        int result = acquireAudioFocus();

        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            stop();
            return START_NOT_STICKY;
        }
        if (action.equalsIgnoreCase(ACTION_PLAY)) {

            transportControls.play();

        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();

        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
            notificationManager.cancelNotify();

        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("kek3", "in onUnbind");

        if (status.equals(PlaybackStatus.IDLE))
            stopSelf();

        return true;
    }

    @Override
    public void onRebind(final Intent intent) {
    }

    @Override
    public void onDestroy() {

        pause();

        exoPlayer.release();
        exoPlayer.removeListener(this);

        if (telephonyManager != null)
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

        notificationManager.cancelNotify();

        mediaSession.release();

        unregisterReceiver(becomingNoisyReceiver);

        super.onDestroy();

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                status = PlaybackStatus.LOADING;
                break;
            case Player.STATE_ENDED:
                status = PlaybackStatus.STOPPED;
                ;
                break;
            case Player.STATE_IDLE:
                status = PlaybackStatus.IDLE;
                break;
            case Player.STATE_READY:
                status = playWhenReady ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED;

                break;
            default:
                status = PlaybackStatus.IDLE;
                break;
        }
        if (!status.equals(PlaybackStatus.IDLE))
            notificationManager.startNotify(status);

        EventBus.getDefault().post(status);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        EventBus.getDefault().post(PlaybackStatus.ERROR);

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    public void play(String streamUrl) {

        int result = acquireAudioFocus();
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

        }

        this.streamUrl = streamUrl;

        if (wifiLock != null && !wifiLock.isHeld()) {

            wifiLock.acquire();

        }

        Uri myUri = Uri.parse(streamUrl);

        String userAgent = "4YOUFM / ANDROID";

        icyHttpDataSourceFactory = new IcyHttpDataSourceFactory.Builder(userAgent).setIcyHeadersListener(new IcyHttpDataSource.IcyHeadersListener() {
            @Override
            public void onIcyHeaders(IcyHttpDataSource.IcyHeaders icyHeaders) {

                radioName = icyHeaders.getName();

            }
        }).setIcyMetadataChangeListener(new IcyHttpDataSource.IcyMetadataListener() {
            @Override
            public void onIcyMetaData(IcyHttpDataSource.IcyMetadata icyMetadata) {

                String title = icyMetadata.getStreamTitle();

                if (title == null || title.equals(" - ") || !title.contains(" - ")) {

                    artz = "not - found";
                } else {

                    artz = title;

                }

                LoadCoverArt.grabImage(artz, new LoadCoverArt.CoverCallback() {
                    @Override
                    public void finish(Bitmap b) {

                        EventBus.getDefault().post(new MessageEvent(artz, b, radioName));
                        RadioManager.getService().updateNoty(artz, b, radioName);

                    }
                }, getApplicationContext());
            }
        }).build();

      /*  icyHttpDataSourceFactory = new IcyHttpDataSourceFactory.Builder(userAgent).setIcyHeadersListener(icyHeaders -> {

            radioName = icyHeaders.getName();

        })
                .setIcyMetadataChangeListener(icyMetadata -> {

                    String title = icyMetadata.getStreamTitle();

                    if (title == null || title.equals(" - ") || !title.contains(" - ")) {

                        artz = "not - found";
                    } else {

                        artz = title;

                    }

                    LoadCoverArt.grabImage(artz, new LoadCoverArt.CoverCallback() {
                        @Override
                        public void finish(Bitmap b) {

                            EventBus.getDefault().post(new MessageEvent(artz, b, radioName));
                            RadioManager.getService().updateNoty(artz, b, radioName);

                        }
                    }, this);

                }).build(); */


        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), null, icyHttpDataSourceFactory);
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .setExtractorsFactory(extractorsFactory)
                .createMediaSource(myUri);

        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

    }

    public void resume() {

        if (!isPlaying()) {

            if (streamUrl != null)
                play(streamUrl);
        }

    }

    public void pause() {

        exoPlayer.setPlayWhenReady(false);
        audioManager.abandonAudioFocus(focusChangeListener);
        wifiLockRelease();
        releaseAudioFocus();

    }

    public void stop() {

        exoPlayer.stop();
        audioManager.abandonAudioFocus(focusChangeListener);
        wifiLockRelease();
    }

    public void playOrPause(String url) {

        if (streamUrl != null && streamUrl.equals(url)) {
            if (!isPlaying()) {
                play(streamUrl);
            } else {
                pause();
            }
        } else {
            if (isPlaying()) {
                pause();
                releaseAudioFocus();
            }
            play(url);
        }

    }

    public String getStatus() {

        return status;

    }

    public MediaSessionCompat getMediaSession() {

        return mediaSession;
    }

    public boolean isPlaying() {

        return this.status.equals(PlaybackStatus.PLAYING);
    }

    private void wifiLockRelease() {

        if (wifiLock != null && wifiLock.isHeld()) {

            wifiLock.release();

        }

    }

    public void stopTS() {
        transportControls.stop();
    }

    public void updateNoty(String title, Bitmap covr, String rn) {

        notificationManager.startNotify(covr, artz, rn);

    }

}
