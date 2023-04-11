package pro.simpleapp.radiobox.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import pro.simpleapp.radiobox.R;



public class NameDialog extends AppCompatDialog implements
        android.view.View.OnClickListener {

    public Activity df;
    public Dialog dr;
    TextView text;
    Button ok;
    public String alert;
    public EditText name;


    public NameDialog(Activity a) {
        super(a);
        this.df = a;
        this.alert = alert;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.name_dialog);
        text = (TextView) findViewById(R.id.errorText);
        ok = (Button) findViewById(R.id.btn_ook);
        name = (EditText) findViewById(R.id.editText_n);
        alert = name.getText().toString();
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ook:
                // c.finish();
                alert = name.getText().toString();
                break;
            default:
                break;
        }
        dismiss();
    }


}
