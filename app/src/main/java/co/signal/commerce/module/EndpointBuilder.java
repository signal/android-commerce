package co.signal.commerce.module;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.SharedPreferences;

import static co.signal.commerce.module.ApplicationModule.ENV_PROD;

/**
 * Constructs the Signal mobile endpoint from preference values
 */
@Singleton
public class EndpointBuilder {
  public static final String PROD_URL = "s.thebrighttag.com/mobile";
  public static final String STAGE_URL = "mobile-stage.signal.ninja/mobile";

  @Inject
  SharedPreferences preferences;

  public String build() {
    return getProtocol() + "://" + getUrl();
  }

  public String buildFromProtocol(String protocol) {
    return protocol.toLowerCase() + "://" + getUrl();
  }

  public String buildFromEnvironment(String environment) {
    return getProtocol() + "://" + (environment.equals(ENV_PROD) ? PROD_URL : STAGE_URL);
  }

  private String getProtocol() {
    return preferences.getString("protocol", "https").toLowerCase();
  }

  private String getUrl() {
    return ENV_PROD.equals(preferences.getString("environment", ENV_PROD))
        ? PROD_URL
        : STAGE_URL;
  }
}
