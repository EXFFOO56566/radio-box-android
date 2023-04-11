package pro.simpleapp.radiobox.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.onesignal.OneSignal;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.adapters.CategoryAdapter;
import pro.simpleapp.radiobox.adapters.RadioAdapter;
import pro.simpleapp.radiobox.adapters.RadioApproveAdapter;
import pro.simpleapp.radiobox.dialogs.ReviewDialog;
import pro.simpleapp.radiobox.globals.RadioGlobal;
import pro.simpleapp.radiobox.items.Category;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.User;



public class UserRadioFragment extends Fragment {

    View rootViev;

    //  @BindView(R.id.show_radio_cat)
    //  RecyclerView show_radi_cat;
    @BindView(R.id.show_radio)
    RecyclerView show_radio;
    @BindView(R.id.txttzt)
    TextView txttzt;


    List<Radio> rList = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    CategoryAdapter categoryAdapter;
    RadioApproveAdapter radioAdapter;
    FragmentTransaction fTrans;
    RadioFragment radioFragmen;

    String playerID;


    public UserRadioFragment() {
        // Required empty public constructor
    }


    public static UserRadioFragment newInstance(String param1, String param2) {
        UserRadioFragment fragment = new UserRadioFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootViev = inflater.inflate(R.layout.fragment_user_radio, container, false);
        ButterKnife.bind(this, rootViev);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        show_radio.setLayoutManager(linearLayoutManager);
        getRd();
        txttzt.setText(R.string.apptove_txt);
        return rootViev;
    }


    private void getRd() {
        ParseQuery<Radio> rQuery = Radio.getAllPost();
        rQuery.include("user");
        rQuery.whereEqualTo(Radio.R_APPROVED, false);
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
        radioAdapter = new RadioApproveAdapter(rList, requireActivity(), false);
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
                final ReviewDialog reviewDialog = new ReviewDialog(requireActivity());
                reviewDialog.show();
                reviewDialog.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Radio radio = rList.get(position);
                        User ur = radio.getRUser();
                        playerID = ur.getPlayerId();
                        radio.setApproved(true);
                        radio.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                getRd();
                                reviewDialog.dismiss();
                                sendPushtoUser(playerID, getString(R.string.approved));

                            }
                        });

                    }
                });
                reviewDialog.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Radio radio = rList.get(position);
                        User ur = radio.getRUser();
                        playerID = ur.getPlayerId();
                        radio.setApproved(true);
                        radio.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                getRd();
                                reviewDialog.dismiss();
                                sendPushtoUser(playerID, getString(R.string.not_approved));

                            }

                        });

                    }
                });
            }
        });

    }


    private void sendPushtoUser(String onesignal, String text) {
        try {
            OneSignal.postNotification(new JSONObject("{'contents': {'en':'" + getResources().getString(R.string.from_admin) + " " + text + "'}, 'include_player_ids': ['" + onesignal + "']}"),
                    new OneSignal.PostNotificationResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                        }


                        @Override
                        public void onFailure(JSONObject response) {
                            Log.i("OneSignalExample", "postNotification Failure: " + response.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}