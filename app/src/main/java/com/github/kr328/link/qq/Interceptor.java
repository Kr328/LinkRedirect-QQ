package com.github.kr328.link.qq;

import android.app.Application;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Interceptor {
    private static final String STRING_USE_BROWSER_OPEN = "使用浏览器打开";
    private static final String STRING_INTERNAL_BROWSER = "内置浏览器";
    private static final String CATEGORY_IGNORE = "com.github.kr328.link.qq.IGNORE";

    private final Application application;

    @Keep
    public Interceptor(Application application) {
        this.application = application;

        Log.i("RedirectLink", "application " + this.application.getPackageName() + " injected");
    }

    @Keep
    @NonNull
    public Intent intercept(@NonNull Intent intent) {
        if (intent.getComponent() == null)
            return intent;

        if (intent.hasCategory(CATEGORY_IGNORE))
            return intent;

        Intent normalLink = interceptNormalLink(intent);
        if (normalLink != null)
            return normalLink;

        //dumpExtras(intent);

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

        Bundle bundle = application.getContentResolver().call(uri, "GET", null, null);

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

        Intent chooser = Intent.createChooser(view, STRING_USE_BROWSER_OPEN);

        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{
                new LabeledIntent(original, application.getPackageName(), STRING_INTERNAL_BROWSER, 0)
        });

        Log.i("RedirectLink", "Open " + uri);

        return chooser;
    }

    private void dumpExtras(@NonNull Intent intent) {
        Log.d("RedirectLink", "Begin dump " + intent);

        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        for (String key : extras.keySet()) {
            Log.d("RedirectLink", Dumper.dump(application.getPackageName(), intent));
        }
    }
}
