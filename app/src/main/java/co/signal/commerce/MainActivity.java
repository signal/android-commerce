package co.signal.commerce;

import javax.inject.Inject;
import javax.inject.Named;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.signal.commerce.module.ApplicationModule;
import co.signal.serverdirect.api.StandardField;

import static co.signal.commerce.module.ApplicationModule.ENV_STAGE;
import static co.signal.commerce.module.Tracking.*;

public class MainActivity extends BaseActivity {

  @Inject @Named(ApplicationModule.NAME_ENVIRONMENT)
  String environment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    String ver = "v" + StandardField.ApplicationVersion.getValue(this);
    ((TextView)findViewById(R.id.label_version)).setText(ver);

    setupPage();
  }

  private void setupPage() {
    Button startBtn = (Button)findViewById(R.id.btn_start_shopping);
    startBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(v.getContext(), CategoriesActivity.class));
        tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, "start");
      }
    });

    ImageView img = (ImageView)findViewById(R.id.img_logo);
    img.setOnClickListener(new WebViewOnClickListener(augmentUrl("http://commerce.signal.ninja")));

    img = (ImageView)findViewById(R.id.img_signal);
    img.setOnClickListener(new WebViewOnClickListener("http://www.signal.co"));
  }

  private class WebViewOnClickListener implements View.OnClickListener {
    String url;

    public WebViewOnClickListener(String url) {
      this.url = url;
    }

    @Override
    public void onClick(View v) {
      try {
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(myIntent);
        tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, "web", LABEL, "url", VALUE, url);
      } catch (ActivityNotFoundException e) {
        Toast.makeText(MainActivity.this,
            "No application can handle this request. Please install a web browser",
            Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  private String augmentUrl(String url) {
    String newUrl = url + "?siteid=" + tracker.getSiteId();
    if (ENV_STAGE.equals(environment)) {
      newUrl = newUrl + "&staging=true";
    }
    return newUrl;
  }
}
