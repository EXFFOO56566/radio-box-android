package pro.simpleapp.radiobox.loginView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.jaeger.library.StatusBarUtil;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.BaseActivity;
import pro.simpleapp.radiobox.MainActivity;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.dialogs.WtDialog;
import pro.simpleapp.radiobox.items.User;


public class LoginParseActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_need_offset)
    View mViewNeedOffset;
    @BindView(R.id.username_edt)
    EditText username_edt;
    @BindView(R.id.passw_edt)
    EditText passw_edt;
    @BindView(R.id.reg_btn)
    Button reg_btn;
    @BindView(R.id.txt_fgt)
    TextView txt_fgt;
    @BindView(R.id.regz_btn)
    Button register_btn;

    User user;
    WtDialog wtDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_parse);
        ButterKnife.bind(this);

        StatusBarUtil.setTranslucentForImageView(LoginParseActivity.this, 0, mViewNeedOffset);
        StatusBarUtil.setLightMode(LoginParseActivity.this);

        wtDialog = new WtDialog(this);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wtDialog.show();

                String username = username_edt.getText().toString();
                String pass = passw_edt.getText().toString();

                ParseUser.logInInBackground(username, pass, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (e == null) {

                            ParseInstallation.getCurrentInstallation().put("user", user);
                            ParseInstallation.getCurrentInstallation().saveInBackground();
                            ((User) user).setInstallation(ParseInstallation.getCurrentInstallation());
                            user.saveInBackground();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    goToMain();
                                    wtDialog.dismiss();

                                }
                            }, 5000);

                        } else {

                        }
                    }
                });

            }
        });

        txt_fgt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent go = new Intent(LoginParseActivity.this, ForgotActivity.class);
                overridePendingTransition(R.anim.slide_bottom, R.anim.slide_up);
                startActivity(go);

            }
        });

        register_btn.setOnClickListener(view -> {
            Intent go = new Intent(LoginParseActivity.this, RegisterParseActivity.class);
            overridePendingTransition(R.anim.slide_bottom, R.anim.slide_up);
            startActivity(go);

        });
    }


    private void goToMain() {

        Intent go = new Intent(LoginParseActivity.this, MainActivity.class);
        overridePendingTransition(R.anim.slide_bottom, R.anim.slide_up);
        startActivity(go);
    }

}
