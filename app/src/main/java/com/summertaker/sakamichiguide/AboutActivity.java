package com.summertaker.sakamichiguide;

import android.os.Bundle;

import com.summertaker.sakamichiguide.common.BaseActivity;
import com.summertaker.sakamichiguide.common.Config;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        initBaseToolbar(Config.TOOLBAR_ICON_BACK, getString(R.string.app_name));

        /*Button btGoToGooglePlay = (Button) findViewById(R.id.btGoToGooglePlay);
        if (btGoToGooglePlay != null) {
            btGoToGooglePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Config.URL_GOOGLE_PLAY));
                    startActivity(intent);
                }
            });
        }*/
    }
}
