package pro.simpleapp.radiobox.radioHelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import pro.simpleapp.radiobox.R;

public class LoadCoverArt {

    public static String grabImage(final String song_name, final CoverCallback callback, final Context context) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... unused) {

                try {

                    JSONObject jsonObject = getJSONObjectFromUrl("https://itunes.apple.com/search?term=" + URLEncoder.encode(song_name, "UTF-8") + "&media=music&limit=1");
                    try {
                        if (jsonObject != null && jsonObject.has("results") && jsonObject.getJSONArray("results").length() > 0) {
                            JSONObject track = jsonObject.getJSONArray("results").getJSONObject(0);
                            String url = track.getString("artworkUrl100");
                            String bigImg = url.replace("100x100bb.jpg", "800x800bb.jpg");
                           // String pngImage = bigImg.replace(".jpg", ".png");
                            return bigImg;
                        } else {

                            return null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } catch (UnsupportedEncodingException er) {

                    er.printStackTrace();

                }
                return null;
            }


            @Override
            protected void onPostExecute(final String img) {



                if (img != null) {

                    Glide.with(context)
                            .asBitmap()
                            .load(img)
                            .placeholder(R.drawable.logo)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                                    callback.finish(resource);
                                }


                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    callback.finish(null);

                                }
                            });

                } else {

                    Bitmap back = ((BitmapDrawable) ResourcesCompat.getDrawable(context.getResources(), R.drawable.back_led, null)).getBitmap();
                    callback.finish(back);
                }
            }
        }.execute();

        return null;
    }


    public interface CoverCallback {

        void finish(Bitmap b);

    }


    public static JSONObject getJSONObjectFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            return new JSONObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getDataFromUrl(String url) {

        StringBuffer buffer = new StringBuffer("");
        try {
            URL urlCon = new URL(url);

            HttpURLConnection connect = (HttpURLConnection) urlCon
                    .openConnection();
            connect.setRequestProperty("User-Agent", "Android");
            connect.setRequestMethod("GET");
            connect.setDoInput(true);
            connect.connect();

            int status = connect.getResponseCode();
            if ((status != HttpURLConnection.HTTP_OK)
                    && (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)) {

                String newUrl = connect.getHeaderField("Location");
                String cookies = connect.getHeaderField("Set-Cookie");

                connect = (HttpURLConnection) new URL(newUrl).openConnection();
                connect.setRequestProperty("Cookie", cookies);
                connect.setRequestProperty("User-Agent", "Android");
                connect.setRequestMethod("GET");
                connect.setDoInput(true);

            }

            InputStream inputStream = connect.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                buffer.append(line);
            }

        } catch (IOException e) {
        }

        return buffer.toString();
    }

}
