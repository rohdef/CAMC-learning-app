package dk.au.cs.listr.camclearner;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.google.common.base.Charsets;
import com.google.common.io.Closer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rohdef on 12/8/13.
 */
public class WindowFilledEvent extends Activity {
    private final Logger logger = LoggerFactory.getLogger(WindowFilledEvent.class);
    private static final WindowFilledEvent instance = new WindowFilledEvent();
    private List<CamcSensorListener> camcSensorListenerList = new ArrayList<CamcSensorListener>();
    private boolean ready = false;
    private String currentLogginType = "none";
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public static WindowFilledEvent getInstance() {
        return  instance;
    }

    public void registerCamcSensorListener(CamcSensorListener camcSensorListener) {
        camcSensorListenerList.add(camcSensorListener);
    }

    public void removeCamcSensorListener(CamcSensorListener camcSensorListener) {
        camcSensorListenerList.remove(camcSensorListener);
    }

    public void reset() {
        ready = false;
        camcSensorListenerList.clear();
    }

    public void sensorEventFired() {
        // To ensure that all windows have been filled.
        if (!ready) {
            for (CamcSensorListener camcSensorListener : camcSensorListenerList) {
                if (camcSensorListener.getLastReading() == null)
                    return;
            }
            ready = true;
        }

        writeData();
    }

    public String getCurrentLogginType() {
        return currentLogginType;
    }

    public void setCurrentLogginType(String currentLogginType) {
        this.currentLogginType = currentLogginType;
    }

    private void writeData() {
        StringBuilder stringBuilder = new StringBuilder();

        for (CamcSensorListener camcSensorListener : camcSensorListenerList) {
            float[] lastReading = camcSensorListener.getLastReading();
            for (float f : lastReading) {
                stringBuilder.append(f + ", ");
            }
        }
        stringBuilder.append(currentLogginType);


        String storageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(storageState)) {
            logger.error("Failed to get storage access.");
            return;
        }

        File externalFilesDir = context.getExternalFilesDir(null);
        final File storageDirectory = new File(externalFilesDir, "listr");

        if (!storageDirectory.exists()) {
            if (!storageDirectory.mkdir()) {
                logger.error("Could not create storage dir");
                return;
            }
        }

        String filename = "camcWeka.data";
        File file = new File(storageDirectory, filename);

        Closer closer = Closer.create();

        try {
            BufferedWriter writer = closer.register(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), Charsets.UTF_8)));

            writer.write(stringBuilder.toString());
            writer.newLine();

            writer.flush();
        } catch (Exception e) {
            logger.error("Something went wrong saving data", e);
        } finally {
            try {
                closer.close();
            } catch (IOException e) {
                logger.error("Failed to close streams.", e);
            }
        }

        logger.debug("data: " + stringBuilder.toString());
    }

    private WindowFilledEvent() {}
}
