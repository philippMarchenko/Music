package com.devphill.music.ui.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.devphill.music.BuildConfig;
import com.devphill.music.JockeyApplication;
import com.devphill.music.R;
import com.devphill.music.data.store.ThemeStore;
import com.devphill.music.ui.BaseActivity;

import javax.inject.Inject;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Inject ThemeStore mThemeStore;

    public static Intent newIntent(Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        JockeyApplication.getComponent(this).inject(this);

        ((TextView) findViewById(R.id.aboutVersion)).setText(BuildConfig.VERSION_NAME);
        ((ImageView) findViewById(R.id.aboutAppIcon)).setImageDrawable(mThemeStore.getLargeAppIcon());
        findViewById(R.id.aboutdevphillLogo).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse("http://devphill.github.io/Jockey/"));
        startActivity(webIntent);
    }
}
