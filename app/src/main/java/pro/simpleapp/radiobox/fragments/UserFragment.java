package pro.simpleapp.radiobox.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.StartActivity;
import pro.simpleapp.radiobox.adapters.AudioAdapter;
import pro.simpleapp.radiobox.dialogs.NameDialog;
import pro.simpleapp.radiobox.helpers.CropBitmap;
import pro.simpleapp.radiobox.items.User;

import static com.yalantis.ucrop.UCropFragment.TAG;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements UCropFragmentCallback {

    View rootView;
    @BindView(R.id.lgout)
    LinearLayout logout_btn;

    @BindView(R.id.user_txt)
    TextView user_txt;

    @BindView(R.id.fv)
    LinearLayout fav_btn;

    @BindView(R.id.m)
    LinearLayout my_radio_btn;


    @BindView(R.id.approvez)
    LinearLayout btn_approve;

    @BindView(R.id.user_ava)
    RoundedImageView user_ava;

    @BindView(R.id.change_ava)
    ImageButton change_ava;

    @BindView(R.id.change_names)
    ImageButton change_names;

    @BindView(R.id.complaint)
    LinearLayout complaint;


    User user;

    FavFragment favFragment;
    FragmentTransaction fTrans;
    RecordsFragment recordsFragment;
    MyRadioFragment myRadioFragment;
    UserRadioFragment userRadioFragment;
    ComplaintFragment complaintFragment;
    CropBitmap cropBitmap;

    Boolean isAdmin;
    ParseFile photo;
    private int requestMode = 1;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";


    public UserFragment() {
        // Required empty public constructor
    }


    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, rootView);
        user = (User) ParseUser.getCurrentUser();
        user_txt.setText(user.getUserNikname());
        String userUrlAva = user.getUserPhoto().getUrl();
        Glide.with(this)
                .load(userUrlAva).into(user_ava);
        isAdmin = user.getUserAsAdmin();
        if (!isAdmin) {
            btn_approve.setVisibility(View.GONE);
            complaint.setVisibility(View.GONE);
        }
        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                complaintFragment = new ComplaintFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fTrans.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
                fTrans.replace(R.id.content, complaintFragment);
                fTrans.addToBackStack(null);
                fTrans.show(complaintFragment);
                fTrans.commit();

            }
        });
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireActivity(), StartActivity.class);
                startActivity(intent);

            }
        });
        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favFragment = new FavFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fTrans.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
                fTrans.replace(R.id.content, favFragment);
                fTrans.addToBackStack(null);
                fTrans.show(favFragment);
                fTrans.commit();
            }
        });
        my_radio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRadioFragment = new MyRadioFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fTrans.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
                fTrans.replace(R.id.content, myRadioFragment);
                fTrans.addToBackStack(null);
                fTrans.show(myRadioFragment);
                fTrans.commit();
            }
        });
        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRadioFragment = new UserRadioFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fTrans.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
                fTrans.replace(R.id.content, userRadioFragment);
                fTrans.addToBackStack(null);
                fTrans.show(userRadioFragment);
                fTrans.commit();
            }
        });
        change_ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();

            }
        });
        change_names.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NameDialog nameDialog = new NameDialog(requireActivity());
                nameDialog.show();
                nameDialog.findViewById(R.id.btn_ook).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String f = nameDialog.name.getText().toString();
                        user.setUserNikname(f);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                nameDialog.dismiss();
                                user_txt.setText(f);

                            }
                        });

                    }
                });

            }
        });
        cropBitmap = new CropBitmap(requireActivity());
        return rootView;
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
        if (resultCode == requireActivity().RESULT_OK) {
            if (requestCode == requestMode) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCrop(selectedUri);
                } else {
                    Toast.makeText(requireActivity(), "UPS", Toast.LENGTH_SHORT).show();
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
    }


    @Override
    public void loadingProgress(boolean showLoader) {
    }


    @Override
    public void onCropFinish(UCropFragment.UCropResult result) {
        int r = requireActivity().RESULT_OK;
        switch (result.mResultCode) {
            case 0:
                handleCropResult(result.mResultData);
                break;
            case UCrop.RESULT_ERROR:
                handleCropError(result.mResultData);
                break;
            default:
                break;
        }
    }


    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            try {
                Uri uri = resultUri;
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                user_ava.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte[] image = stream.toByteArray();
                photo = new ParseFile("itm.png", image);
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        user.setUaerPhoto(photo);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                            }
                        });

                    }
                });

            } catch (IOException e) {
            }

        } else {
            Toast.makeText(requireActivity(), "NOT CROPPED", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(requireActivity(), cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireActivity(), "ERROR !!!", Toast.LENGTH_SHORT).show();
        }
    }


    private void startCrop(@NonNull Uri uri) {
        String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(requireActivity().getCacheDir(), destinationFileName)));
        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);
        uCrop.start((AppCompatActivity) requireActivity());

    }


    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setToolbarWidgetColor(ContextCompat.getColor(requireActivity(), R.color.black));
        options.setToolbarWidgetColor(ContextCompat.getColor(requireActivity(), R.color.black));
        options.setToolbarColor(ContextCompat.getColor(requireActivity(), R.color.white));
        options.setAspectRatioOptions(0,
                new AspectRatio("1:1", 1, 1));//,
        return uCrop.withOptions(options);
    }


    private UCrop basisConfig(@NonNull UCrop uCrop) {
        return uCrop;
    }


}