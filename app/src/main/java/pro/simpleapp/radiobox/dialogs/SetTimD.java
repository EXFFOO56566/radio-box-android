package pro.simpleapp.radiobox.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.appizona.yehiahd.fastsave.FastSave;

import me.tankery.lib.circularseekbar.CircularSeekBar;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.prfs.Prfs;



public class SetTimD extends AppCompatDialog implements
        View.OnClickListener, Prfs {

    public Activity df;
    public Dialog dr;
    TextView text;
    Button ok;
    CircularSeekBar seek;
    TextView tims;


    public SetTimD(Activity a) {
        super(a);
        this.df = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timer_set_dialog);
        text = (TextView) findViewById(R.id.errorText);
        ok = (Button) findViewById(R.id.btn_ook);
        seek = (CircularSeekBar) findViewById(R.id.seek_bar);
        tims = (TextView) findViewById(R.id.tx_t);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        seek.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, float progress, boolean fromUser) {
                FastSave.getInstance().saveInt(COWNTDOWN, Math.round(progress));
                String timez = secToTime(Math.round(progress));
                tims.setText(timez);
            }


            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }


            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ook:
                break;
            case R.id.seek_bar:
                break;
            default:
                break;
        }
        dismiss();
    }


    private String secToTime(int sec) {
        int second = sec % 60;
        int minute = sec / 60;
        if (minute >= 60) {
            int hour = minute / 60;
            minute %= 60;
            return hour + " min " + (minute < 10 ? "0" + minute : minute) + " sec" + (second < 10 ? "0" + second : second);
        }
        return minute + " min " + (second < 10 ? "0" + second : second);
    }


}
