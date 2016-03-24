package co.signal.commerce.api;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.SharedPreferences;
import android.text.TextUtils;

import co.signal.serverdirect.api.Hashes;
import co.signal.serverdirect.api.SignalConfig;
import co.signal.serverdirect.api.SignalProfileStore;

/**
 * A user manager class that manages the user account and associated data.
 */
@Singleton
public class UserManager {
  public static final String PREF_EMAIL = "user_email";
  public static final String HASHED_EMAIL = "uid-hashed-email-sha256";

  @Inject
  SharedPreferences prefs;
  @Inject
  SignalConfig config;
  @Inject
  SignalProfileStore profileStore;

  public boolean userLogin(String email, String password) {
    // A real app would probably encrypt this value before persisting it
    prefs.edit().putString(PREF_EMAIL, email).apply();
    config.addCustomField(HASHED_EMAIL, Hashes.sha256(email));
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
    profileStore.clear();
    // TODO: Call server
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
    return "true".equals(profileStore.getData("Preferred"));
  }
}

