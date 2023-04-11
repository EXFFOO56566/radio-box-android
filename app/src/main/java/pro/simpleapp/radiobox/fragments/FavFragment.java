package pro.simpleapp.radiobox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import com.parse.ParseUser;

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
import pro.simpleapp.radiobox.items.Like;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.User;

import static pro.simpleapp.radiobox.prfs.Prfs.ADS_OFF;



public class FavFragment extends Fragment {

    View rootView;

    @BindView(R.id.show_fav)
    RecyclerView show_fav;

    @BindView(R.id.ads)
    LinearLayout ads_lay;

    LinearLayoutManager linearLayoutManager;
    List<Radio> rList = new ArrayList<>();
    List<Like> lList = new ArrayList<>();
    RadioAdapter radioAdapter;
    RadioFragment radioFragmen;
    FragmentTransaction fTrans;

    String deviceId;
    Md5Class md5Class;
    AdRequest adRequest;
    User user;


    public FavFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fav, container, false);
        ButterKnife.bind(this, rootView);
        user = (User) ParseUser.getCurrentUser();
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        show_fav.setLayoutManager(linearLayoutManager);
        getFav();

        /// adsd

        AdmobHelperr admobHelperr = new AdmobHelperr();
        admobHelperr.setsAdmob(requireActivity(), ads_lay);


        return rootView;
    }


    private void getFav() {
        ParseQuery<Like> lQuery = Like.getAllLike();
        lQuery.whereEqualTo(Like.L_USER, user);
        lQuery.include("user");
        lQuery.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                if (e == null) {
                    lList = objects;
                    for (Like fav : objects) {
                        try {
                            Radio d = fav.getRadio().fetchIfNeeded();
                            rList.add(d);

                        } catch (ParseException e1) {
                        }

                    }
                    make();

                }
            }
        });

    }


    private void make() {
        radioAdapter = new RadioAdapter(rList, requireActivity(), false);
        show_fav.setAdapter(radioAdapter);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(requireActivity(), resId);
        show_fav.setLayoutAnimation(animation);
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


}