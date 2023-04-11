package pro.simpleapp.radiobox.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
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
import pro.simpleapp.radiobox.adapters.RadioAdapter;
import pro.simpleapp.radiobox.adapters.RadioApproveAdapter;
import pro.simpleapp.radiobox.globals.RadioGlobal;
import pro.simpleapp.radiobox.helpers.CategoryAdapter;
import pro.simpleapp.radiobox.helpers.SwipeToDeleteCallback;
import pro.simpleapp.radiobox.helpers.SwipeToDeleteCallbacks;
import pro.simpleapp.radiobox.items.Complaint;
import pro.simpleapp.radiobox.items.Like;
import pro.simpleapp.radiobox.items.Radio;



public class ComplaintFragment extends Fragment {

    View rootView;
    @BindView(R.id.show_compl)
    RecyclerView show_compl;
    @BindView(R.id.txttz)
    TextView txttz;

    RadioApproveAdapter radioAdapter;
    LinearLayoutManager linearLayoutManager;
    List<Radio> rList = new ArrayList<>();
    List<Complaint> ccLIst = new ArrayList<>();
    FragmentTransaction fTrans;
    RadioFragment radioFragmen;


    public ComplaintFragment() {
        // Required empty public constructor
    }


    public static ComplaintFragment newInstance(String param1, String param2) {
        ComplaintFragment fragment = new ComplaintFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_complaint, container, false);
        ButterKnife.bind(this, rootView);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        show_compl.setLayoutManager(linearLayoutManager);
        enableSwipeToDeleteAndUndos();
        txttz.setText("For hide this radio please slide cell to left");
        requestComplaint();
        return rootView;
    }


    private void requestComplaint() {
        ccLIst.clear();
        rList.clear();
        ParseQuery<Complaint> comRequest = Complaint.getAllComplaint();
        comRequest.include(Complaint.C_RADIO);
        comRequest.findInBackground(new FindCallback<Complaint>() {
            @Override
            public void done(List<Complaint> objects, ParseException e) {
                if (e == null) {
                    ccLIst = objects;
                    for (Complaint com : objects) {
                        Radio rdo = com.getRadio();
                        rList.add(rdo);

                    }
                }
                makeC();

            }
        });

    }


    private void makeC() {
        radioAdapter = new RadioApproveAdapter(rList, requireActivity(), false);
        show_compl.setAdapter(radioAdapter);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(requireActivity(), resId);
        show_compl.setLayoutAnimation(animation);
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


    private void enableSwipeToDeleteAndUndos() {
        SwipeToDeleteCallbacks swipeToDeleteCallback = new SwipeToDeleteCallbacks(requireActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                Complaint complaint = ccLIst.get(position);
                Radio radio = complaint.getRadio();
                String oid = radio.getRUser().getPlayerId();
                sendPushtoUser(oid, "We re sorry, but we are blocking your radio because it doesn t work.");
                radio.setApproved(false);
                radio.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        complaint.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                radioAdapter.notifyItemRemoved(position);
                                ccLIst.clear();
                                rList.clear();
                                makeC();
                            }
                        });
                    }
                });

            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(show_compl);
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