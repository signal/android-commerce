package co.signal.commerce;

import javax.inject.Inject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
}
