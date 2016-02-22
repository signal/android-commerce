package co.signal.commerce.module;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.common.collect.ImmutableList;

import co.signal.commerce.CategoriesActivity;
import co.signal.commerce.CheckoutActivity;
import co.signal.commerce.LoginActivity;
import co.signal.commerce.MainActivity;
import co.signal.commerce.ProductDetailsActivity;
import co.signal.commerce.ProductsActivity;
import co.signal.commerce.SettingsActivity;
import co.signal.commerce.api.CategoryParser;
import co.signal.commerce.api.ProductImageUrlParser;
import co.signal.commerce.api.UserManager;
import co.signal.commerce.model.Cart;
import co.signal.serverdirect.api.Hashes;
import co.signal.serverdirect.api.SignalConfig;
import co.signal.serverdirect.api.SignalInc;
import co.signal.serverdirect.api.StandardField;
import co.signal.serverdirect.api.Tracker;
import co.signal.util.SignalLogger;
import dagger.Module;
import dagger.Provides;

@Module(
  library=true,
  injects = {
    MainActivity.class,
    CategoriesActivity.class,
    ProductsActivity.class,
    ProductDetailsActivity.class,
    LoginActivity.class,
    CheckoutActivity.class,
    UserManager.class,
    EndpointBuilder.class,
    SettingsActivity.class,
    SettingsActivity.GeneralPreferenceFragment.class,
    SettingsActivity.ControlsPreferenceFragment.class,
    SettingsActivity.StandardFieldPreferenceFragment.class,
    SettingsActivity.LoggingPreferenceFragment.class
  }
)
public class ApplicationModule {
  // Named strings for injection
  public static final String NAME_SITE_ID = "SITE_ID";
  public static final String NAME_ENVIRONMENT = "ENV";
  public static final String NAME_API_URL = "API_URL";
  public static final String NAME_THUMB_URL = "THUMB_URL";

  // Preference keys
  public static final String PREF_SITE_ID = "siteid";
  public static final String PREF_ENVIRONMENT = "environment";
  // Preference Values
  public static final String ENV_PROD = "Production";
  public static final String ENV_STAGE = "Staging";

  // Static URLs
  private static final String BOUTIQUE_111_URL = "http://commerce.signal.ninja/api/rest/";
  private static final String RE_THUMB_URL = "http://api.rethumb.com/v1/square/";

  private Context appContext;
  private SharedPreferences preferences;

  public ApplicationModule(Context appContext) {
    this.appContext = appContext;
    preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
  }

  @Provides @Singleton @Named(NAME_API_URL)
  public String provideBoutique111Url() {
    return BOUTIQUE_111_URL;
  }

  @Provides @Singleton @Named(NAME_THUMB_URL)
  public String provideThumbnailUrl() {
    return RE_THUMB_URL;
  }

  @Provides @Named(NAME_SITE_ID)
  public String provideSiteId() {
    return preferences.getString(PREF_SITE_ID, "NotSet"); // Set in SettingsActivity
  }

  @Provides @Named(NAME_ENVIRONMENT)
  public String provideEnvironment() {
    return preferences.getString(PREF_ENVIRONMENT, ENV_PROD); // Set in SettingsActivity
  }

  @Provides @Singleton
  public SharedPreferences provideSharedPreferences() {
    return preferences;
  }

  @Provides @Singleton
  public SignalConfig provideSignalConfig(@Named("SITE_ID") String siteId, EndpointBuilder endpointBuilder) {
    SignalConfig config = SignalInc.createConfig(appContext)
        .setEndpoint(endpointBuilder.build())
        .setMaxQueuedMessages(getPrefInt("msg_max_queued", 1000))
        .setMessageExpiration(getPrefLong("msg_expiration", 86400))
        .setMessageRetryCount(getPrefInt("msg_retry_count", 3))
        .setDispatchInterval(getPrefLong("dispatch_interval", 30))
        .setBatteryPercentage(getPrefInt("battery_percentage", 20))
        .setSocketConnectTimeout(getPrefLong("socket_connect_to", 10000))
        .setSocketReadTimeout(getPrefLong("socket_read_to", 5000))
        .setPublishInBackground(preferences.getBoolean("enable_background", false))
        .setNetworkWifiOnly(preferences.getBoolean("enable_wifi", false))
        .setLifecycleEventsEnabled(preferences.getBoolean("enable_lifecycle", true))
        .setDebug(preferences.getBoolean("debug_enabled", true))
        .setVerbose(preferences.getBoolean("verbose_enabled", false));

    // Don't send lifecycle events until a proper siteId is set, causes issues with the listener
    if (TextUtils.isEmpty(siteId) || "notset".equals(siteId.toLowerCase())) {
      config.setLifecycleEventsEnabled(false);
    }

    // Set the standard fields
    ImmutableList.Builder<StandardField> builder = ImmutableList.builder();
    for (StandardField stdFld : StandardField.values()) {
      String key = "pref_" + stdFld.getName();
      if (preferences.getBoolean(key, false)) {
        builder.add(stdFld);
      }
    }
    config.setStandardFields(builder.build());

    // Add something for a custom field, email will be added after a login
    config.addCustomField("demo", "true");
    // Add the hashed email if the user is logged in
    if (preferences.contains(UserManager.PREF_EMAIL)) {
      config.addCustomField(UserManager.HASHED_EMAIL,
          Hashes.sha256(preferences.getString(UserManager.PREF_EMAIL, "")));
    }

    return config;
  }

  @Provides @Singleton
  public SignalInc provideSignalInc(SignalConfig config) {
    return SignalInc.getInstance(appContext, config);
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
    // Normally, all setup would be in this one method and provide the Tracker as a singleton.
    if (TextUtils.isEmpty(siteId) || "notset".equals(siteId.toLowerCase())) {
      return NULL_TRACKER;
    }
    return signalInc.getTracker(siteId);
  }

  private long getPrefLong(String key, long defaultValue) {
    // Stored as a string via the settings pages, so need to convert back and forth
    return Long.parseLong(preferences.getString(key, String.valueOf(defaultValue)));
  }

  private int getPrefInt(String key, int defaultValue) {
    // Stored as a string via the settings pages, so need to convert back and forth
    return Integer.parseInt(preferences.getString(key, String.valueOf(defaultValue)));
  }

  @Provides @Singleton
  public CategoryParser provideCategoryParser() {
    // Need to create manually since there is nothing injected in this class
    return new CategoryParser();
  }

  @Provides @Singleton
  public ProductImageUrlParser provideImageUrlParser() {
    // Need to create manually since there is nothing injected in this class
    return new ProductImageUrlParser();
  }

  @Provides @Singleton
  public Cart provideShoppingCart() {
    return new Cart();
  }

  /**
   * Use a null tracker until the SiteId is set.
   */
  private static final Tracker NULL_TRACKER = new Tracker() {
    @Override
    public String getSiteId() { return null; }

    @Override
    public boolean isDebug() { return false; }

    @Override
    public void setDebug(boolean b) { }

    @Override
    public void publish(String event, String... values) {
      SignalLogger.df("tracker", "NullTracker | %s | %s", event, Arrays.toString(values));
    }

    @Override
    public void publish(String event, Map<String, String> values) {
      SignalLogger.df("tracker", "NullTracker | %s | %s ", event, values);
    }

    @Override
    public void addStandardFields(StandardField... standardFields) {
      SignalLogger.df("tracker", "NullTracker | stdflds: %s", Arrays.toString(standardFields));
    }

    @Override
    public void addCustomField(String key, String value) {
      SignalLogger.df("tracker", "NullTracker | custom: %s -> %s", key, value);
    }
  };
}
