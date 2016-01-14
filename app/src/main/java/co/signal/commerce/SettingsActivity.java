package co.signal.commerce;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import java.util.List;

import javax.inject.Inject;

import co.signal.commerce.module.ApplicationModule;
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
  }  /**
   * A preference value change listener that updates the preference's summary
   * to reflect its new value.
   */
  private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
      String stringValue = value.toString();

      if (preference instanceof ListPreference) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);

        // Set the summary to reflect the new value.
        preference.setSummary(
            index >= 0
                ? listPreference.getEntries()[index]
                : null);

      } else {
        // For all other preferences, set the summary to the value's
        // simple string representation.
        preference.setSummary(stringValue);
      }
      return true;
    }
  };

  /**
   * {@inheritDoc}
   */
  @Override
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public void onBuildHeaders(List<Header> target) {
    loadHeadersFromResource(R.xml.pref_headers, target);
  }

  /**
   * Binds a preference's summary to its value. More specifically, when the
   * preference's value is changed, its summary (line of text below the
   * preference title) is updated to reflect the value. The summary is also
   * immediately updated upon calling this method. The exact display format is
   * dependent on the type of preference.
   *
   * @see #sBindPreferenceSummaryToValueListener
   */
  private static void bindPreferenceSummaryToValue(Preference preference) {
    // Set the listener to watch for value changes.
    preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

    // Trigger the listener immediately with the preference's
    // current value.
    sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
        PreferenceManager
            .getDefaultSharedPreferences(preference.getContext())
            .getString(preference.getKey(), ""));
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

  /**
   * This fragment shows general preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class GeneralPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_general);
      setHasOptionsMenu(true);

      // Bind the summaries of EditText/List/Dialog/Ringtone preferences
      // to their values. When their values change, their summaries are
      // updated to reflect the new value, per the Android Design
      // guidelines.
      bindPreferenceSummaryToValue(findPreference(ApplicationModule.PREF_SITE_ID));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * This fragment shows notification preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class LoggingPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_logging);
      setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * This fragment shows data and sync preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class ControlsPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_controls);
      setHasOptionsMenu(true);

      // Bind the summaries of EditText/List/Dialog/Ringtone preferences
      // to their values. When their values change, their summaries are
      // updated to reflect the new value, per the Android Design
      // guidelines.
      bindPreferenceSummaryToValue(findPreference("msg_expiration"));
      bindPreferenceSummaryToValue(findPreference("msg_max_queued"));
      bindPreferenceSummaryToValue(findPreference("msg_retry_count"));
      bindPreferenceSummaryToValue(findPreference("battery_percentage"));
      bindPreferenceSummaryToValue(findPreference("dispatch_interval"));
      bindPreferenceSummaryToValue(findPreference("socket_connect_to"));
      bindPreferenceSummaryToValue(findPreference("socket_read_to"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }
  }

  /**
   * This fragment shows notification preferences only. It is used when the
   * activity is showing a two-pane settings UI.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public static class StandardFieldPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      addPreferencesFromResource(R.xml.pref_std_fields);
      setHasOptionsMenu(true);

      // Bind the summaries of EditText/List/Dialog/Ringtone preferences
      // to their values. When their values change, their summaries are
      // updated to reflect the new value, per the Android Design
      // guidelines.
//      bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int id = item.getItemId();
      if (id == android.R.id.home) {
        startActivity(new Intent(getActivity(), SettingsActivity.class));
        return true;
      }
      return super.onOptionsItemSelected(item);
    }
  }

}
