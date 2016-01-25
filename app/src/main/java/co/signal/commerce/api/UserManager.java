package co.signal.commerce.api;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.SharedPreferences;

import co.signal.serverdirect.api.Hashes;
import co.signal.serverdirect.api.SignalConfig;

/**
 * A user manager class that manages the user account and associated data.
 */
@Singleton
public class UserManager {
  public static final String PREF_EMAIL = "user_email";
  public static final String HASHED_EMAIL = "uid-hashed-email-sha256";

  private boolean loginPageViewed = false;

  @Inject
  SharedPreferences prefs;
  @Inject
  SignalConfig config;

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
    // TODO: Call server
  }

  /**
   * Called whenever the login activity is viewed and maybe the user logged in
   */
  public void loginViewed() {
    loginPageViewed = true;
  }

  /**
   * Returnes true if the last activity was the LoginActivity
   */
  public boolean wasLoginViewed() {
    boolean result = loginPageViewed;
    loginPageViewed = false;
    return result;
  }
}
