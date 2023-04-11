package pro.simpleapp.radiobox;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.appizona.yehiahd.fastsave.FastSave;
import com.jaeger.library.StatusBarUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.isuru.sheriff.enums.SheriffPermission;
import info.isuru.sheriff.helper.Sheriff;
import info.isuru.sheriff.interfaces.PermissionListener;
import pro.simpleapp.radiobox.globals.CategoryGlobal;
import pro.simpleapp.radiobox.globals.CatsGlobal;
import pro.simpleapp.radiobox.helpers.Alarmz;
import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.User;



public class StartActivity extends BaseActivity implements PermissionListener {

    private Toolbar mToolbar;
    private View mViewNeedOffset;

    @BindView(R.id.button_phone)
    Button phone_btn;

    User user;

    Sheriff sheriffPermission;
    private static final int REQUEST_SINGLE_PERMISSION = 102;
    private static final int REQUEST_MULTIPLE_PERMISSION = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForImageView(StartActivity.this, 0, mViewNeedOffset);
        StatusBarUtil.setLightMode(StartActivity.this);
        phone_btn.setVisibility(View.GONE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                startActivity(intent);

            }
        }, 3000);
        sheriffPermission = Sheriff.Builder()
                .with(this)
                .requestCode(REQUEST_MULTIPLE_PERMISSION)
                .setPermissionResultCallback(this)
                .askFor(SheriffPermission.STORAGE, SheriffPermission.CAMERA)
                .build();
        sheriffPermission.requestPermissions();
        FastSave.getInstance().saveBoolean("internet", isOnline());

       // addCategoryDemo();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        sheriffPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onPermissionsGranted(int requestCode, ArrayList<String> acceptedPermissionList) {


    }


    @Override
    public void onPermissionsDenied(int requestCode, ArrayList<String> deniedPermissionList) {
    }


    private void addCategoryDemo() {
        String[] cats = {"House", "Pop", "Hip-hop"};
        for (int i = 0; i < cats.length; i++) {
            String nm = cats[i];
            System.out.println("CCCCC = = = = = =   " + nm);
            Category ct = new Category();
            ct.setCategory(nm);
            ct.saveEventually();

        }
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}