package pro.simpleapp.radiobox.loginView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.jaeger.library.StatusBarUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.BaseActivity;
import pro.simpleapp.radiobox.MainActivity;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.dialogs.AlertDialog;


public class ForgotActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_need_offset)
    View mViewNeedOffset;
    @BindView(R.id.e_mail)
    EditText e_mail;
    @BindView(R.id.f_btn)
    Button f_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForImageView(ForgotActivity.this, 0, mViewNeedOffset);
        StatusBarUtil.setLightMode(ForgotActivity.this);

        f_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = e_mail.getText().toString();

                ParseUser.requestPasswordResetInBackground(mail,
                        new RequestPasswordResetCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    // An email was successfully sent with reset instructions.

                                    final AlertDialog a = new AlertDialog(ForgotActivity.this, "Forgot");
                                    a.show();
                                    a.findViewById(R.id.btn_ook).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            a.dismiss();
                                            goToMain();
                                        }
                                    });

                                } else {
                                    // Something went wrong. Look at the ParseException to see what's up.

                                    System.out.println("LOG: " + e.getLocalizedMessage());
                                }
                            }
                        });
            }

        });

    }


    private void goToMain() {

        Intent go = new Intent(ForgotActivity.this, MainActivity.class);
        overridePendingTransition(R.anim.slide_bottom, R.anim.slide_up);
        startActivity(go);
    }

}
