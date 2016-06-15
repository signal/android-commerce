package co.signal.commerce.module;

import javax.inject.Named;
import javax.inject.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.common.collect.ImmutableList;

import co.signal.commerce.CategoriesActivity;
import co.signal.commerce.CheckoutActivity;
import co.signal.commerce.CommerceApplication;
import co.signal.commerce.LoginActivity;
import co.signal.commerce.MainActivity;
import co.signal.commerce.ProductDetailsActivity;
import co.signal.commerce.ProductsActivity;
import co.signal.commerce.ProfileDataActivity;
import co.signal.commerce.R;
import co.signal.commerce.SettingsActivity;
import co.signal.commerce.api.CategoryParser;
import co.signal.commerce.api.ProductImageUrlParser;
import co.signal.commerce.api.UserManager;
import co.signal.commerce.db.DBHelper;
import co.signal.commerce.db.DBManager;
import co.signal.commerce.model.Cart;
import co.signal.serverdirect.api.Hashes;
import co.signal.serverdirect.api.SignalConfig;
import co.signal.serverdirect.api.SignalInc;
import co.signal.serverdirect.api.SignalProfileStore;
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
    ProfileDataActivity.class,
    UserManager.class,
    EndpointBuilder.class,
    DBManager.class,
    SettingsActivity.class,
    SettingsActivity.GeneralPreferenceFragment.class,
    SettingsActivity.ControlsPreferenceFragment.class,
    SettingsActivity.StandardFieldPreferenceFragment.class,
    SettingsActivity.LoggingPreferenceFragment.class,
    CommerceApplication.TestInject.class
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
  public static final String PREF_GA_ENABLED = "enable_ga";
  public static final String PREF_PREFIX = "pref_";

  // Preference Values
  public static final String ENV_PROD = "Production";
  public static final String ENV_STAGE = "Staging";
  public static final String ENV_DEV = "Dev";
  public static final String ENV_VAGRANT = "Vagrant";

  // Static URLs
  private static final String BOUTIQUE_111_URL = "http://commerce.signal.ninja/api/rest/";
  private static final String RE_THUMB_URL = "http://api.rethumb.com/v1/square/";

  private static final Tracker NULL_TRACKER = new NullTracker();
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
    return preferences.getString(PREF_SITE_ID, "KzzOeke"); // Set in SettingsActivity
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
  public DBHelper provideDBHelper() {
    return new DBHelper(appContext);
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
        .setProfileDataEnabled(preferences.getBoolean("enable_profile", false))
        .setPublishInBackground(preferences.getBoolean("enable_background", false))
        .setNetworkWifiOnly(preferences.getBoolean("enable_wifi", false))
        .setLifecycleEventsEnabled(preferences.getBoolean("enable_lifecycle", true))
        .setDebug(preferences.getBoolean("debug_enabled", true))
        .setVerbose(preferences.getBoolean("verbose_enabled", false))
        .setMemoryDbFallbackEnabled(true)
        .setAndroidIdEnabled(false)
        .setAdIdEnabled(false);

    // Don't send lifecycle events until a proper siteId is set, causes issues with the listener
    if (TextUtils.isEmpty(siteId) || "notset".equals(siteId.toLowerCase())) {
      config.setLifecycleEventsEnabled(false);
    }

    // Set the standard fields
    if (preferences.getBoolean("first_run", true)) {
      preferences.edit().putBoolean("first_run", false)
          .putBoolean(PREF_PREFIX + StandardField.ApplicationVersion.getName(), true)
          .putBoolean(PREF_PREFIX + StandardField.OsVersion.getName(), true)
          .putBoolean(PREF_PREFIX + StandardField.DeviceId.getName(), true)
          .putBoolean(PREF_PREFIX + StandardField.DeviceIdType.getName(), true)
          .putBoolean(PREF_PREFIX + StandardField.ScreenResolution.getName(), true)
          .putBoolean(PREF_PREFIX + StandardField.UserLanguage.getName(), true)
          .apply();
    }
    ImmutableList.Builder<StandardField> builder = ImmutableList.builder();
    for (StandardField stdFld : StandardField.values()) {
      String key = PREF_PREFIX + stdFld.getName();
      if (preferences.getBoolean(key, false)) {
        builder.add(stdFld);
      }
    }
    config.setStandardFields(builder.build());

    // Add a few custom fields, email will be added after a login
    config.addCustomField("sdkVersion", SignalInc.getSdkVersion());
    config.addCustomField("demo", "true");

    // Add the hashed email if the user is already logged in
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
    if (trackingActive(siteId)) {
      return signalInc.getTracker(siteId);
    }
    return NULL_TRACKER;
  }

  /**
   * Returns the Google Analytics tracker.
   */
  @Provides @Singleton
  public com.google.android.gms.analytics.Tracker provideGoogleTracker() {
    GoogleAnalytics analytics = GoogleAnalytics.getInstance(appContext);
    return analytics.newTracker(R.xml.global_tracker);
  }

  @Provides @Singleton
  public SdkEventStack provideEventStack(SignalInc signalInc) {
    SdkEventStack eventStack = new SdkEventStack();
    signalInc.registerCallback(eventStack);
    return eventStack;
  }

  @Provides
  public TrackerWrapper provideTrackerWrapper(Tracker signalTracker,
      com.google.android.gms.analytics.Tracker gaTracker,
      SdkEventStack eventStack, @Named("SITE_ID") String siteId) {
    boolean gaActive = preferences.getBoolean(PREF_GA_ENABLED, true);
    SignalLogger.v("Return wrapper: " + (trackingActive(siteId) && gaActive));
    return new TrackerWrapper(signalTracker, gaTracker, eventStack, trackingActive(siteId) && gaActive);
  }

  @Provides @Singleton
  public SignalProfileStore provideProfileStore(SignalInc signalInc) {
    return signalInc.getProfileStore();
  }

  private long getPrefLong(String key, long defaultValue) {
    // Stored as a string via the settings pages, so need to convert back and forth
    return Long.parseLong(preferences.getString(key, String.valueOf(defaultValue)));
  }

  private int getPrefInt(String key, int defaultValue) {
    // Stored as a string via the settings pages, so need to convert back and forth
    return Integer.parseInt(preferences.getString(key, String.valueOf(defaultValue)));
  }

  private boolean trackingActive(String siteId) {
    return !TextUtils.isEmpty(siteId) && !"notset".equals(siteId.toLowerCase());
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
  public Cart provideShoppingCart(DBManager dbManager, UserManager userManager) {
    return new Cart(dbManager, userManager);
  }
}
