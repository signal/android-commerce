package co.signal.commerce.api;

import java.io.IOException;

import android.util.JsonReader;

public interface BaseParser<T> {
  T parse(JsonReader reader) throws IOException;
}
