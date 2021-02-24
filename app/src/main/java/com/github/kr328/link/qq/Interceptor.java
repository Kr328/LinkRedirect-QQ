package com.github.kr328.link.qq;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Interceptor extends ContextWrapper {
    private static final String CATEGORY_IGNORE = "com.github.kr328.link.qq.IGNORE";

    private final Application application;

    @Keep
    public Interceptor(Application application, Context context) {
        super(context);

        this.application = application;

        Log.i(Constants.TAG, "application " + application.getPackageName() + " injected");
    }

    @Keep
    @NonNull
    public Intent intercept(@NonNull Intent intent) {
        if (intent.getComponent() == null)
            return intent;

        Log.i(Constants.TAG, "Intercept " + application.getPackageName() + " intent: " + intent);

        if (intent.hasCategory(CATEGORY_IGNORE))
            return intent;

        Intent normalLink = interceptNormalLink(intent);
        if (normalLink != null)
            return normalLink;

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

                return createChooser(intent, uri);
            }
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
    private Intent createChooser(@NonNull Intent original, @NonNull Uri uri) {
        original.addCategory(CATEGORY_IGNORE);

        Intent view = new Intent(Intent.ACTION_VIEW);

        view.addCategory(Intent.CATEGORY_BROWSABLE);
        view.setData(uri);

        Intent chooser = Intent.createChooser(view, getString(R.string.open_link));

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{
                new LabeledIntent(original, getPackageName(), getString(R.string.internal_browser), 0)
        });

        Log.i(Constants.TAG, "Opening " + uri);

        return chooser;
    }

    private void dumpExtras(@NonNull Intent intent) {
        Log.d(Constants.TAG, Dumper.dump(getPackageName(), intent));
    }
}
