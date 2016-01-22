package co.signal.commerce;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

import javax.inject.Inject;

import com.google.common.collect.ImmutableList;

import co.signal.commerce.module.ApplicationModule;
import co.signal.commerce.module.EndpointBuilder;
import co.signal.serverdirect.api.SignalConfig;
import co.signal.serverdirect.api.StandardField;
import co.signal.serverdirect.api.Tracker;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

  @Inject
  Tracker tracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    ((CommerceApplication)getApplication()).inject(this);
    super.onCreate(savedInstanceState);
    setupActionBar();
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    String name = this.getClass().getSimpleName();
    tracker.publish("view:" + name);
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  private void setupActionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      // Show the Up button in the action bar.
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  /**
   * Helper method to determine if the device has an extra-large screen. For
   * example, 10" tablets are extra-large.
   */
  private static boolean isXLargeTablet(Context context) {
    return (context.getResources().getConfiguration().screenLayout
        & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean onIsMultiPane() {
    return isXLargeTablet(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.pref_headers, target);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * This method stops fragment injection in malicious applications.
   * Make sure to deny any unknown fragments here.
   */
  protected boolean isValidFragment(String fragmentName) {
    return PreferenceFragment.class.getName().equals(fragmentName)
        || GeneralPreferenceFragment.class.getName().equals(fragmentName)
        || ControlsPreferenceFragment.class.getName().equals(fragmentName)
        || LoggingPreferenceFragment.class.getName().equals(fragmentName)
        || StandardFieldPreferenceFragment.class.getName().equals(fragmentName);
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class GeneralPreferenceFragment extends PreferenceFragment {
    @Inject
    SignalConfig config;
    @Inject
    EndpointBuilder endpointBuilder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ((CommerceApplication)getActivity().getApplication()).inject(this);

      addPreferencesFromResource(R.xml.pref_general);
      setHasOptionsMenu(true);

      final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

      attachListenerAndUpdateSummary(findPreference("siteid"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          // noop for siteid
        }
      });
      attachListenerAndUpdateSummary(findPreference("environment"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setEndpoint(endpointBuilder.buildFromEnvironment(value.toString()));
        }
      });
      attachListenerAndUpdateSummary(findPreference("protocol"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setEndpoint(endpointBuilder.buildFromProtocol(value.toString()));
        }
      });

      findPreference("enable_lifecycle").setOnPreferenceChangeListener(new SignalConfigUpdateListener() {
        @Override
        void updateConfig(Preference preference, Object value) {
          config.setLifecycleEventsEnabled((Boolean) value);
        }
      });
      findPreference("enable_background").setOnPreferenceChangeListener(new SignalConfigUpdateListener() {
        @Override
        void updateConfig(Preference preference, Object value) {
          config.setPublishInBackground((Boolean) value);
        }
      });
      findPreference("enable_wifi").setOnPreferenceChangeListener(new SignalConfigUpdateListener() {
        @Override
        void updateConfig(Preference preference, Object value) {
          config.setNetworkWifiOnly((Boolean) value);
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class LoggingPreferenceFragment extends PreferenceFragment {
    @Inject
    SignalConfig config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ((CommerceApplication)getActivity().getApplication()).inject(this);

      addPreferencesFromResource(R.xml.pref_logging);
      setHasOptionsMenu(true);

      findPreference("debug_enabled").setOnPreferenceChangeListener(new SignalConfigUpdateListener() {
        @Override
        void updateConfig(Preference preference, Object value) {
          config.setDebug((Boolean) value);
        }
      });
      findPreference("verbose_enabled").setOnPreferenceChangeListener(new SignalConfigUpdateListener() {
        @Override
        void updateConfig(Preference preference, Object value) {
          config.setVerbose((Boolean) value);
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class ControlsPreferenceFragment extends PreferenceFragment {
    @Inject
    SignalConfig config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ((CommerceApplication)getActivity().getApplication()).inject(this);

      addPreferencesFromResource(R.xml.pref_controls);
      setHasOptionsMenu(true);

      attachListenerAndUpdateSummary(findPreference("msg_expiration"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setMessageExpiration(Long.parseLong(value.toString()));
        }
      });
      attachListenerAndUpdateSummary(findPreference("msg_max_queued"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setMaxQueuedMessages(Integer.parseInt(value.toString()));
        }
      });
      attachListenerAndUpdateSummary(findPreference("msg_retry_count"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setMessageRetryCount(Integer.parseInt(value.toString()));
        }
      });
      attachListenerAndUpdateSummary(findPreference("battery_percentage"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setBatteryPercentage(Integer.parseInt(value.toString()));
        }
      });
      attachListenerAndUpdateSummary(findPreference("dispatch_interval"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setDispatchInterval(Long.parseLong(value.toString()));
        }
      });
      attachListenerAndUpdateSummary(findPreference("socket_connect_to"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setSocketConnectTimeout(Long.parseLong(value.toString()));
        }
      });
      attachListenerAndUpdateSummary(findPreference("socket_read_to"), new SummaryUpdateListener() {
        @Override
        public void updateConfig(Preference preference, Object value) {
          config.setSocketReadTimeout(Long.parseLong(value.toString()));
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class StandardFieldPreferenceFragment extends PreferenceFragment {
    @Inject
    SignalConfig config;

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ((CommerceApplication)getActivity().getApplication()).inject(this);

      addPreferencesFromResource(R.xml.pref_std_fields);
      setHasOptionsMenu(true);

      Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
          List<StandardField> empty = ImmutableList.of();
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

          config.setStandardFields(empty);
          for (StandardField stdFld : StandardField.values()) {
            String key = "pref_" + stdFld.getName();
            if (preference.getKey().equals(key)) {
              // The current checkbox will not be set in prefs yet, so check the new value
              if ((Boolean)newValue) {
                config.addStandardField(stdFld);
              }
            } else if (prefs.getBoolean(key, false)) {
              config.addStandardField(stdFld);
            }
          }
          return true;
        }
      };

      for (StandardField stdFld : StandardField.values()) {
        Preference preference = findPreference("pref_" + stdFld.getName());
        if (preference != null) {
          preference.setOnPreferenceChangeListener(listener);
        }
      }
    }
  }

  /**
   * Binds a preference's summary to its value. More specifically, when the
   * preference's value is changed, its summary (line of text below the
   * preference title) is updated to reflect the value. The summary is also
   * immediately updated upon calling this method. The exact display format is
   * dependent on the type of preference.
   */
  private static void attachListenerAndUpdateSummary(Preference preference, SummaryUpdateListener listener) {
    // Set the listener to watch for value changes.
    preference.setOnPreferenceChangeListener(listener);

    // Trigger the listener immediately with the preference's current value.
    listener.updateSummary(preference,
        PreferenceManager.getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
  }

  /**
   * A preference value listener that updates the SignalConfig bundle to reflect its new value.
   */
  private static abstract class SignalConfigUpdateListener implements Preference.OnPreferenceChangeListener {
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
      updateConfig(preference, value);
      return true;
    }

    abstract void updateConfig(Preference preference, Object value);
  }

  /**
   * A preference value change listener that updates the preference's summary
   * to reflect its new value as well as updating the SignalConfig.
   */
  private static abstract class SummaryUpdateListener extends SignalConfigUpdateListener {

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
      updateSummary(preference, value);
      return super.onPreferenceChange(preference, value);
    }

    void updateSummary(Preference preference, Object value) {
      String stringValue = value.toString();
      if (preference instanceof ListPreference) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);

        // Set the summary to reflect the new value.
        preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

      } else {
        // For all other preferences, set the summary to the value's
        // simple string representation.
        preference.setSummary(stringValue);
      }
    }
  }
}
