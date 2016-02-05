package co.signal.commerce.api;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.util.JsonReader;
import android.util.JsonToken;

import com.google.common.collect.ImmutableList;

import co.signal.commerce.model.Product;

@Singleton
public class ProductListParser extends BaseParser<List<Product>> {
  @Inject
  ProductParser productParser;

  @Override
  public List<Product> parse(JsonReader reader) throws IOException {
    ImmutableList.Builder<Product> builder = ImmutableList.builder();
    if (reader.peek() == JsonToken.BEGIN_ARRAY) {
      // An empty array is returned when there are no products.
      reader.beginArray();
      reader.endArray();
      return builder.build();
    }

    reader.beginObject();
    while (reader.hasNext()) {
      reader.nextName(); // The entity id, skip it
      builder.add(productParser.parse(reader));
    }
    reader.endObject();
    return builder.build();
  }
}
