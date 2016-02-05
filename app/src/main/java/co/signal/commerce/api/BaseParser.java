package co.signal.commerce.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;

import javax.inject.Inject;
import javax.inject.Named;

import android.util.JsonReader;

import co.signal.commerce.module.ApplicationModule;

public abstract class  BaseParser<T> {

  public abstract T parse(JsonReader reader) throws IOException;

  public BigDecimal parseMoney(String value) {
    return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP);
  }

  public BigDecimal parseMoney(Double value) {
    return new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP);
  }
}
