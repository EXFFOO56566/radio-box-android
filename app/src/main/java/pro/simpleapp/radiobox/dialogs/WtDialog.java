package pro.simpleapp.radiobox.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

import pro.simpleapp.radiobox.R;


public class WtDialog extends AppCompatDialog implements
        View.OnClickListener {

    public Activity df;


    public WtDialog(Activity a) {
        super(a);
        this.df = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wt_dialog);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            default:
                break;
        }
        dismiss();
    }

}