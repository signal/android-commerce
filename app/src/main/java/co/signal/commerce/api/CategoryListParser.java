package co.signal.commerce.api;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.util.JsonReader;

import com.google.common.collect.ImmutableList;

import co.signal.commerce.model.Category;

@Singleton
public class CategoryListParser implements BaseParser<List<Category>> {
  @Inject
  CategoryParser categoryParser;

  @Override
  public List<Category> parse(JsonReader reader) throws IOException {
    ImmutableList.Builder<Category> builder = ImmutableList.builder();
    reader.beginObject();
    while (reader.hasNext()) {
      reader.nextName(); // The entity id, skip it
      builder.add(categoryParser.parse(reader));
    }
    reader.endObject();
    return builder.build();
  }
}
