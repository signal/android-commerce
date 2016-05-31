package co.signal.commerce;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import co.signal.commerce.api.UserManager;
import co.signal.commerce.model.Cart;
import co.signal.commerce.module.TrackerWrapper;
import co.signal.serverdirect.api.Tracker;

import static co.signal.commerce.module.Tracking.*;

/**
 * Base class for all Activity instances to initiate dependency injection
 */
public class BaseActivity extends AppCompatActivity {
  @Inject
  Tracker tracker;
  @Inject
  TrackerWrapper trackerWrapper;
  @Inject
  UserManager userManager;
  @Inject
  Cart cart;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    ((CommerceApplication)getApplication()).inject(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    String name = this.getClass().getSimpleName();
    tracker.publish(TRACK_VIEW, VIEW_NAME, name);

    // The menu might have changed due to login or cart changes
    invalidateOptionsMenu();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);

    if (userManager.isLoggedIn()) {
      menu.findItem(R.id.action_login).setVisible(false);
    } else {
      menu.findItem(R.id.action_logout).setVisible(false);
    }

    menu.findItem(R.id.action_cart)
        .setIcon(cart.isEmpty() ? R.drawable.ic_shopping_cart_black_24dp
                                : R.drawable.ic_shopping_cart_white_24dp)
        .setVisible(this.getClass() != CheckoutActivity.class);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_cart) {
      if (cart.isEmpty()) {
        Toast.makeText(this, "Your shopping cart is empty... go buy something!", Toast.LENGTH_LONG)
            .show();
      } else {
        startActivity(new Intent(this, CheckoutActivity.class));
        tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, MENU, LABEL, "checkout");
      }
      return true;
    } else if (id == R.id.action_settings) {
      startActivity(new Intent(this, SettingsActivity.class));
      tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, MENU, LABEL, "settings");
      return true;
    } else if (id == R.id.action_login) {
      startActivity(new Intent(this, LoginActivity.class));
      tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, MENU, LABEL, "login");
      return true;
    } else if (id == R.id.action_logout) {
      tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, MENU, LABEL, "logout");
      // Call publish above before UserManager call so logout has the hashed email
      userManager.userLogout();
      invalidateOptionsMenu();
      return true;
    } else if (id == R.id.action_profile_load) {
      // View needs work before we can display the data
//      startActivity(new Intent(this, ProfileDataActivity.class));

      // First event to retrieve the profile data, second event for analytics
      // Too tricky on the server side to configure both with single event
      tracker.publish("profile:load");
      tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, MENU, LABEL, "profileLoad");
      return true;
    } else if (id == R.id.action_profile_clear) {
      tracker.publish(TRACK_EVENT, CATEGORY, CLICK, ACTION, MENU, LABEL, "profileClear");
      userManager.clear();
      return true;
    } else if (id == android.R.id.home) {
      onBackPressed();
    }

    return super.onOptionsItemSelected(item);
  }

  public Tracker getTracker() {
    return tracker;
  }

  public UserManager getUserManager() {
    return userManager;
  }

  public Cart getCart() {
    return cart;
  }
}
