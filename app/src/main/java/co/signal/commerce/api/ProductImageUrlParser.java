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
public class ProductImageUrlParser extends BaseParser<List<String>> {

  @Override
  public List<String> parse(JsonReader reader) throws IOException {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    if (reader.peek() != JsonToken.BEGIN_ARRAY) {
      // An empty array is returned when there is an error
      return builder.build();
    }

    reader.beginArray();
    while (reader.hasNext()) {
      reader.beginObject();
      while (reader.hasNext()) {
        String key = reader.nextName();
        if ("url".equals(key)) {
          builder.add(reader.nextString());
        } else if ("types".equals(key)) {
          reader.beginArray();
          while (reader.hasNext()) {
            reader.skipValue();
          }
          reader.endArray();
        } else {
          reader.skipValue();
        }
      }
      reader.endObject();
    }
    reader.endArray();

    return builder.build();
  }
}

//[
//  {
//    "id": "760",
//    "label": "",
//    "position": "1",
//    "url": "http://commerce.signal.ninja/media/catalog/product/w/s/wsd000t.jpg",
//    "types": [
//      "image",
//      "small_image",
//      "thumbnail"
//    ]
//  },
//  {
//    "id": "704",
//    "label": "",
//    "position": "2",
//    "url": "http://commerce.signal.ninja/media/catalog/product/w/s/wsd000a_2.jpg",
//    "types": []
//  },
//  {
//    "id": "705",
//    "label": "",
//    "position": "3",
//    "url": "http://commerce.signal.ninja/media/catalog/product/w/s/wsd000b_2.jpg",
//    "types": []
//  }
//]
