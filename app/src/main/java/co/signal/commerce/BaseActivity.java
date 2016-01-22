package co.signal.commerce;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import co.signal.serverdirect.api.Tracker;

/**
 * Base class for all Activity instances to initiate dependency injection
 */
public class BaseActivity extends AppCompatActivity {
  @Inject
  Tracker tracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    ((CommerceApplication)getApplication()).inject(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    String name = this.getClass().getSimpleName();
    tracker.publish("view:" + name);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
      startActivity(new Intent(this, SettingsActivity.class));
      tracker.publish("click:menu_settings");
      return true;
    } else if (id == R.id.action_login) {
      startActivity(new Intent(this, LoginActivity.class));
      tracker.publish("click:menu_login");
      return true;
    } else if (id == android.R.id.home) {
      onBackPressed();
    }

    return super.onOptionsItemSelected(item);
  }
}
