package co.signal.commerce;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    setupPage();
  }

  private void setupPage() {
    Button startBtn = (Button)findViewById(R.id.btn_start_shopping);
    startBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(v.getContext(), CategoriesActivity.class));
      }
    });

    ImageView img = (ImageView)findViewById(R.id.img_logo);
    img.setOnClickListener(new WebViewOnClickListener("http://commerce.signal.ninja"));

    img = (ImageView)findViewById(R.id.img_signal);
    img.setOnClickListener(new WebViewOnClickListener("http://www.signal.co"));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      startActivity(new Intent(this, SettingsActivity.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
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
      } catch (ActivityNotFoundException e) {
        Toast.makeText(MainActivity.this,
            "No application can handle this request. Please install a web browser",
            Toast.LENGTH_LONG)
            .show();
      }
    }
  }
}
