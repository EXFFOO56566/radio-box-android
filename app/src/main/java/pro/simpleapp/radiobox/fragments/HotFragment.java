package pro.simpleapp.radiobox.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.BuildConfig;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.adapters.RadioAdapter;
import pro.simpleapp.radiobox.globals.RadioGlobal;
import pro.simpleapp.radiobox.helpers.AdmobHelperr;
import pro.simpleapp.radiobox.helpers.CategoryAdapter;
import pro.simpleapp.radiobox.helpers.Md5Class;
import pro.simpleapp.radiobox.items.Radio;

import static pro.simpleapp.radiobox.prfs.Prfs.ADS_OFF;



public class HotFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    View rootView;
    @BindView(R.id.shoe_hot)
    RecyclerView show_hot;

    @BindView(R.id.ads)
    LinearLayout ads_lay;
    @BindView(R.id.searchBar)
    MaterialSearchBar materialSearchBar;

    LinearLayoutManager linearLayoutManager;
    RadioAdapter radioAdapter;
    List<Radio> rList = new ArrayList<>();

    RadioFragment radioFragmen;
    FragmentTransaction fTrans;

    String deviceId;
    Md5Class md5Class;
    AdRequest adRequest;


    public HotFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_hot, container, false);
        ButterKnife.bind(this, rootView);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        show_hot.setLayoutManager(linearLayoutManager);
        getHot();
        materialSearchBar.setHint("Search radio");
        materialSearchBar.setOnSearchActionListener(this);
        materialSearchBar.isSuggestionsVisible();

        AdmobHelperr admobHelperr = new AdmobHelperr();
        admobHelperr.setsAdmob(requireActivity(), ads_lay);


        return rootView;
    }


    private void getHot() {
        rList.clear();
        ParseQuery<Radio> f = Radio.getAllPost();

        f.include("user");
        f.setLimit(25);
        f.orderByDescending(Radio.R_RARING);
        f.findInBackground(new FindCallback<Radio>() {
            @Override
            public void done(List<Radio> objects, ParseException e) {
                rList = objects;
                makeDraw();

            }
        });

    }


    private void makeDraw() {
        radioAdapter = new RadioAdapter(rList, requireActivity(), false);
        show_hot.setAdapter(radioAdapter);
        show_hot.setLayoutManager(new LinearLayoutManager(requireContext()));
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(requireActivity(), resId);
        show_hot.setLayoutAnimation(animation);
        radioAdapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
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


    @Override
    public void onSearchStateChanged(boolean enabled) {
    }


    @Override
    public void onSearchConfirmed(CharSequence text) {
        getSearchRadio(text.toString());

    }


    private void getSearchRadio(String textSeach) {
        ParseQuery<Radio> rQuery = Radio.getAllPost();
        rQuery.whereFullText(Radio.R_NAME, textSeach);
        rQuery.findInBackground(new FindCallback<Radio>() {
            @Override
            public void done(List<Radio> objects, ParseException e) {
                if (e == null) {
                    rList = objects;
                    makeDraw();
                }
            }
        });
    }


    @Override
    public void onButtonClicked(int buttonCode) {
    }


}