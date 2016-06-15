package co.signal.commerce.api;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.SharedPreferences;
import android.text.TextUtils;

import co.signal.commerce.module.TrackerWrapper;
import co.signal.serverdirect.api.Hashes;
import co.signal.serverdirect.api.SignalConfig;
import co.signal.serverdirect.api.Tracker;

import static co.signal.commerce.module.Tracking.LOGIN;
import static co.signal.commerce.module.Tracking.USER;
//import co.signal.serverdirect.api.SignalProfileStore;

/**
 * A user manager class that manages the user account and associated data.
 */
@Singleton
public class UserManager {
  public static final String PREF_EMAIL = "user_email";
  public static final String HASHED_EMAIL = "uid-hashed-email-sha256";

  @Inject
  TrackerWrapper trackerWrapper;
  @Inject
  Tracker tracker;
  @Inject
  SharedPreferences prefs;
  @Inject
  SignalConfig config;
//  @Inject
//  SignalProfileStore profileStore;

  public boolean userLogin(String email, String password) {
    // A real app would probably encrypt this value before persisting it
    prefs.edit().putString(PREF_EMAIL, email).apply();
    config.addCustomField(HASHED_EMAIL, Hashes.sha256(email));

    trackerWrapper.trackEvent(USER, LOGIN); // standard tracking
    tracker.publish("action:login"); // separate event for profile sync

    // TODO: Call server
    return true;
  }

  public boolean isLoggedIn() {
    return prefs.contains(PREF_EMAIL);
  }

  public String getEmail() {
    return prefs.getString(PREF_EMAIL, null);
  }

  public void userLogout() {
    prefs.edit().remove(PREF_EMAIL).apply();
    config.removeCustomField(HASHED_EMAIL);
    clear();

    tracker.publish("action:logout");

    // TODO: Call server
  }

  public void clear() {
//    profileStore.clear();
  }

  public static boolean isValidEmail(String target) {
    if (TextUtils.isEmpty(target)) {
      return false;
    }
    //android Regex to check the email address Validation
    return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
  }

  public boolean isPreferred() {
    if (!isLoggedIn()) {
      return false;
    }
//    return "true".equals(profileStore.getData("Preferred"));
    return false;
  }
}

