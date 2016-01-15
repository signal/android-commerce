package co.signal.commerce.api;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import android.util.JsonReader;

import com.google.common.collect.ImmutableList;

import co.signal.commerce.model.Product;

public class ProductListParser implements BaseParser<List<Product>> {
  @Inject
  ProductParser productParser;

  @Override
  public List<Product> parse(JsonReader reader) throws IOException {
    ImmutableList.Builder<Product> builder = ImmutableList.builder();
    reader.beginObject();
    while (reader.hasNext()) {
      reader.nextName(); // The entity id, skip it
      builder.add(productParser.parse(reader));
    }
    reader.endObject();
    return builder.build();
  }
}
