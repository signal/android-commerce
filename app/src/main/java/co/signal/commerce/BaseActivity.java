package co.signal.commerce;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Base class for all Activity instances
 */
public class BaseActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((CommerceApplication) getApplication()).inject(this);
  }
}
