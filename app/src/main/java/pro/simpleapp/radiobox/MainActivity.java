package pro.simpleapp.radiobox;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.appizona.yehiahd.fastsave.FastSave;
import com.fxn.BubbleTabBar;
import com.fxn.OnBubbleClickListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jaeger.library.StatusBarUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import nl.joery.animatedbottombar.AnimatedBottomBar;
import pro.simpleapp.radiobox.dialogs.AlertDialog;
import pro.simpleapp.radiobox.fragments.AddFragment;
import pro.simpleapp.radiobox.fragments.AllRadioFragment;
import pro.simpleapp.radiobox.fragments.HotFragment;
import pro.simpleapp.radiobox.fragments.NoInternetFragment;
import pro.simpleapp.radiobox.fragments.SettingsFragment;
import pro.simpleapp.radiobox.fragments.UserFragment;
import pro.simpleapp.radiobox.helpers.Alarmz;
import pro.simpleapp.radiobox.helpers.Md5Class;
import pro.simpleapp.radiobox.items.User;

import static nl.joery.animatedbottombar.AnimatedBottomBar.*;



public class MainActivity extends BaseActivity {

    @BindView(R.id.bottom_bar)
    AnimatedBottomBar bottom_bar;

    @BindView(R.id.adView)
    AdView mAdView;
    @BindView(R.id.ads)
    LinearLayout ads_lay;

    private Toolbar mToolbar;
    private RelativeLayout mViewNeedOffset;
    FragmentTransaction fTrans;

    AddFragment addFragment;
    UserFragment userFragment;
    AllRadioFragment allRadioFragment;
    HotFragment hotFragment;
    SettingsFragment settingsFragment;
    NoInternetFragment noInternetFragment;

    Boolean isInternet;


    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        StatusBarUtil.setTranslucentForImageView(MainActivity.this, 0, mViewNeedOffset);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewNeedOffset = findViewById(R.id.view_need_offset);
        isInternet = FastSave.getInstance().getBoolean("internet", false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        user = (User) ParseUser.getCurrentUser();
        // isAdmin();
        addFragment = new AddFragment();
        userFragment = new UserFragment();
        allRadioFragment = new AllRadioFragment();
        hotFragment = new HotFragment();
        settingsFragment = new SettingsFragment();
        noInternetFragment = new NoInternetFragment();
        bottom_bar.setOnTabInterceptListener(new OnTabInterceptListener() {
            @Override
            public boolean onTabIntercepted(int i, @Nullable Tab tab, int i1, @NotNull Tab tab1) {
                switch (i1) {
                    case 0:
                        openFragment(allRadioFragment);
                        break;
                    case 1:
                        openFragment(hotFragment);
                        break;
                    case 2:
                        if (user != null) {
                            openFragment(addFragment);
                        } else {
                            Alarmz alarmz = new Alarmz();
                            alarmz.als(MainActivity.this);

                        }
                        break;
                    case 3:
                        if (user != null) {
                            openFragment(userFragment);
                        } else {
                            Alarmz alarmz = new Alarmz();
                            alarmz.als(MainActivity.this);
                        }
                        break;
                    case 4:
                        openSettinfs(settingsFragment);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        openFragment(allRadioFragment);
    }


    private void openFragment(Fragment fragment) {
        if (isInternet) {
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.content, fragment);
            fTrans.addToBackStack(null);
            fTrans.show(fragment);
            fTrans.commit();

        } else {
            fTrans = getSupportFragmentManager().beginTransaction();
            fTrans.replace(R.id.content, noInternetFragment);
            fTrans.addToBackStack(null);
            fTrans.show(noInternetFragment);
            fTrans.commit();

        }

    }


    private void openSettinfs(Fragment fragment) {
        fTrans = getSupportFragmentManager().beginTransaction();
        fTrans.replace(R.id.content, fragment);
        fTrans.addToBackStack(null);
        fTrans.show(fragment);
        fTrans.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }


    private void isAdmin() {
        user.setUserAsAdmin(true);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });

    }


}