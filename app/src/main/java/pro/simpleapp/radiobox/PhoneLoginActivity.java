package pro.simpleapp.radiobox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.appizona.yehiahd.fastsave.FastSave;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.jaeger.library.StatusBarUtil;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import pro.simpleapp.radiobox.dialogs.TopAlert;
import pro.simpleapp.radiobox.helpers.Rstring;
import pro.simpleapp.radiobox.items.User;
import pro.simpleapp.radiobox.otp.OtpEditText;



public class PhoneLoginActivity extends BaseActivity {

    private Button sendVerificationCodeButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    EditText editTextCarrierNumber;
    LinearLayout linearLayout;
    OtpEditText validationCodeText;
    TextView sms;
    LinearLayout vernb;
    private View mViewNeedOffset;
    Toolbar mToolbar;
    LinearLayout toast_alert;
    LottieAnimationView error_animation;
    TextView error_txt;
    TopAlert topAlert;
    FirebaseAuth mAuth;
    String UID;
    String userName;
    String password;
    String emails;
    String owenerPhone;
    ParseUser currentUser;
    CountryCodePicker ccp;
    Boolean isVAlidPhone;
    ParseFile photo;
    String playerIdOnesignal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        InitializeField();
        StatusBarUtil.setLightMode(PhoneLoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        mAuth.useAppLanguage();
        sendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "+" + ccp.getFullNumber();
                owenerPhone = phoneNumber;
                System.out.println("PPP - O - - " + phoneNumber);
                if (TextUtils.isEmpty(phoneNumber)) {
                    topAlert.build(getString(R.string.phone_number_require), error_animation, error_txt, "error.json", toast_alert);
                } else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            PhoneLoginActivity.this,
                            callbacks);
                }
                hideKeyboard(PhoneLoginActivity.this);
                showKeyboard(validationCodeText);

            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }


            @Override
            public void onVerificationFailed(FirebaseException e) {
                sendVerificationCodeButton.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
                validationCodeText.setVisibility(View.INVISIBLE);
                sms.setVisibility(View.INVISIBLE);
                vernb.setVisibility(View.INVISIBLE);
                topAlert.build(getString(R.string.invalid_phone_number), error_animation, error_txt, "1_e.json", toast_alert);
            }


            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                validationCodeText.setVisibility(View.VISIBLE);
                sms.setVisibility(View.VISIBLE);
                vernb.setVisibility(View.VISIBLE);
                topAlert.build(getString(R.string.code_has_been_sent), error_animation, error_txt, "1_s.json", toast_alert);

            }

        };
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.resetToDefaultCountry();
        editTextCarrierNumber = (EditText) findViewById(R.id.phone_number_input);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);
        ccp.setAutoDetectedCountry(true);
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                isVAlidPhone = isValidNumber;
            }
        });
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
            }
        });
        validationCodeText.setOnCompleteListener(new pro.simpleapp.radiobox.otp.OnCompleteListener() {

            @Override
            public void onComplete(String value) {
                sendVerificationCodeButton.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
                String verificationCode = value;
                if (TextUtils.isEmpty(verificationCode)) {
                    topAlert.build(getString(R.string.please_write_verification_code), error_animation, error_txt, "error.json", toast_alert);
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });
        reg();

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


    private void InitializeField() {
        topAlert = new TopAlert();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewNeedOffset = findViewById(R.id.view_need_offset);
        StatusBarUtil.setTranslucentForImageView(PhoneLoginActivity.this, 0, mViewNeedOffset);
        sendVerificationCodeButton = findViewById(R.id.send_verification_button);
        linearLayout = (LinearLayout) findViewById(R.id.nb);
        validationCodeText = (OtpEditText) findViewById(R.id.validationEditText);
        sms = (TextView) findViewById(R.id.txt_sms);
        vernb = (LinearLayout) findViewById(R.id.vernb);
        toast_alert = (LinearLayout) findViewById(R.id.error_l);
        error_animation = (LottieAnimationView) findViewById(R.id.animation_view_error);
        error_txt = (TextView) findViewById(R.id.error_textview);

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UID = task.getResult().getUser().getUid();
                            topAlert.build(getString(R.string.congrat_phone_login), error_animation, error_txt, "error.json", toast_alert);
                            SendUserToMainActivity();

                        } else {
                            String message = task.getException().getLocalizedMessage().toString();
                            topAlert.build(getString(R.string.error) + " " + message, error_animation, error_txt, "error.json", toast_alert);
                        }
                    }
                });
    }


    private void SendUserToMainActivity() {
        userName = owenerPhone.toString() + "@app.com";
        password = UID;
        emails = userName;
        owenerPhone = owenerPhone.toString();
        FastSave.getInstance().saveString("owener", owenerPhone);
        currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            ParseUser.logInInBackground(userName, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {

                        OSDeviceState deviceState = OneSignal.getDeviceState();
                        playerIdOnesignal = deviceState != null ? deviceState.getUserId() : null;

                        ParseInstallation.getCurrentInstallation().put("user", user);
                        ParseInstallation.getCurrentInstallation().saveInBackground();
                        ((User) user).setInstallation(ParseInstallation.getCurrentInstallation());
                        ((User) user).setPlayerId(playerIdOnesignal);
                        user.saveInBackground();
                        goToMAin();

                    } else {
                        registration(userName, password, emails);

                    }
                }
            });

        } else {
            goToMAin();

        }

    }


    private void registration(String username, String password, String email) {
        OSDeviceState deviceState = OneSignal.getDeviceState();
        playerIdOnesignal = deviceState != null ? deviceState.getUserId() : null;

        Rstring d = new Rstring();
        String r = d.random(8);
        final User newUser = new User();
        newUser.setEmail(email);
        newUser.setUserName(username);
        newUser.setPassword(password);
        newUser.setUserNikname("App user");
        newUser.setUaerPhoto(photo);
        newUser.setPlayerId(playerIdOnesignal);
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseInstallation.getCurrentInstallation().put("user", newUser);
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                    ((User) newUser).setInstallation(ParseInstallation.getCurrentInstallation());
                    newUser.saveInBackground();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gotoAdditional();

                        }
                    }, 1000);
                } else {
                    System.out.println("Error radiobox reg - " + e.getLocalizedMessage());

                }
            }
        });

    }


    private void gotoAdditional() {
        Intent intent = new Intent(PhoneLoginActivity.this, AddInfoActivity.class);
        startActivity(intent);

    }


    private void goToMAin() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PhoneLoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public void showKeyboard(final OtpEditText ettext) {
        ettext.requestFocus();
        ettext.postDelayed(new Runnable() {
                               @Override
                               public void run() {
                                   InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                   keyboard.showSoftInput(ettext, 0);
                               }
                           }
                , 200);
    }


    @Override
    protected void setStatusBar() {
        mViewNeedOffset = findViewById(R.id.view_need_offset);
    }


}
