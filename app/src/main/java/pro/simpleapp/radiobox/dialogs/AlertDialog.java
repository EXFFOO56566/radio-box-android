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

import pro.simpleapp.radiobox.R;



public class AlertDialog extends AppCompatDialog implements
        android.view.View.OnClickListener {

    public Activity df;
    public Dialog dr;
    TextView text;
    Button ok;
    String alert;


    public AlertDialog(Activity a, String alert) {
        super(a);
        this.df = a;
        this.alert = alert;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // String texts = getContext().getResources().getString(R.string.rules);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog);
        text = (TextView) findViewById(R.id.errorText);
        ok = (Button) findViewById(R.id.btn_ook);
        text.setText(alert);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ook:
                // c.finish();
                break;
            default:
                break;
        }
        dismiss();
    }


}

