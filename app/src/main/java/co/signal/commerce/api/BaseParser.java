package co.signal.commerce.api;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import android.util.JsonReader;

import co.signal.commerce.module.ApplicationModule;

public interface  BaseParser<T> {

  T parse(JsonReader reader) throws IOException;
}
