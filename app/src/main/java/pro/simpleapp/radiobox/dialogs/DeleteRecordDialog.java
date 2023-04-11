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



public class DeleteRecordDialog extends AppCompatDialog implements
        View.OnClickListener {

    public Activity df;
    Button btn_delete;
    Button brn_share_mp3;
    String track_name;
    TextView up_txt;


    public DeleteRecordDialog(Activity a, String track_name) {
        super(a);
        this.df = a;
        this.track_name = track_name;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.delete_dialog);
        btn_delete = (Button) findViewById(R.id.btn_delete_d);
        up_txt = (TextView) findViewById(R.id.deleteText);
        up_txt.setText(track_name);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete_d:
                break;
            case R.id.btn_share_mp3:
                break;
            default:
                break;
        }
        dismiss();
    }


}


