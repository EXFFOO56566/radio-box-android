package pro.simpleapp.radiobox.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.parse.DeleteCallback;
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
import pro.simpleapp.radiobox.adapters.CategoryAdapter;
import pro.simpleapp.radiobox.adapters.RadioAdapter;
import pro.simpleapp.radiobox.globals.RadioGlobal;
import pro.simpleapp.radiobox.helpers.AdmobHelperr;
import pro.simpleapp.radiobox.helpers.Md5Class;
import pro.simpleapp.radiobox.helpers.SwipeToDeleteCallback;
import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.Like;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.User;

import static pro.simpleapp.radiobox.prfs.Prfs.ADS_OFF;



public class MyRadioFragment extends Fragment {

    View rootViev;


    @BindView(R.id.show_radio)
    RecyclerView show_radio;

    @BindView(R.id.ads)
    LinearLayout ads_lay;
    @BindView(R.id.txtt)
    TextView txtt;

    List<Radio> rList = new ArrayList<>();

    LinearLayoutManager layoutManager;
    LinearLayoutManager linearLayoutManager;
    User user;

    RadioAdapter radioAdapter;
    FragmentTransaction fTrans;
    RadioFragment radioFragmen;

    String deviceId;
    Md5Class md5Class;
    AdRequest adRequest;
    Radio radioDelete;


    public MyRadioFragment() {
        // Required empty public constructor
    }


    public static MyRadioFragment newInstance(String param1, String param2) {
        MyRadioFragment fragment = new MyRadioFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootViev = inflater.inflate(R.layout.fragment_my_radio, container, false);
        ButterKnife.bind(this, rootViev);
        user = (User) ParseUser.getCurrentUser();
        enableSwipeToDeleteAndUndo();
        txtt.setText("For delete radio please slide cell to left");
        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        show_radio.setLayoutManager(linearLayoutManager);
        gemY();

        // adsd
        AdmobHelperr admobHelperr = new AdmobHelperr();
        admobHelperr.setsAdmob(requireActivity(), ads_lay);

        return rootViev;
    }


    private void gemY() {
        ParseQuery<Radio> rQuery = Radio.getAllPost();
        rQuery.whereEqualTo(Radio.R_USER, user);
        rQuery.include("user");
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
        radioAdapter = new RadioAdapter(rList, requireActivity(), false);
        show_radio.setAdapter(radioAdapter);
        show_radio.setHasFixedSize(true);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(requireActivity(), resId);
        show_radio.setLayoutAnimation(animation);
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


    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(requireActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                radioDelete = rList.get(position);
                ParseQuery<Like> lQuery = Like.getAllLike();
                lQuery.whereEqualTo(Like.L_RADIO, radioDelete);
                lQuery.findInBackground(new FindCallback<Like>() {
                    @Override
                    public void done(List<Like> objects, ParseException e) {
                        for (Like like : objects) {
                            like.deleteEventually();
                        }
                    }

                });
                deleteRadio(position);

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(show_radio);
    }


    private void deleteRadio(int position) {
        radioDelete.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                radioAdapter.notifyItemRemoved(position);
                gemY();

            }
        });
    }


}