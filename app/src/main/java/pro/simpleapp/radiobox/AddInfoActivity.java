package pro.simpleapp.radiobox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.rishabhharit.roundedimageview.RoundedImageView;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropFragment;
import com.yalantis.ucrop.UCropFragmentCallback;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.helpers.CropBitmap;
import pro.simpleapp.radiobox.items.User;

import static com.yalantis.ucrop.UCropFragment.TAG;



public class AddInfoActivity extends BaseActivity implements UCropFragmentCallback {

    private Toolbar mToolbar;
    private View mViewNeedOffset;


    @BindView(R.id.text_user)
    EditText text_title;
    @BindView(R.id.publish)
    Button btn_publish;
    @BindView(R.id.img_add)
    RoundedImageView roundedImageView;
    @BindView(R.id.image_add_btn)
    ImageButton add_image_ntn;

    ParseFile photo;
    User user;
    CropBitmap cropBitmap;

    private int requestMode = 1;
    private UCropFragment fragment;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        ButterKnife.bind(this);
        StatusBarUtil.setTranslucentForImageView(AddInfoActivity.this, 0, mViewNeedOffset);
        StatusBarUtil.setLightMode(AddInfoActivity.this);
        user = (User) ParseUser.getCurrentUser();
        cropBitmap = new CropBitmap(this);
        add_image_ntn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setUaerPhoto(photo);
                user.setUserNikname(text_title.getText().toString());
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Intent intent = new Intent(AddInfoActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

    }


    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(Intent.createChooser(intent, "Select image"), requestMode);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == requestMode) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCrop(selectedUri);
                } else {
                    Toast.makeText(this, "UPS", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void loadingProgress(boolean showLoader) {
    }


    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        switch (result.mResultCode) {
            case RESULT_OK:
                handleCropResult(result.mResultData);
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
        }
    }


    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            try {
                Uri uri = resultUri;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                roundedImageView.setImageBitmap(bitmap);
                add_image_ntn.setVisibility(View.GONE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] image = stream.toByteArray();
                photo = new ParseFile("itm.png", image);
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                    }
                });

            } catch (IOException e) {
            }

        } else {
            Toast.makeText(this, "NOT CROPPED", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "ERROR !!!", Toast.LENGTH_SHORT).show();
        }
    }


    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(this.getCacheDir(), destinationFileName)));
        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);
        uCrop.start((AppCompatActivity) this);

    }


    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));
        options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.white));
        options.setAspectRatioOptions(0,
                new AspectRatio("1:1", 1, 1));//,
        return uCrop.withOptions(options);
    }


    private UCrop basisConfig(@NonNull UCrop uCrop) {
        return uCrop;
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


}