package com.deluxedevelopment.whatsonmywatchlist.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ImageUtils {
    private static final String CACHE_DIRECTORY = "artwork_cache";

    public static String saveImageToCache(Context context, Uri imageUri) throws IOException {
        File cacheDir = new File(context.getCacheDir(), CACHE_DIRECTORY);
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        String fileName = UUID.randomUUID().toString() + ".jpg";
        File destFile = new File(cacheDir, fileName);

        try (InputStream in = context.getContentResolver().openInputStream(imageUri);
             OutputStream out = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        }

        return destFile.getAbsolutePath();
    }

    public static void clearUnusedCache(Context context, java.util.Set<String> activePaths) {
        File cacheDir = new File(context.getCacheDir(), CACHE_DIRECTORY);
        if (cacheDir.exists()) {
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!activePaths.contains(file.getAbsolutePath())) {
                        file.delete();
                    }
                }
            }
        }
    }
}
