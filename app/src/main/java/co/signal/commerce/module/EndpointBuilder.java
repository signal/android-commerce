package co.signal.commerce.module;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.SharedPreferences;

import static co.signal.commerce.module.ApplicationModule.ENV_PROD;
import static co.signal.commerce.module.ApplicationModule.ENV_STAGE;
import static co.signal.commerce.module.ApplicationModule.ENV_VAGRANT;

/**
 * Constructs the Signal mobile endpoint from preference values
 */
@Singleton
public class EndpointBuilder {
  public static final String PROD_URL = "s.thebrighttag.com/mobile";
  public static final String STAGE_URL = "mobile-stage.signal.ninja/mobile";
//  public static final String STAGE_URL = "tagserve.stage.thebrighttag.com/mobile";
  public static final String DEV_URL = "tagserve.dv2.thebrighttag.com/mobile";
  public static final String VAGRANT_URL = "10.200.2.6:8080/mobile";

  @Inject
  SharedPreferences preferences;

  public String build() {
    return getProtocol() + "://" + getUrl();
  }

  public String buildFromProtocol(String protocol) {
    return protocol.toLowerCase() + "://" + getUrl();
  }

  public String buildFromEnvironment(String environment) {
    String url;
    if (ENV_PROD.equals(environment)) {
      url = PROD_URL;
    } else if (ENV_STAGE.equals(environment)) {
      url = STAGE_URL;
    } else if (ENV_VAGRANT.equals(environment)) {
      url = VAGRANT_URL;
    } else {
      url = DEV_URL;
    }
    return getProtocol() + "://" + url;
  }

  private String getProtocol() {
    return preferences.getString("protocol", "https").toLowerCase();
  }

  private String getUrl() {
    String curEnv = preferences.getString("environment", ENV_PROD);
    if (ENV_PROD.equals(curEnv)) {
      return PROD_URL;
    } else if (ENV_STAGE.equals(curEnv)) {
      return STAGE_URL;
    } else if (ENV_VAGRANT.equals(curEnv)) {
      return VAGRANT_URL;
    }
    return DEV_URL;
  }
}
