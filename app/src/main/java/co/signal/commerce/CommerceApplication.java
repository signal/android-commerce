package co.signal.commerce;

import android.app.Application;

import co.signal.commerce.module.ApplicationModule;
import dagger.ObjectGraph;

/**
 * Override application class for DI
 */
public class CommerceApplication extends Application {
  private ObjectGraph objectGraph;

  @Override
  public void onCreate() {
    super.onCreate();
    objectGraph = ObjectGraph.create(new ApplicationModule());
  }

  public ObjectGraph getObjectGraph() {
    return objectGraph;
  }

  public void inject(Object object) {
    objectGraph.inject(object);
  }
}
