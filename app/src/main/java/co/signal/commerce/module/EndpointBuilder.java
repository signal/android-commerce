package co.signal.commerce.module;

import android.content.SharedPreferences;

/**
 * Constructs the Signal mobile endpoint from preference values
 */
public class EndpointBuilder {
  public static final String PROD_ENV = "Production";
  public static final String PROD_URL = "s.thebrighttag.com/mobile";
  public static final String STAGE_URL = "mobile-stage.signal.ninja/mobile";

  private SharedPreferences preferences;

  public EndpointBuilder(SharedPreferences preferences) {
    this.preferences = preferences;
  }

  public String build() {
    return getProtocol() + "://" + getUrl();
  }

  public String buildFromProtocol(String protocol) {
    return protocol.toLowerCase() + "://" + getUrl();
  }

  public String buildFromEnvironment(String environment) {
    return getProtocol() + "://" + (environment.equals(PROD_ENV) ? PROD_URL : STAGE_URL);
  }

  private String getProtocol() {
    return preferences.getString("protocol", "https").toLowerCase();
  }

  private String getUrl() {
    return PROD_ENV.equals(preferences.getString("environment", PROD_ENV))
        ? PROD_URL
        : STAGE_URL;
  }
}
