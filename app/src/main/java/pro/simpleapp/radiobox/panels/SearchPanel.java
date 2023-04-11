package pro.simpleapp.radiobox.panels;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.appizona.yehiahd.fastsave.FastSave;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.rishabhharit.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.prfs.Prfs;
import pro.simpleapp.radiobox.prfs.Setups;



public class SearchPanel extends BottomSheetDialogFragment implements Setups {


    @BindView(R.id.btn_deezer)
    MaterialButton btn_deezer;
    @BindView(R.id.btn_apple)
    MaterialButton btn_apple;
    @BindView(R.id.btn_spotify)
    MaterialButton btn_spotify;
    @BindView(R.id.btn_vk)
    MaterialButton btn_vk;
    @BindView(R.id.btn_google)
    MaterialButton btn_google;
    @BindView(R.id.btn_yandex)
    MaterialButton btn_yandex;
    @BindView(R.id.btn_install)
    MaterialButton btn_install;
    @BindView(R.id.top_txt)
    TextView top_txt;
    @BindView(R.id.img_album)
    RoundedImageView img_album;
    @BindView(R.id.btn_liryc)
    MaterialButton btn_litycs;
    @BindView(R.id.btn_yutube)
    MaterialButton btn_youtube;
    @BindView(R.id.bottom_sheet)
    LinearLayout bottom_sheet;
    @BindView(R.id.button_close)
    ImageButton button_close;

    String uri;
    String song_title;
    View rootView;
    Boolean isPanel;


    public static SearchPanel newInstance() {
        return new SearchPanel();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);
        btn_install.setVisibility(View.GONE);
        Bundle mArgs = getArguments();
        String t = mArgs.getString("song").toUpperCase();
        String r1 = t.replace("/", " ");
        String r2 = r1.replace("\n", " ");
        song_title = r2;
        isPanel = mArgs.getBoolean("search", false);
        setCancelable(false);
        String img = FastSave.getInstance().getString(Prfs.BITMAP_COVER, "");
        Bitmap bm = StringToBitMap(img);
        img_album.setImageBitmap(bm);
        String txt = "find : \n" + t + "\n" + "in popular music streaming services.";
        int art = (r2.replace(" ", "").length());
        int all = txt.length();
        int start = song_title.length() + 8;
        Spannable wordtoSpan = new SpannableString(txt);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#E32345")), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(Color.parseColor("#E32345")), start, all, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        top_txt.setText(wordtoSpan);
        btn_deezer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("deezer.android.app", requireActivity())) {
                    try {
                        String decoded = URLDecoder.decode(song_title, "UTF-8");
                        uri = "deezer://www.deezer.com/search//" + decoded + "/";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        requireActivity().startActivity(intent);
                        dismiss();

                    } catch (UnsupportedEncodingException ee) {
                    }

                } else {
                    showAppAlert("Deezer", "https://play.google.com/store/apps/details?id=deezer.android.app&hl=us", requireActivity());

                }
            }
        });
        btn_apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.apple.android.music", requireActivity())) {
                    try {
                        String decoded = URLDecoder.decode(song_title, "UTF-8");
                        String[] separated = decoded.split(" - ");
                        String queryURL = "https://itunes.apple.com/search?term=" + separated[0] + " " + separated[1] + "&limit=1";
                        AndroidNetworking.get(queryURL)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            int counting = response.getInt("resultCount");
                                            if (counting == 0) {
                                            } else {
                                                JSONArray array = response.getJSONArray("results");
                                                JSONObject obj = array.getJSONObject(0);
                                                String a1 = obj.getString("trackViewUrl");
                                                String a2 = a1.replace("https://", "");
                                                uri = "apple-music://" + a2;
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                requireActivity().startActivity(intent);
                                                dismiss();

                                            }

                                        } catch (JSONException de) {
                                            Log.d("XXX", de.getLocalizedMessage());

                                        }

                                    }


                                    @Override
                                    public void onError(ANError anError) {
                                    }
                                });

                    } catch (UnsupportedEncodingException ed) {
                    }

                } else {
                    showAppAlert("Apple music", "https://play.google.com/store/apps/details?id=com.apple.android.music&hl=en", requireActivity());

                }

            }
        });
        btn_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.spotify.music", requireActivity())) {
                    try {
                        String decoded = URLDecoder.decode(song_title, "UTF-8");
                        uri = "spotify://search/" + decoded + "/";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        requireActivity().startActivity(intent);
                        dismiss();

                    } catch (UnsupportedEncodingException ee) {
                        Log.d("XXX", ee.getLocalizedMessage());

                    }

                } else {
                    showAppAlert("Spotify", "https://play.google.com/store/apps/details?id=com.spotify.music&hl=en", requireActivity());

                }

            }
        });
        btn_vk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("com.vkontakte.android", requireActivity()) || appInstalledOrNot("com.vtosters.android", requireActivity())) {
                    try {
                        String decoded = URLDecoder.decode(song_title, "UTF-8"); // https://vk.com/audio?q=Vanotek,%20Eneli%20-%20Back%20to%20Me
                        uri = "vk://vk.com/audio?q=" + decoded;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        requireActivity().startActivity(intent);
                        dismiss();

                    } catch (UnsupportedEncodingException ee) {
                        Log.d("XXX", ee.getLocalizedMessage());

                    }

                } else {
                    showAppAlert("Vk", "https://play.google.com/store/apps/details?id=com.vkontakte.android&hl=en", requireActivity());

                }

            }
        });
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String decoded = URLDecoder.decode(song_title, "UTF-8"); // https://vk.com/audio?q=Vanotek,%20Eneli%20-%20Back%20to%20Me
                    uri = "googleplaymusic://play.google.com//music/search/" + decoded;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    requireActivity().startActivity(intent);
                    dismiss();

                } catch (UnsupportedEncodingException ee) {
                    Log.d("XXX", ee.getLocalizedMessage());

                }
            }
        });
        btn_yandex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appInstalledOrNot("ru.yandex.music", requireActivity())) {
                    try {
                        String decoded = URLDecoder.decode(song_title, "UTF-8"); // https://vk.com/audio?q=Vanotek,%20Eneli%20-%20Back%20to%20Me
                        uri = "yandexmusic://search/?text=" + decoded;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        requireActivity().startActivity(intent);
                        dismiss();

                    } catch (UnsupportedEncodingException ee) {
                        Log.d("XXX", ee.getLocalizedMessage());

                    }

                } else {
                    showAppAlert("Yandex music", "https://play.google.com/store/apps/details?id=ru.yandex.music&hl=en", requireActivity());

                }

            }
        });
        btn_litycs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                String[] parts = song_title.split("-");
                String artist = parts[0];
                String track = parts[1];
                String url = "https://api.musixmatch.com/ws/1.1/matcher.lyrics.get?q_track=" + track + "&q_artist=" + artist + "&apikey=" + MX_KEY;
                AndroidNetworking.get(url)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject message = response.getJSONObject("message");
                                    JSONObject header = message.getJSONObject("header");
                                    String status = header.getString("status_code");
                                    if (status.equals("200")) {
                                        JSONObject body = message.getJSONObject("body");
                                        JSONObject lir = body.getJSONObject("lyrics");
                                        String text = lir.getString("lyrics_body");
                                        Intent intent = new Intent("panel");
                                        // You can also include some extra data.
                                        intent.putExtra("lyrics", text);
                                        intent.putExtra("error", false);
                                        intent.putExtra("artist", song_title);
                                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                                    } else {
                                        Intent intent = new Intent("panel");
                                        intent.putExtra("error", true);
                                        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                                    }

                                } catch (JSONException e) {
                                    System.out.println("ZZZ -  e - - " + e);
                                }

                            }


                            @Override
                            public void onError(ANError anError) {
                            }
                        });

            }
        });
        btn_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent("panel");
                intent.putExtra("youtube", true);
                intent.putExtra("artist", song_title);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

            }
        });
        bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;

    }


    private void showAppAlert(String app, String url, Activity activity) {
        btn_install.setVisibility(View.VISIBLE);
        btn_install.setText(getString(R.string.please_install) + app);
        btn_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // String url = uri;
                // Intent i = new Intent(Intent.ACTION_VIEW);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
                // i.setData(Uri.radiobox(url));
                // activity.startActivity(i);
            }
        });
        top_txt.setText(R.string.not_app);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }


    private boolean appInstalledOrNot(String uri, Activity activity) {
        PackageManager manager = activity.getPackageManager();
        List<ApplicationInfo> infoList = manager.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo i: infoList){


        }

        for (ApplicationInfo info : infoList) {
            if (info.packageName.equals(uri)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }


}
