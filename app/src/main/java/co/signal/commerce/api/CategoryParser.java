package co.signal.commerce.api;

import java.io.IOException;

import android.util.JsonReader;

import co.signal.commerce.model.Category;

/**
 * Parses the json for a Category object
 *
 *  "26": {
 *    "entity_id": "26",
 *    "parent_id": "8",
 *    "name": "Woman",
 *    "children_count": "0"
 *  }
 */
public class CategoryParser implements BaseParser<Category> {

  public Category parse(JsonReader reader) throws IOException {
    Category.Builder builder = new Category.Builder();
    reader.beginObject();
    while (reader.hasNext()) {
      String key = reader.nextName();
      if ("entity_id".equals(key)) {
        builder.categoryId(reader.nextString());
      } else if ("name".equals(key)) {
        builder.name(reader.nextString());
      } else if ("parent_id".equals(key)) {
        builder.parentId(reader.nextString());
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();
    return builder.build();
  }
}
