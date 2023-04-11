package pro.simpleapp.radiobox.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.general.errors.OnInvalidPathListener;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.adapters.AudioAdapter;
import pro.simpleapp.radiobox.dialogs.DeleteRecordDialog;
import pro.simpleapp.radiobox.helpers.Utils;
import pro.simpleapp.radiobox.prfs.Prfs;
import pro.simpleapp.radiobox.radioHelper.RadioService;

import static com.parse.Parse.getApplicationContext;



/**
 * A simple {@link Fragment} subclass.
 */
public class RecordsFragment extends Fragment implements OnInvalidPathListener, JcPlayerManagerListener {

    View rootView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.jcplayer)
    JcPlayerView player;
    private static final String TAG = "TAG";
    private static RecordsFragment instance = null;
    private AudioAdapter audioAdapter;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();


    public RecordsFragment() {
    }


    public static RecordsFragment getInstance() {
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_records, container, false);
        ButterKnife.bind(this, rootView);
        instance = this;
        getRecords();
        Intent intent_close = new Intent(getContext(), RadioService.class);
        intent_close.setAction(RadioService.ACTION_STOP);
        getContext().startService(intent_close);
        return rootView;
    }


    private void getRecords() {
        String dirPath = Utils.getRootDirPath(getApplicationContext()) + Prfs.FOLDER_REC;
        File rirr = new File(dirPath);
        File[] contents = rirr.listFiles();
        if (contents == null) {
        } else if (contents.length == 0) {
        } else {
            if (rirr.isDirectory()) {
                for (File f : rirr.listFiles()) {
                    if (f.isFile()) {
                        String namez = f.getName().replace(".mp3", "");
                        String end_title = namez.replace("_", " ");
                        String[] z = end_title.split("-XYZ-");
                        String filedir = rirr + File.separator + f.getName();
                        jcAudios.add(JcAudio.createFromFilePath(z[1], filedir));

                    }
                    if (jcAudios.size() < 1) {
                    } else {
                        player.initPlaylist(jcAudios, this);
                        adapterSetup();
                    }

                }

            }

        }

    }


    protected void adapterSetup() {
        audioAdapter = new AudioAdapter(player.getMyPlaylist());
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                player.playAudio(player.getMyPlaylist().get(position));

            }


            @Override
            public void onSongItemDeleteClicked(int position) {
                final DeleteRecordDialog deleteRecordDialog = new DeleteRecordDialog(getActivity(), jcAudios.get(position).getTitle());
                deleteRecordDialog.show();
                deleteRecordDialog.findViewById(R.id.btn_delete_d).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = player.getMyPlaylist().get(position).getPath();
                        new File(path).delete();
                        jcAudios.remove(position);
                        audioAdapter.notifyDataSetChanged();
                        deleteRecordDialog.dismiss();

                    }
                });
                deleteRecordDialog.findViewById(R.id.btn_share_mp3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = player.getMyPlaylist().get(position).getPath();
                        Uri uri = Uri.parse(path);
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("audio/*");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share Sound File"));
                        deleteRecordDialog.dismiss();
                    }
                });
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(audioAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }


    @Override
    public void onStop() {
        super.onStop();
       // player.createNotification();
    }


    @Override
    public void onPause() {
        super.onPause();
       // player.createNotification();
        player.kill();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.kill();
    }


    @Override
    public void onPathError(JcAudio jcAudio) {
        Toast.makeText(getContext(), jcAudio.getPath() + " with problems", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPreparedAudio(JcStatus status) {
    }


    @Override
    public void onCompletedAudio() {
    }


    @Override
    public void onPaused(JcStatus status) {
    }


    @Override
    public void onContinueAudio(JcStatus status) {
    }


    @Override
    public void onPlaying(JcStatus status) {
    }


    @Override
    public void onTimeChanged(@NonNull JcStatus status) {
        updateProgress(status);
    }


    @Override
    public void onJcpError(@NonNull Throwable throwable) {
        Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
    }


    private void updateProgress(final JcStatus jcStatus) {
        Log.d(TAG, "Song duration = " + jcStatus.getDuration()
                + "\n song position = " + jcStatus.getCurrentPosition());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float progress = (float) (jcStatus.getDuration() - jcStatus.getCurrentPosition())
                        / (float) jcStatus.getDuration();
                progress = 1.0f - progress;
                audioAdapter.updateProgress(jcStatus.getJcAudio(), progress);
            }
        });
    }


    private void removeItem(int position) {
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        player.removeAudio(player.getMyPlaylist().get(position));
        audioAdapter.notifyItemRemoved(position);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }


    @Override
    public void onStopped(JcStatus status) {
    }


}
