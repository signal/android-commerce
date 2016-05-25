package co.signal.commerce;

import javax.inject.Inject;

import android.app.Application;

import co.signal.commerce.module.ApplicationModule;
import co.signal.serverdirect.api.Tracker;
import dagger.ObjectGraph;

/**
 * Override application class for DI
 */
public class CommerceApplication extends Application {
  private ObjectGraph objectGraph;

  @Override
  public void onCreate() {
    objectGraph = ObjectGraph.create(new ApplicationModule(this));

    // Uncomment to test launching from Application rather than first Activity
//    TestInject ti = new TestInject();
//    objectGraph.inject(ti);
//    ti.go();

    super.onCreate();
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }

  public static class TestInject {
    @Inject
    Tracker tracker;

    public void go() {
      tracker.publish("testinject");
    }
  }
}
