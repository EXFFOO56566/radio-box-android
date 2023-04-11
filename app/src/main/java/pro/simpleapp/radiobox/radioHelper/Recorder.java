package pro.simpleapp.radiobox.radioHelper;

import android.content.Context;
import android.os.AsyncTask;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import pro.simpleapp.radiobox.helpers.Utils;
import pro.simpleapp.radiobox.prfs.Prfs;

import static com.parse.Parse.getApplicationContext;

public class Recorder extends AsyncTask {
    private Context context;
    private String urlPath;
    private String recordedFileName;
    private boolean isRecording = false;
    // private MediaPlayer mediaPlayer;

    public Recorder() {
    }

    public Recorder(Context context, String url, String recordedFilePath) {
        this.context = context;
        this.urlPath = url;
        this.recordedFileName = recordedFilePath;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        isRecording = true;

        try {
            URL url = new URL(urlPath);
            InputStream inputStream = url.openStream();

            File file = new File(Utils.getRootDirPath(getApplicationContext()) + Prfs.FOLDER_REC);

            file.mkdirs();

            final File files = new File(file, recordedFileName);

            OutputStream outputStream = new FileOutputStream(files);

            byte[] buffer = new byte[4 * 1024];
            int read;

            while ((read = inputStream.read(buffer)) != -1) {
                if (isCancelled())
                    break;
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void record() {
        File file = new File(Utils.getRootDirPath(getApplicationContext()) + Prfs.FOLDER_REC + recordedFileName);
        if (file.exists()) {
            file.delete();
        }

        //  player.play();

        this.execute();

    }

    public void stopRecording() {
        isRecording = false;
        this.cancel(true);

        // player.stop();
    }

   /* public void playFromRecording()
    {
        try {
            File file = new File(context.getCacheDir(), recordedFileName);
          //  mediaPlayer.setDataSource(file.getPath());
          //  mediaPlayer.prepare(); // might take long! (for buffering, etc)
           // mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } */

   /* public void stopPlayingFromRecord()
    {
       // mediaPlayer.stop();
       // mediaPlayer.reset();
    } */

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getRecordedFileName() {
        return recordedFileName;
    }

    public void setRecordedFileName(String recordedFileName) {
        this.recordedFileName = recordedFileName;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        this.isRecording = recording;
    }

    // public MediaPlayer getMediaPlayer() {
    //     return mediaPlayer;
    // }

    // public void setMediaPlayer(MediaPlayer mediaPlayer) {
    //     this.mediaPlayer = mediaPlayer;
    // }

}
