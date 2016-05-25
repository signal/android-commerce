package co.signal.commerce.module;

import java.util.Arrays;
import java.util.Map;

import co.signal.serverdirect.api.StandardField;
import co.signal.serverdirect.api.Tracker;
import co.signal.util.SignalLogger;

/**
 * A tracker that only logs activity. Used for testing and before the SiteId is set.
 */
public class NullTracker implements Tracker {
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
}
