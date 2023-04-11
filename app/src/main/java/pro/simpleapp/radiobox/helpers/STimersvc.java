package pro.simpleapp.radiobox.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pro.simpleapp.radiobox.radioHelper.RadioService;



public class STimersvc extends BroadcastReceiver {

    Timerst timerz;


    @Override
    public void onReceive(Context context, Intent intent) {
        timerz = new Timerst(context);
        if (timerz.getIsSleepTimeOn()) {
            timerz.sleepTime(false, 0, 0);
        }
        Intent intent_close = new Intent(context, RadioService.class);
        intent_close.setAction(RadioService.ACTION_STOP);
        context.startService(intent_close);
    }


}
