package pro.simpleapp.radiobox.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appizona.yehiahd.fastsave.FastSave;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rishabhharit.roundedimageview.RoundedImageView;
import com.thekhaeng.pushdownanim.PushDownAnim;
import com.yashovardhan99.timeit.Stopwatch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.dialogs.AlertDialog;
import pro.simpleapp.radiobox.dialogs.ComplaintDialog;
import pro.simpleapp.radiobox.globals.RadioGlobal;
import pro.simpleapp.radiobox.globals.UserGlobal;
import pro.simpleapp.radiobox.helpers.Alarmz;
import pro.simpleapp.radiobox.helpers.Rstring;
import pro.simpleapp.radiobox.items.Complaint;
import pro.simpleapp.radiobox.items.Like;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.Rating;
import pro.simpleapp.radiobox.items.User;
import pro.simpleapp.radiobox.panels.SearchPanel;
import pro.simpleapp.radiobox.prfs.MessageEvent;
import pro.simpleapp.radiobox.prfs.Prfs;
import pro.simpleapp.radiobox.radioHelper.PlaybackStatus;
import pro.simpleapp.radiobox.radioHelper.RadioManager;
import pro.simpleapp.radiobox.radioHelper.RadioService;
import pro.simpleapp.radiobox.radioHelper.Recorder;



public class RadioFragment extends Fragment implements Prfs, AudioManager.OnAudioFocusChangeListener, Stopwatch.OnTickListener {

    View rootView;

    @BindView(R.id.radio_name)
    TextView radio_name_txt;
    @BindView(R.id.image_cover)
    RoundedImageView image_cover;
    @BindView(R.id.artist_txt)
    TextView artist_text;
    @BindView(R.id.txt_song)
    TextView song_txt;
    @BindView(R.id.rbar)
    SimpleRatingBar rbar;
    @BindView(R.id.rate)
    MaterialButton rate_btn;
    @BindView(R.id.playTrigger)
    ImageButton playTrigger;
    @BindView(R.id.baseds)
    TextView baseds_txt;
    @BindView(R.id.search)
    ImageButton search_btn;
    @BindView(R.id.like_btn)
    ImageButton like_btn;
    @BindView(R.id.shares_btn)
    ImageButton share_btn;
    @BindView(R.id.record_label)
    TextView record_label;
    @BindView(R.id.btn_record)
    ImageButton record_btn;
    @BindView(R.id.btn_back)
    ImageButton btn_back;
    @BindView(R.id.complaint_btn)
    ImageView complaint_btn;
    @BindView(R.id.text_user_p)
    TextView text_user_p;
    @BindView(R.id.image_user_p)
    RoundedImageView image_user_p;
    @BindView(R.id.layout_user)
    RelativeLayout layout_user;

    UserProfileFragment userProfileFragment;
    FragmentTransaction fragmentTransaction;

    RadioManager radioManager;
    RadioService service;
    String urlRadio;
    Radio radio;
    Boolean isPlaing;
    User user;
    User userRadio;

    String nameStation;
    Boolean iDle;
    Bitmap artImage;
    Recorder recorder;
    String song_name;
    Stopwatch stopwatch;


    int voiting;
    int rating;
    int ratez;

    Boolean isLike;


    public RadioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_radio, container, false);
        ButterKnife.bind(this, rootView);
        user = (User) ParseUser.getCurrentUser();
        iDle = false;
        record_label.setVisibility(View.GONE);
        stopwatch = new Stopwatch();
        stopwatch.setTextView(record_label);
        record_label.setSelected(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        radio = RadioGlobal.getInstance().getRadio();
        urlRadio = radio.getStram();
        isLike = false;
        radioManager = RadioManager.with(getActivity());
        service = RadioManager.getService();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        userRadio = radio.getRUser();
        text_user_p.setText(userRadio.getUserNikname());
        String urlUserAva = userRadio.getUserPhoto().getUrl();
        Glide.with(this)
                .load(urlUserAva).into(image_user_p);
        layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserGlobal.getInstance().setUser(radio.getRUser());
                userProfileFragment = new UserProfileFragment();
                fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
                fragmentTransaction.replace(R.id.content, userProfileFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.show(userProfileFragment);
                fragmentTransaction.commit();
            }
        });
        complaint_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User ownradio = radio.getRUser();
                final ComplaintDialog complaintDialog = new ComplaintDialog(requireActivity());
                complaintDialog.show();
                complaintDialog.findViewById(R.id.btn_stream_not_work).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Complaint complaint = new Complaint();
                        complaint.setcType("not work");
                        complaint.setRadio(radio);
                        complaint.setLUser(ownradio);
                        complaint.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                complaintDialog.dismiss();
                            }
                        });

                    }
                });
                complaintDialog.findViewById(R.id.btn_mystation).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Complaint complaint = new Complaint();
                        complaint.setcType("my radio");
                        complaint.setRadio(radio);
                        complaint.setLUser(ownradio);
                        complaint.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                complaintDialog.dismiss();
                            }
                        });

                    }
                });
            }
        });
        if (urlRadio.equals(FastSave.getInstance().getString(URL_STREAM, ""))) {
            if (service != null) {
                if (service.getStatus() == PlaybackStatus.PLAYING) {
                    isPlaing = true;
                    String title = FastSave.getInstance().getString(NAME_ARTIST, "");
                    String[] rez = title.split(" - ");
                    String artist = rez[0];
                    String label = rez[1];
                    artist_text.setText(artist);
                    song_txt.setText(label);
                    nameStation = FastSave.getInstance().getString(TITLE_RADIO, "");
                    radio_name_txt.setText(nameStation);
                    String fc = FastSave.getInstance().getString(BITMAP_COVER, "");
                    Bitmap bt = decodeBase64(fc);
                    image_cover.setImageBitmap(bt);

                } else if (service.getStatus() == PlaybackStatus.PAUSED) {
                } else if (service.getStatus() == PlaybackStatus.IDLE) {
                    //  playz();
                }
                isPlaing = false;
            } else {
                playz();
            }

        } else {
            playz();
        }
        FastSave.getInstance().saveString(URL_STREAM, radio.getStram());
        PushDownAnim.setPushDownAnimTo(playTrigger)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        radioManager.playOrPause(urlRadio);

                    }

                });
        getRating();
        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratez == 0) {
                    showAllert(getString(R.string.choose_rating));

                } else {
                    ratingz();

                }

            }
        });
        rbar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                ratez = Math.round(rating);

            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = FastSave.getInstance().getString(NAME_ARTIST, "");
                String[] rez = title.split(" - ");
                String artist = rez[0];
                String label = rez[1];
                Bundle args = new Bundle();
                args.putString("song", title);
                args.putBoolean("search", true);
                SearchPanel searchPanel = SearchPanel.newInstance();
                searchPanel.setArguments(args);
                searchPanel.show(requireActivity().getSupportFragmentManager(), "");
            }
        });
        checkLike();
        like_btn.setOnClickListener(new View.OnClickListener() {  // remove add like if not a user
            @Override
            public void onClick(View v) {
                if (user != null) {
                    if (!isLike) {
                        Like like = new Like();
                        like.setLUser(user);
                        like.setRadio(radio);
                        like.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                showAllert("Added successfully");
                                like_btn.setBackgroundResource(R.drawable.heart_r);
                            }
                        });

                    } else {
                        showAllert("You have already added this to your favorites");
                    }
                } else {
                    Alarmz alarmz = new Alarmz();
                    alarmz.als(requireActivity());
                }

            }

        });
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_app = " http://play.google.com/store/apps/details?id=" + getActivity().getBaseContext().getPackageName();
                Rstring ss = new Rstring();
                String na = ss.random(8);
                File cache = getActivity().getApplicationContext().getExternalCacheDir();
                File sharefile = new File(cache, na + ".png");
                Log.d("share file type is", sharefile.getAbsolutePath());
                try {
                    FileOutputStream out = new FileOutputStream(sharefile);
                    artImage.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Log.e("ERROR", String.valueOf(e.getMessage()));

                }
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.i_listen) + " " + FastSave.getInstance().getString(NAME_ARTIST, "") + getString(R.string.in) + APPNAME + getString(R.string.application_download) + url_app);
                share.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse("file://" + sharefile));
                startActivity(Intent.createChooser(share,
                        "Share Radio"));

            }
        });
        record_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recorder != null) {
                    if (recorder.isRecording()) {
                        recorder.stopRecording();
                        record_btn.setBackground(getResources().getDrawable(R.drawable.record_b_g));
                        recorder = null;
                        record_btn.clearAnimation();
                        record_label.setVisibility(View.GONE);
                        if (stopwatch.isStarted())
                            stopwatch.stop();

                    }

                } else {
                    if (song_name == null) {
                    } else {
                        System.out.println("REC R- - - -   nul");
                        String tlt = song_name.replace(" ", "_");
                        String tld = tlt.replace("/", "_");
                        String currentTime = new SimpleDateFormat("ss", Locale.getDefault()).format(new Date());
                        String rnd_title = currentTime + "-XYZ-" + tld + ".mp3";
                        recorder = new Recorder(getContext(), urlRadio, rnd_title);
                        recorder.record();
                        record_btn.setBackground(getResources().getDrawable(R.drawable.record_b_r));
                        record_label.setVisibility(View.VISIBLE);
                        record_btn.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.rotate));
                        if (!stopwatch.isStarted()) {
                            stopwatch.start();

                        }

                    }

                }

            }

        });
        return rootView;
    }


    private void checkLike() {  // remove like if not user!
        if (user != null) {
            ParseQuery<Like> lQuery = Like.getAllLike();
            lQuery.whereEqualTo(Like.L_USER, user);
            lQuery.whereEqualTo(Like.L_RADIO, radio);
            lQuery.findInBackground(new FindCallback<Like>() {
                @Override
                public void done(List<Like> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            like_btn.setBackgroundResource(R.drawable.heart_r);
                            isLike = true;

                        } else {
                            like_btn.setBackgroundResource(R.drawable.heart_w);
                            isLike = false;
                        }
                    }
                }
            });

        } else {
        }
    }


    private void ratingz() {
        ParseQuery<Rating> query = Rating.getParseRadioRating();
        query.whereEqualTo(Rating.RT_USER, user);
        query.whereEqualTo(Rating.RT_RADIO, radio);
        query.findInBackground(new FindCallback<Rating>() {
            @Override
            public void done(List<Rating> objects, ParseException e) {
                if (objects.size() > 0) {
                    showAllert(getResources().getString(R.string.already_voited));

                } else {
                    setRating();

                }
            }

        });
    }


    private void setRating() {
        if (user != null) {
            rating = rating + ratez;
            voiting = voiting + 1;
            final Rating ratingz = new Rating();
            ratingz.setRadioRating(radio);
            ratingz.setRatingUser(user);
            ratingz.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    int allrate = (rating / voiting);
                    ParseQuery<Radio> query = Radio.getAllPost();
                    query.getInBackground(radio.getObjectId(), new GetCallback<Radio>() {
                        public void done(Radio object, ParseException e) {
                            if (e == null) {

                                object.setRating(rating);
                                object.setVoiting(voiting);
                                object.setrAllrating(allrate);
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        showAllert(getString(R.string.rated_saved));
                                        getRating();
                                    }
                                });
                            } else {
                                System.out.println(e.getLocalizedMessage());
                            }
                        }
                    });

                }
            });

        } else {
            Alarmz alarmz = new Alarmz();
            alarmz.als(requireActivity());
        }

    }


    private void getRating() {
        ParseQuery<Radio> rr = Radio.getAllPost();
        rr.getInBackground(radio.getObjectId(), new GetCallback<Radio>() {
            @Override
            public void done(Radio object, ParseException e) {
                rating = object.getRating();
                voiting = object.getVoiting();
                if (voiting == 0) {
                    rbar.setRating(0);
                } else {
                    rbar.setRating(rating / voiting);

                }
                baseds_txt.setText(getResources().getString(R.string.based) + " " + voiting + " " + getResources().getString(R.string.votes));
            }
        });

    }


    private boolean isEmpty(EditText myeditText) {
        return myeditText.getText().toString().trim().length() == 0;
    }


    private void playz() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!radioManager.isPlaying()) {
                    System.out.println("UUUU = = = = " + urlRadio);
                    radioManager.playOrPause(urlRadio);
                } else {
                    //radioManager.stopPlayer();
                    System.out.println("UUUZ -= = = = = =  ST");
                    radioManager.playOrPause(urlRadio);
                }

            }
        }, 1000);

    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                radioManager.stopPlayer();
                break;
        }
    }


    @Subscribe
    public void onEvent(MessageEvent event) {
        makeScreenUpdate(event.message, event.bitmap, event.radioName);

    }


    private void makeScreenUpdate(String title, Bitmap bitmap, String radioname) {
        artImage = bitmap;
        song_name = title;
        String[] separated = title.split(" - ");
        String artist = separated[0];
        String song = separated[1];
        FastSave.getInstance().saveString(NAME_ARTIST, title);
        song_txt.setText(song);
        artist_text.setText(artist);
        radio_name_txt.setText(radioname);
        FastSave.getInstance().saveString(TITLE_RADIO, radioname);
        image_cover.setImageBitmap(bitmap);
        String imgs = encodeTobase64(bitmap);
        FastSave.getInstance().saveString(BITMAP_COVER, imgs);

    }


    @Subscribe
    public void onEvent(final String status) {
        switch (status) {
            case PlaybackStatus.LOADING:
                break;
            case PlaybackStatus.ERROR:
                radioManager.stopPlayer();
                FastSave.getInstance().saveBoolean("play", false);
                break;
            case PlaybackStatus.STOPPED:
                FastSave.getInstance().saveBoolean("play", false);
                break;
            case PlaybackStatus.IDLE:
                iDle = true;
                break;
            case PlaybackStatus.PAUSED:
                FastSave.getInstance().saveBoolean("play", false);
                break;
            case PlaybackStatus.PLAYING:
                FastSave.getInstance().saveBoolean("play", true);
                break;

        }
        playTrigger.setBackground(status.equals(PlaybackStatus.PLAYING) ? ContextCompat.getDrawable(getActivity(), R.drawable.pausebtn) : ContextCompat.getDrawable(getActivity(), R.drawable.playbtn));

    }


    @Override
    public void onResume() {
        radioManager.bind();
        super.onResume();

    }


    private boolean isPlaying() {
        return (null != radioManager && null != RadioManager.getService() && RadioManager.getService().isPlaying());
    }


    @Override
    public void onPause() {
        super.onPause();
        // super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        // radioManager.unbind();
        super.onDestroy();

    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        radioManager.bind();
        super.onStart();

    }


    @Override
    public void onTick(Stopwatch stopwatch) {
        Log.d("MAINACTIVITYLISTENER", String.valueOf(stopwatch.getElapsedTime()));
    }


    private void showAllert(String q) {
        final AlertDialog a = new AlertDialog(requireActivity(), q);
        a.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        a.show();
        a.findViewById(R.id.btn_ook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.dismiss();
            }
        });

    }


    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        // Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }


    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }


}