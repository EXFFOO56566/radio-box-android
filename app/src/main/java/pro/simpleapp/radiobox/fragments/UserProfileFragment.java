package pro.simpleapp.radiobox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.rishabhharit.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.adapters.RadioAdapter;
import pro.simpleapp.radiobox.globals.RadioGlobal;
import pro.simpleapp.radiobox.globals.UserGlobal;
import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.User;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    View rootView;

    @BindView(R.id.user_ava)
    RoundedImageView user_ava;
    @BindView(R.id.user_name)
    TextView user_name;
    @BindView(R.id.show_user_radio)
    RecyclerView show_user_radio;

    User user;
    List<Radio> rList = new ArrayList<>();
    LinearLayoutManager layoutManager;
    RadioAdapter radioAdapter;
    RadioFragment radioFragmen;
    FragmentTransaction fTrans;


    public UserProfileFragment() {
        // Required empty public constructor
    }


    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
        rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this, rootView);
        user = UserGlobal.getInstance().getUser();
        String urlAva = user.getUserPhoto().getUrl();
        Glide.with(this)
                .load(urlAva).into(user_ava);
        user_name.setText(user.getUserNikname());
        layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        show_user_radio.setLayoutManager(layoutManager);
        getRadio();
        return rootView;
    }


    private void getRadio() {
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
        ((SimpleItemAnimator) show_user_radio.getItemAnimator()).setSupportsChangeAnimations(true);
        radioAdapter = new RadioAdapter(rList, requireActivity(), false);
        show_user_radio.setAdapter(radioAdapter);
        show_user_radio.setHasFixedSize(true);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(requireActivity(), resId);
        show_user_radio.setLayoutAnimation(animation);
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