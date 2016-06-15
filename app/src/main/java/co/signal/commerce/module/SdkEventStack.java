package co.signal.commerce.module;

import java.util.LinkedList;

import javax.inject.Singleton;

import com.google.common.collect.Lists;

import co.signal.commerce.BaseActivity;
import co.signal.serverdirect.ServerDirectCallback;
import co.signal.serverdirect.ServerDirectRequest;
import co.signal.serverdirect.ServerDirectResponse;

@Singleton
public class SdkEventStack implements ServerDirectCallback {
  private int queueSize = 0;
  private final LinkedList<ServerDirectRequest> events = Lists.newLinkedList();
  BaseActivity currentView;

  public void setCurrentView(BaseActivity currentView) {
    this.currentView = currentView;
  }

  public synchronized int getQueueSize() {
    return queueSize;
  }

  public synchronized String activeEventInfo() {
    if (events.size() == 0) {
      return "";
    }
    ServerDirectRequest event = events.getLast();
    return event.getEvent() + " (" + event.getData().size() + ")";
  }

  @Override
  public synchronized void onPublish(ServerDirectRequest request, int queueSize) {
    this.queueSize = queueSize;
    if (currentView != null) {
      currentView.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          currentView.showSdkStatus();
        }
      });
    }
  }

  @Override
  public synchronized void onComplete(ServerDirectRequest request, ServerDirectResponse response) {
    events.add(request);
    if (events.size() > 10) {
      events.removeFirst();
    }
    if (currentView != null) {
      currentView.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          currentView.showSdkStatus();
        }
      });
    }
  }

  @Override
  public synchronized void onError(Exception e, ServerDirectRequest serverDirectRequest) { }

  @Override
  public void onDiscard(ServerDirectRequest serverDirectRequest) { }
}
