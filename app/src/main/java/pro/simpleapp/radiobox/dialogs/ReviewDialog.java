package pro.simpleapp.radiobox.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialog;

import pro.simpleapp.radiobox.R;



public class ReviewDialog extends AppCompatDialog implements
        android.view.View.OnClickListener {

    public Activity df;
    public Dialog dr;
    Button yes;
    Button no;


    public ReviewDialog(Activity a) {
        super(a);
        this.df = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.review_dialog);
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:
                break;
            case R.id.no:
                break;
            default:
                break;
        }
        dismiss();
    }


}
