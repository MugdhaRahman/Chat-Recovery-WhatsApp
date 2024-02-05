package com.androvine.chatrecovery.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.util.Log;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Objects;

public class MFileUtils {

    public static void copyFileOrDirectory(String str, String str2) {
        try {
            File file = new File(str);
            File file2 = new File(str2, file.getName());
            if (file.isDirectory()) {
                for (String file3 : file.list()) {
                    copyFileOrDirectory(new File(file, file3).getPath(), file2.getPath());
                }
                return;
            }
            copyFile(file, file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(File file, File file2) throws IOException {
        FileChannel fileChannel;
        if (!file2.getParentFile().exists()) {
            file2.getParentFile().mkdirs();
        }
        if (!file2.exists()) {
            file2.createNewFile();
        }
        FileChannel fileChannel2 = null;
        try {
            FileChannel channel = new FileInputStream(file).getChannel();
            try {
                fileChannel2 = new FileOutputStream(file2).getChannel();
                fileChannel2.transferFrom(channel, 0, channel.size());
                Log.d("mediaSaved", "media saved");
                if (channel != null) {
                    channel.close();
                }
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
            } catch (Throwable th) {
                FileChannel fileChannel3 = fileChannel2;
                fileChannel2 = channel;
                fileChannel = fileChannel3;
                if (fileChannel2 != null) {
                    fileChannel2.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            fileChannel = null;
            if (fileChannel2 != null) {
            }
            if (fileChannel != null) {
            }
            throw th2;
        }
    }


    // Method for Android R and above using SAF
    public static void copyFileOrDirectory(Context context, Uri srcUri, String destPath) {
        DocumentFile srcFile = DocumentFile.fromSingleUri(context, srcUri);
        if (srcFile == null) return;

        if (srcFile.isDirectory()) {
            for (DocumentFile childFile : srcFile.listFiles()) {
                copyFileOrDirectory(context, childFile.getUri(), destPath + File.separator + childFile.getName());
            }
        } else {
            DocumentFile destParent = DocumentFile.fromFile(new File(destPath));
            if (!destParent.exists() && destParent.isDirectory()) {
                String[] parts = destPath.split(File.separator);
                DocumentFile grandParent = DocumentFile.fromFile(new File(destPath.substring(0, destPath.lastIndexOf(File.separator))));
                destParent = grandParent.createDirectory(parts[parts.length - 1]);
            }

            if (destParent == null) return;

            DocumentFile destFile = destParent.createFile(Objects.requireNonNull(srcFile.getType()), Objects.requireNonNull(srcFile.getName()));
            if (destFile != null) {
                copyFile(context, srcFile.getUri(), destFile.getUri());
            }

        }
    }

    public static void copyFile(Context context, Uri srcUri, Uri destUri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(srcUri);
            OutputStream os = context.getContentResolver().openOutputStream(destUri);

            if (is == null || os == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                FileUtils.copy(is, os); // Using Android's FileUtils to copy the streams
            } else {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            is.close();
            os.close();
            Log.d("mediaSaved", "media saved via SAF");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
