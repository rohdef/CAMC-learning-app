package dk.au.cs.listr.camclearner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by rohdef on 12/12/13.
 */
public class DataHelper implements View.OnClickListener {
    private final Logger logger = LoggerFactory.getLogger(DataHelper.class);
    private Context context;
    private static int counter = 0;

    public void setContext(Context context) {
        this.context = context;
    }

    private void deleteFile() {
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

        File[] files = storageDirectory.listFiles();
        for (File file : files) {
            if (file.exists()) file.delete();
        }
        counter = 0;
    }

    public void rename() {
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
        File newName = new File(storageDirectory, filename + counter);
        if (file.exists()) file.renameTo(newName);
        counter++;
    }

    private void sendFile() {
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

        File[] files = storageDirectory.listFiles();
        ArrayList<Uri> uris = new ArrayList<Uri>();
        for (File f : files) {
            uris.add(Uri.fromFile(f));
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(emailIntent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendFileButton) {
            sendFile();
        } else if (view.getId() == R.id.deleteFileButton) {
            deleteFile();
        } else {
            logger.error("Unknown button ID recieved");
        }
    }
}
