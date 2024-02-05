package com.androvine.chatrecovery.utils;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.androvine.chatrecovery.R;

import java.io.File;
import java.util.Objects;


public class ChatRecoveryMediaObService extends Service {

    private static final int SDK_INT = Build.VERSION.SDK_INT;
    private final String TAG = "ChatRecoveryMediaObService";
    private FileObserver[] mFileObservers; // For versions below Android 11
    private ContentObserver mContentObserver; // For Android 11 and above
    private Uri yourSAFUri; // URI for SAF

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        yourSAFUri = PermSAFUtils.Companion.getSAFUri(this);


        Log.e(TAG, "onStartCommand:  service started");

        if (SDK_INT < Build.VERSION_CODES.R) {
            String[] paths = {"WhatsApp/Media/WhatsApp Images", "WhatsApp/Media/WhatsApp Video"};

            Log.e(TAG, "onStartCommand:  method called for below R");

            mFileObservers = new FileObserver[paths.length];
            for (int i = 0; i < paths.length; i++) {
                final String path = paths[i];
                mFileObservers[i] = new FileObserver(new File(Environment.getExternalStorageDirectory(), path).toString()) {
                    @Override
                    public void onEvent(int event, String pathInside) {
                        if (event == 2 || event == 128) {
                            Log.e(TAG, "Event type: " + event + " on path: " + pathInside);
                            handleEventBelowR(path, pathInside);
                        }
                    }
                };
                mFileObservers[i].startWatching();
            }
        } else {
            Uri contentUri = MediaStore.Files.getContentUri("external");
            mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    handleEventRAndAbove(uri);
                }
            };
            getContentResolver().registerContentObserver(contentUri, true, mContentObserver);
        }
        return START_STICKY;
    }


    private void handleEventBelowR(String directoryPath, String filePath) {
        Log.e(TAG, "handleEventBelowR: directoryPath: " + directoryPath + " filePath: " + filePath);

        try {
            File sourceFile = new File(Environment.getExternalStorageDirectory() + "/" + directoryPath, filePath);
            String fullFilePath = sourceFile.getPath();
            if (fullFilePath.contains("WhatsApp Images")) {
                MFileUtils.copyFileOrDirectory(fullFilePath, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + getResources().getString(R.string.app_name) + "/Images");
            } else if (fullFilePath.contains("WhatsApp Video")) {
                MFileUtils.copyFileOrDirectory(fullFilePath, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + getResources().getString(R.string.app_name) + "/Videos");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void handleEventRAndAbove(Uri uri) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            DocumentFile sourceFile = DocumentFile.fromTreeUri(this, yourSAFUri);
            if (Objects.requireNonNull(uri.getPath()).contains("WhatsApp Images")) {
                if (sourceFile != null) {
                    MFileUtils.copyFileOrDirectory(this, Objects.requireNonNull(sourceFile.findFile("WhatsApp Images")).getUri(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + getResources().getString(R.string.app_name) + "/Images");
                }
            } else if (uri.getPath().contains("WhatsApp Video")) {
                if (sourceFile != null) {
                    MFileUtils.copyFileOrDirectory(this, Objects.requireNonNull(sourceFile.findFile("WhatsApp Video")).getUri(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + getResources().getString(R.string.app_name) + "/Videos");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if (SDK_INT < Build.VERSION_CODES.R && mFileObservers != null) {
            for (FileObserver observer : mFileObservers) {
                observer.stopWatching();
            }
        } else {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
