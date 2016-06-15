package co.signal.commerce.module;

import java.util.Map;

import com.google.android.gms.analytics.HitBuilders;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import co.signal.util.SignalLogger;

/**
 * Wrapper class to wrap multiple trackers and distribute events
 */
public class TrackerWrapper implements Tracking {
  private final co.signal.serverdirect.api.Tracker signalTracker;
  private final com.google.android.gms.analytics.Tracker gaTracker;
  private final SdkEventStack eventStack;
  private final boolean gaEnabled;

  public TrackerWrapper(co.signal.serverdirect.api.Tracker signalTracker,
                        com.google.android.gms.analytics.Tracker gaTracker,
                        SdkEventStack eventStack, boolean gaEnabled) {
    this.signalTracker = signalTracker;
    this.gaTracker = gaTracker;
    this.eventStack = eventStack;
    this.gaEnabled = gaEnabled;
  }

  public void trackView(String viewName) {
    // Signal
    signalTracker.publish(TRACK_VIEW, VIEW_NAME, viewName);

    if (!gaEnabled) {
      SignalLogger.v("GA Disabled - view ignored");
      return;
    }
    // GA
    gaTracker.setScreenName(viewName);
    gaTracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  public void trackEvent(String category, String action) {
    trackEvent(category, action, null, null, null);
  }

  public void trackEvent(String category, String action, String label, Integer value) {
    trackEvent(category, action, label, value, null);
  }

  public void trackEvent(String category, String action, String label, Integer value, String extraKey, String extraValue) {
    trackEvent(category, action, label, value, ImmutableMap.of(extraKey, extraValue));
  }

  public void trackEvent(String category, String action, String label, Integer value, Map<String, String> extras) {
    // Signal
    Map<String,String> eventValues = Maps.newHashMap();
    eventValues.put(CATEGORY, category);
    eventValues.put(ACTION, action);
    if (label != null) {
      eventValues.put(LABEL, label);
    }
    if (value != null) {
      eventValues.put(VALUE, value.toString());
    }
    if (extras != null) {
      eventValues.putAll(extras);
    }
    signalTracker.publish(TRACK_EVENT, eventValues);

    if (!gaEnabled) {
      SignalLogger.v("GA Disabled - event ignored");
      return;
    }
    // GA
    HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
    eventBuilder.setCategory(category).setAction(action);
    if (label != null) {
      eventBuilder.setLabel(label);
    }
    if (value != null) {
      eventBuilder.setValue(value);
    }
    gaTracker.send(eventBuilder.build());
  }
}
