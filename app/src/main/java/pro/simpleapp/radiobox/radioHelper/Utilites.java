package pro.simpleapp.radiobox.radioHelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utilites {

    public static void isNetworkUnavailable(final Context context, String message) {

        if (isNetworkAvailable(context, false)) {
            if (message != null) {
            }
        } else {
        }
    }


    public static void isNetworkUnavailable(final Context context) {
        isNetworkUnavailable(context, null);
    }


    public static boolean isNetworkAvailable(Context context, boolean showDialog) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else if (showDialog) {
            isNetworkUnavailable(context);
        }
        return false;
    }


    public static String getDataFromUrl(String url) {


        StringBuffer stringBuffer = new StringBuffer("");
        try {
            URL urlCon = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlCon.openConnection();
            connection.setRequestProperty("User-Agent", "Your Radio App Single Station");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }

        } catch (IOException e) {
        }

        return stringBuffer.toString();
    }


    public static JSONObject getJSONObjectFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            return new JSONObject(data);
        } catch (Exception e) {
            Log.e("INFO", "Error parsing JSON. Printing stacktrace now");
        }

        return null;
    }


    public static JSONArray getJSONArrayFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            return new JSONArray(data);
        } catch (Exception e) {
            Log.e("INFO", "Error parsing JSON. Printing stacktrace now");
        }

        return null;
    }

}
