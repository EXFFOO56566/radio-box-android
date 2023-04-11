package pro.simpleapp.radiobox.loginView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.jaeger.library.StatusBarUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.BaseActivity;
import pro.simpleapp.radiobox.MainActivity;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.dialogs.WtDialog;
import pro.simpleapp.radiobox.helpers.Rstring;
import pro.simpleapp.radiobox.items.User;


public class RegisterParseActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_need_offset)
    View mViewNeedOffset;
    @BindView(R.id.username_edt)
    EditText username_edt;
    @BindView(R.id.mail_edt)
    EditText mail_edt;
    @BindView(R.id.passw_edt)
    EditText passw_edt;
    @BindView(R.id.reg_btn)
    Button reg_btn;
    @BindView(R.id.nikname)
    EditText nick_name;
    User user;
    WtDialog wtDialog;
    ParseFile photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_parse);
        ButterKnife.bind(this);

        StatusBarUtil.setTranslucentForImageView(RegisterParseActivity.this, 0, mViewNeedOffset);
        StatusBarUtil.setLightMode(RegisterParseActivity.this);
        reg();

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wtDialog = new WtDialog(RegisterParseActivity.this);
                wtDialog.show();

                String username = username_edt.getText().toString();
                String email = mail_edt.getText().toString();
                String password = passw_edt.getText().toString();
                String nickname = nick_name.getText().toString();

                Rstring nick = new Rstring();

               // String userNick = nick.random(8);

                user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setEmail(email);
                user.setUserNikname(nickname);
                user.setUserAsAdmin(false);
                user.setUaerPhoto(photo);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    goToMain();
                                    wtDialog.dismiss();
                                }
                            }, 6000);
                        }

                    }
                });

            }
        });
    }


    private void goToMain() {

        Intent go = new Intent(RegisterParseActivity.this, MainActivity.class);
        overridePendingTransition(R.anim.slide_bottom, R.anim.slide_up);
        startActivity(go);
    }

    public void reg() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        photo = new ParseFile("userpicture.png", image);
        photo.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
            }
        });

    }

}
