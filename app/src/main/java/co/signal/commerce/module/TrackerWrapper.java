package co.signal.commerce.module;

import java.util.List;
import java.util.Map;

import co.signal.serverdirect.api.StandardField;
import co.signal.serverdirect.api.Tracker;

/**
 * Wrapper class to wrap multiple trackers in the event more than one is active
 */
public class TrackerWrapper implements Tracker {
  private List<Tracker> trackers;

  public TrackerWrapper(List<Tracker> trackers) {
    this.trackers = trackers;
  }

  @Override
  public String getSiteId() {
    throw new UnsupportedOperationException("Not available in wrapper tracker");
  }

  @Override
  public boolean isDebug() {
    throw new UnsupportedOperationException("Not available in wrapper tracker");
  }

  @Override
  public void setDebug(boolean b) {
    throw new UnsupportedOperationException("Not available in wrapper tracker");

  }

  @Override
  public void publish(String event, String... params) {
    for (Tracker tracker : trackers) {
      tracker.publish(event, params);
    }
  }

  @Override
  public void publish(String event, Map<String, String> params) {
    for (Tracker tracker : trackers) {
      tracker.publish(event, params);
    }
  }

  @Override
  public void addStandardFields(StandardField... standardFields) {
    throw new UnsupportedOperationException("Not available in wrapper tracker");
  }

  @Override
  public void addCustomField(String s, String s1) {
    throw new UnsupportedOperationException("Not available in wrapper tracker");
  }
}
