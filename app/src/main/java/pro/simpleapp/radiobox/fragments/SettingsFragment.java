package pro.simpleapp.radiobox.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.appizona.yehiahd.fastsave.FastSave;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.dialogs.SetTimD;
import pro.simpleapp.radiobox.helpers.Cms;
import pro.simpleapp.radiobox.helpers.STimersvc;
import pro.simpleapp.radiobox.helpers.Timerst;
import pro.simpleapp.radiobox.prfs.Prfs;

import static android.content.Context.ALARM_SERVICE;



/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements Prfs {

    View rootView;
    @BindView(R.id.button_mail)
    Button send_mail;
    @BindView(R.id.button_timer)
    Button timer_btn;
    @BindView(R.id.timer_text)
    TextView t_r;
    @BindView(R.id.recs)
    LinearLayout recs;
    Timerst timerst;
    Cms cms;
    Handler handler = new Handler();

    FragmentTransaction fTrans;
    RecordsFragment recordsFragment;


    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, rootView);
        send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:?subject=" + getString(R.string.sent) + "&body=" + getString(R.string.write) + "&to=" + DEV_MAIL);
                mailIntent.setData(data);
                startActivity(Intent.createChooser(mailIntent, "Send mail..."));

            }
        });
        timerst = new Timerst(getActivity());
        timerst.checkSleepTime();
        cms = new Cms();
        if (timerst.getIsSleepTimeOn()) {
            timer_btn.setText(getString(R.string.stop_timer));

        } else {
            timer_btn.setText(getString(R.string.start_timer));
        }
        timer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerst.getIsSleepTimeOn()) {
                    Intent i = new Intent(getActivity(), STimersvc.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), timerst.getSleepID(), i, PendingIntent.FLAG_ONE_SHOT);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                    pendingIntent.cancel();
                    alarmManager.cancel(pendingIntent);
                    timerst.sleepTime(false, 0, 0);
                    t_r.setText(getString(R.string.timer));
                    timer_btn.setText(getString(R.string.start_timer));

                } else {
                    setTimeDialog();
                }
            }
        });
        t_r.setText(getString(R.string.timer));
        updatez(t_r, timerst.getSleepTime());
        recs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordsFragment = new RecordsFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fTrans.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
                fTrans.replace(R.id.content, recordsFragment);
                fTrans.addToBackStack(null);
                fTrans.show(recordsFragment);
                fTrans.commit();
            }
        });
        return rootView;
    }


    private void setTimeDialog() {
        final SetTimD td = new SetTimD(getActivity());
        td.show();
        td.findViewById(R.id.btn_ook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min = FastSave.getInstance().getInt(COWNTDOWN, 0);
                String hours = String.valueOf(min / 60);
                String minute = String.valueOf(min % 60);
                if (hours.length() == 1) {
                    hours = "0" + hours;
                }
                if (minute.length() == 1) {
                    minute = "0" + minute;
                }
                String totalTime = hours + ":" + minute;
                long total_timer = cms.cToMs(totalTime) + System.currentTimeMillis();
                Random random = new Random();
                int id = random.nextInt(100);
                timerst.sleepTime(true, total_timer, id);
                Intent intent = new Intent(getActivity(), STimersvc.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), id, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, total_timer, pendingIntent);
                updatez(t_r, timerst.getSleepTime());
                td.dismiss();
                timer_btn.setText(getString(R.string.stop_timer));

            }
        });
    }


    private void updatez(final TextView textView, long time) {
        long timeleft = time - System.currentTimeMillis();
        if (timeleft > 0) {
            @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeleft),
                    TimeUnit.MILLISECONDS.toMinutes(timeleft) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(timeleft) % TimeUnit.MINUTES.toSeconds(1));
            udpt(hms);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (timerst.getIsSleepTimeOn()) {
                        updatez(textView, timerst.getSleepTime());
                    }
                }
            }, 1000);
        }
    }


    private void udpt(String t) {
        t_r.setText(t);
        t_r.invalidate();
        t_r.requestLayout();
    }


}
