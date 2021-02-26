package com.github.kr328.link.qq;

import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.FileProvider;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.mobileqq.filemanager.data.ForwardFileInfo;

import java.io.File;

public class Interceptor extends ContextWrapper {
    private static final String CATEGORY_IGNORE = "com.github.kr328.link.qq.IGNORE";

    private static final ComponentName FILE_VIEWER_COMPONENT = ComponentName
            .unflattenFromString("com.tencent.tim/com.tencent.mobileqq.filemanager.fileviewer.TroopFileDetailBrowserActivity");

    @Keep
    public Interceptor(Context context) {
        super(context);

        Log.i(Constants.TAG, "application " + getApplicationContext().getPackageName() + " injected");
    }

    @Keep
    @NonNull
    public Intent intercept(@NonNull Intent intent) {
        if (intent.getComponent() == null)
            return intent;

        Log.i(Constants.TAG, "Intercept " + getApplicationContext().getPackageName() + " intent: " + intent);

        intent.setExtrasClassLoader(Interceptor.class.getClassLoader());

        if (intent.hasCategory(CATEGORY_IGNORE))
            return intent;

        Intent normalLink = interceptNormalLink(intent);
        if (normalLink != null)
            return normalLink;

        Intent fileViewer = interceptFileViewer(intent);
        if (fileViewer != null)
            return fileViewer;

        if (BuildConfig.DEBUG) {
            dumpExtras(intent);
        }

        return intent;
    }

    @Nullable
    private Intent interceptNormalLink(@NonNull Intent intent) {
        String url = intent.getStringExtra("url");

        if (url != null) {
            Uri uri = Uri.parse(url);

            if ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) {
                Bundle options = getOptions();

                if (!options.getBoolean("normal_link", true))
                    return null;

                return createChooser(intent, uri, null);
            }
        }

        return null;
    }

    @Nullable
    private Intent interceptFileViewer(@NonNull Intent intent) {
        if (FILE_VIEWER_COMPONENT.equals(intent.getComponent())) {
            ForwardFileInfo fileInfo = intent.getParcelableExtra("fileinfo");

            if (fileInfo == null || fileInfo.getLocalPath() == null) {
                return null;
            }

            Bundle options = getOptions();

            if (!options.getBoolean("file_open", true)) {
                return null;
            }

            File file = new File(fileInfo.getLocalPath());

            if (!file.isFile() || file.length() != fileInfo.getFileSize()) {
                return null;
            }

            Uri uri = FileProvider.getUriForFile(
                    getApplicationContext(),
                    getApplicationContext().getPackageName() + ".fileprovider",
                    file
            );

            String type = getContentResolver().getType(uri);

            if (type == null) {
                type = "application/octet-stream";
            }

            Log.i(Constants.TAG, "File: name = " + fileInfo.getFileName() +
                    " path = " + fileInfo.getLocalPath() +
                    " size = " + fileInfo.getFileSize() +
                    " mimeType = " + type);

            return createChooser(intent, uri, type);
        }

        return null;
    }

    @NonNull
    private Bundle getOptions() {
        Uri uri = new Uri.Builder()
                .scheme("content")
                .authority(BuildConfig.APPLICATION_ID + ".options")
                .build();

        Bundle bundle = getContentResolver().call(uri, "GET", null, null);

        if (bundle == null) {
            bundle = new Bundle();
        }

        return bundle;
    }

    @NonNull
    private Intent createChooser(@NonNull Intent original, @NonNull Uri uri, @Nullable String mimeType) {
        original.addCategory(CATEGORY_IGNORE);

        Intent view = new Intent(Intent.ACTION_VIEW);

        if (mimeType == null)
            view.addCategory(Intent.CATEGORY_BROWSABLE);
        else
            view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        view.setDataAndType(uri, mimeType);

        Intent chooser = Intent.createChooser(view, null);

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{
                new LabeledIntent(original, getPackageName(), getString(R.string.internal_browser), 0)
        });

        Log.i(Constants.TAG, "Redirect: " + uri);

        return chooser;
    }

    private void dumpExtras(@NonNull Intent intent) {
        Log.d(Constants.TAG, Dumper.dump(getPackageName(), intent));
    }
}
