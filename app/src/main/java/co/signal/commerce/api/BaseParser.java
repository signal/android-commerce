package co.signal.commerce.api;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import android.util.JsonReader;

public abstract class BaseParser<T> {
  @Inject @Named("THUMB_URL")
  String thumbnailRootUrl;

  public abstract T parse(JsonReader reader) throws IOException;

  protected String getThumbnailUrl(String imageUrl, int size) {
    return thumbnailRootUrl + "/" + size + "/" + imageUrl;
  }
}
