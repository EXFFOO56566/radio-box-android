package pro.simpleapp.radiobox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.BuildConfig;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.adapters.CategoryAdapter;
import pro.simpleapp.radiobox.adapters.RadioAdapter;
import pro.simpleapp.radiobox.globals.RadioGlobal;
import pro.simpleapp.radiobox.helpers.AdmobHelperr;
import pro.simpleapp.radiobox.helpers.Md5Class;
import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.Radio;

import static pro.simpleapp.radiobox.prfs.Prfs.ADS_OFF;



public class AllRadioFragment extends Fragment {

    View rootViev;

    @BindView(R.id.show_radio_cat)
    RecyclerView show_radi_cat;
    @BindView(R.id.show_radio)
    RecyclerView show_radio;

    @BindView(R.id.ads)
    LinearLayout ads_lay;


    List<Radio> rList = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();

    LinearLayoutManager layoutManager;
    LinearLayoutManager linearLayoutManager;

    CategoryAdapter categoryAdapter;
    RadioAdapter radioAdapter;
    FragmentTransaction fTrans;
    RadioFragment radioFragmen;

    String deviceId;
    Md5Class md5Class;
    AdRequest adRequest;
    int positionz;


    public AllRadioFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootViev = inflater.inflate(R.layout.fragment_all_radio, container, false);
        ButterKnife.bind(this, rootViev);
        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        show_radi_cat.setLayoutManager(layoutManager);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        show_radio.setLayoutManager(linearLayoutManager);
        getCats();

       // adsd
        AdmobHelperr admobHelperr = new AdmobHelperr();
        admobHelperr.setsAdmob(requireActivity(), ads_lay);


        return rootViev;
    }


    private void getCats() {
        ParseQuery<Category> cQuery = Category.getAllCategory();
        cQuery.findInBackground(new FindCallback<Category>() {
            @Override
            public void done(List<Category> objects, ParseException e) {
                if (e == null) {
                    categoryList = objects;
                    makeCat();

                }
            }
        });
    }


    private void makeCat() {
        getRadio(categoryList.get(positionz));
        categoryAdapter = new CategoryAdapter(categoryList, requireActivity(), false);
        show_radi_cat.setAdapter(categoryAdapter);
        categoryAdapter.setOnItemClickListener(new pro.simpleapp.radiobox.helpers.CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                getRadio(categoryList.get(position));
                positionz = position;

            }

            @Override
            public void onItemDeleteClicked(int position) {
            }
        });

    }


    private void getRadio(Category category) {
        ParseQuery<Radio> rQuery = Radio.getAllPost();
        rQuery.whereEqualTo(Radio.R_CATEGORY, category);
        rQuery.include("user");
        rQuery.whereEqualTo(Radio.R_APPROVED, true);
        rQuery.findInBackground(new FindCallback<Radio>() {
            @Override
            public void done(List<Radio> objects, ParseException e) {
                if (e == null) {
                    rList = objects;
                    makeRadio();

                }
            }
        });
    }


    private void makeRadio() {
        ((SimpleItemAnimator) show_radio.getItemAnimator()).setSupportsChangeAnimations(true);
        radioAdapter = new RadioAdapter(rList, requireActivity(), false);
        show_radio.setAdapter(radioAdapter);
        show_radio.setHasFixedSize(true);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(requireActivity(), resId);
        show_radio.setLayoutAnimation(animation);
        radioAdapter.notifyDataSetChanged();
        radioAdapter.setOnItemClickListener(new pro.simpleapp.radiobox.helpers.CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                RadioGlobal.getInstance().setRadio(rList.get(position));
                radioFragmen = new RadioFragment();
                fTrans = getActivity().getSupportFragmentManager().beginTransaction();
                fTrans.setCustomAnimations(R.anim.pull_in_right, R.anim.push_out_left);
                fTrans.replace(R.id.content, radioFragmen);
                fTrans.addToBackStack(null);
                fTrans.show(radioFragmen);
                fTrans.commit();

            }


            @Override
            public void onItemDeleteClicked(int position) {
            }
        });

    }


}