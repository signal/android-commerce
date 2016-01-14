package co.signal.commerce.module;

import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import co.signal.commerce.CategoriesActivity;
import co.signal.commerce.MainActivity;
import co.signal.commerce.SettingsActivity;
import co.signal.commerce.api.CategoryParser;
import co.signal.commerce.api.ProductParser;
import co.signal.serverdirect.api.SignalConfig;
import co.signal.serverdirect.api.SignalInc;
import co.signal.serverdirect.api.StandardField;
import co.signal.serverdirect.api.Tracker;
import dagger.Module;
import dagger.Provides;

@Module(
  library=true,
  injects = {
    MainActivity.class,
    CategoriesActivity.class,
    SettingsActivity.class
  }
)
public class ApplicationModule {

  // Named strings for injection
  public static final String NAME_SITE_ID = "SITE_ID";
  public static final String NAME_API_URL = "API_URL";

  // Preference keys
  public static final String PREF_SITE_ID = "siteid";

  private static final String BOUTIQUE_111_URL = "http://commerce.signal.ninja/api/rest/";
  private Context appContext;

  public ApplicationModule(Context appContext) {
    this.appContext = appContext;
  }

  @Provides @Singleton @Named(NAME_API_URL)
  public String provideBoutique111Url() {
    return BOUTIQUE_111_URL;
  }

  @Provides @Singleton
  public ProductParser provideProductParser() {
    return new ProductParser();
  }

  @Provides @Singleton
  public CategoryParser provideCategoryParser() {
    return new CategoryParser();
  }

  @Provides @Singleton
  public SignalConfig provideSignalConfig() {
    SignalConfig config = SignalInc.createConfig(appContext)
        .setEndpoint("https://mobile-stage.signal.ninja/mobile")
        .setDebug(true)
        .setVerbose(true)
        .setBatteryPercentage(15)
        .setPublishInBackground(true)
        .setMessageRetryCount(3)
        .setDispatchInterval(20)
        .setLifecycleEventsEnabled(true)
        .setMaxQueuedMessages(100)
        .setMessageExpiration(3600)
        .setSocketConnectTimeout(10000)
        .setSocketReadTimeout(5000)
        .setNetworkWifiOnly(false);

    config.setStandardFields(StandardField.Timezone, StandardField.ScreenOrientation,
                             StandardField.DeviceInfo);

    config.addCustomField("demo", "true"); // just to add something
    return config;
  }

  @Provides @Singleton
  public SignalInc provideSignalInc(SignalConfig config) {
    return SignalInc.getInstance(appContext, config);
  }

  @Provides @Named(NAME_SITE_ID)
  public String provideSiteId() {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    return preferences.getString("siteid", "NotSet"); // Set it SettingsActivity
  }

  /**
   * Returns the default tracker from SignalSDK. Should not be a Singleton from Dagger's
   * point of view, and should always ask SignalInc for the default tracker;
   *
   * @return A null Tracker until a proper SiteId is set, otherwise the real default Tracker
   */
  @Provides
  public Tracker provideSignalTracker(SignalInc signalInc, @Named("SITE_ID") String siteId) {
    // This is not typical of a real app. This demo app allows the config and SiteId
    // to be changed dynamically, so all the Signal objects are discretely provided.
    // Normally, all would be in this one method and provide the Tracker as a singleton.
//    if (TextUtils.isEmpty(siteId) || "NotSet".equals(siteId)) {
//      return new NullTracker();
//    }
    return signalInc.getTracker(siteId);
  }

  private class NullTracker implements Tracker {
    @Override
    public String getSiteId() { return null; }

    @Override
    public boolean isDebug() { return false; }

    @Override
    public void setDebug(boolean b) { }

    @Override
    public void publish(String s, String... strings) {
      Log.d("tracker", "NullTracker - nothing published");
    }

    @Override
    public void publish(String s, Map<String, String> map) {
      Log.d("tracker", "NullTracker - nothing published");
    }

    @Override
    public void addStandardFields(StandardField... standardFields) {
      Log.d("tracker", "NullTracker - nothing changed");
    }

    @Override
    public void addCustomField(String s, String s1) {
      Log.d("tracker", "NullTracker - nothing changed");
    }
  }
}
