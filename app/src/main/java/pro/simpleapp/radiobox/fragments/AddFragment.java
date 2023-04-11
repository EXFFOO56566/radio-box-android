package pro.simpleapp.radiobox.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.MainActivity;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.dialogs.ChooseCategoryDialog;
import pro.simpleapp.radiobox.helpers.CategoryAdapter;
import pro.simpleapp.radiobox.helpers.CropBitmap;
import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.User;

import static android.app.Activity.RESULT_OK;
import static com.yalantis.ucrop.UCropFragment.TAG;



public class AddFragment extends Fragment implements UCropFragmentCallback {

    View rootView;

    @BindView(R.id.text_name_station)
    EditText text_name_st;
    @BindView(R.id.text_url_stream)
    EditText text_url_st;
    @BindView(R.id.publish)
    Button btn_publish;
    @BindView(R.id.categ_btn)
    MaterialButton categ_btn;

    @BindView(R.id.img_add)
    RoundedImageView roundedImageView;
    @BindView(R.id.image_add_btn)
    ImageButton add_image_ntn;

    // private static final String DEMO_PHOTO_PATH = "OURO";
    ParseFile photo;
    User user;
    // String colorz;
    // String textColor;
    CropBitmap cropBitmap;
    Category category;


    private int requestMode = 1;
    //  private UCropFragment fragment;
    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    List<Category> categoryList;


    public AddFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add, container, false);
        ButterKnife.bind(this, rootView);
        user = (User) ParseUser.getCurrentUser();
        cropBitmap = new CropBitmap(requireContext());
        add_image_ntn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });
        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Radio radio = new Radio();
                radio.setCategory(category);
                radio.setName(text_name_st.getText().toString());
                radio.setpStream(text_url_st.getText().toString());
                radio.setLogo(photo);
                radio.setRating(0);
                radio.setVoiting(0);
                radio.setRUser(user);
                radio.setCategory(category);
                radio.setApproved(false);
                radio.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(requireActivity(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            System.out.println("ERR = = ==      " + e.getLocalizedMessage());
                        }

                    }
                });

            }
        });
        getCats();
        categ_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChooseCategoryDialog chooseCategoryDialog = new ChooseCategoryDialog(requireActivity(), categoryList, requireActivity());
                chooseCategoryDialog.show();
                chooseCategoryDialog.categoryAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        System.out.println("CCC = = = = =  " + position);
                        chooseCategoryDialog.dismiss();
                        categ_btn.setText(categoryList.get(position).getCategory());
                        category = categoryList.get(position);
                    }


                    @Override
                    public void onItemDeleteClicked(int position) {
                    }
                });

            }
        });
        return rootView;
    }


    private void getCats() {
        ParseQuery<Category> cQuery = Category.getAllCategory();
        cQuery.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                if (e == null) {
                    categoryList = objects;
                }
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
                    Toast.makeText(getActivity(), "UPS", Toast.LENGTH_SHORT).show();
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                roundedImageView.setImageBitmap(bitmap);
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
        uCrop.start((AppCompatActivity) getActivity());
    }


    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setToolbarWidgetColor(Color.WHITE);
        options.setToolbarWidgetColor(ContextCompat.getColor(requireActivity(), R.color.white));
        options.setToolbarColor(Color.BLACK);
        options.setMaxBitmapSize(640);
        options.setAspectRatioOptions(0,
                new AspectRatio("1:1", 1, 1));
        return uCrop.withOptions(options);
    }


    private UCrop basisConfig(@NonNull UCrop uCrop) {
        return uCrop;
    }


}