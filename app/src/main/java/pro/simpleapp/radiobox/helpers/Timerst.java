package pro.simpleapp.radiobox.helpers;

import android.content.Context;
import android.content.SharedPreferences;



public class Timerst {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public Timerst(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("datas", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }


    public void checkSleepTime() {
        if (getSleepTime() <= System.currentTimeMillis()) {
            sleepTime(false, 0, 0);
        }
    }


    public void sleepTime(Boolean isTimerOn, long sleepTime, int id) {
        editor.putBoolean("isOn", isTimerOn);
        editor.putLong("sTime", sleepTime);
        editor.putInt("sTimeID", id);
        editor.apply();
    }


    public Boolean getIsSleepTimeOn() {
        return sharedPreferences.getBoolean("isOn", false);
    }


    public long getSleepTime() {
        return sharedPreferences.getLong("sTime", 0);
    }


    public int getSleepID() {
        return sharedPreferences.getInt("sTimeID", 0);
    }


}
