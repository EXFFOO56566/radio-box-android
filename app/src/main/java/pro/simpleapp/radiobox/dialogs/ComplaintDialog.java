package pro.simpleapp.radiobox.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import pro.simpleapp.radiobox.R;



public class ComplaintDialog extends AppCompatDialog implements
        View.OnClickListener {

    public Activity df;
    Button btn_stream;
    Button btn_mystation;

    TextView up_txt;


    public ComplaintDialog(Activity a) {
        super(a);
        this.df = a;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.complaint_dialog);
        btn_stream = (Button) findViewById(R.id.btn_stream_not_work);
        btn_mystation = (Button) findViewById(R.id.btn_mystation);
        up_txt = (TextView) findViewById(R.id.deleteText);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stream_not_work:
                break;
            case R.id.btn_mystation:
                break;
            default:
                break;
        }
        dismiss();
    }


}



