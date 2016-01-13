package co.signal.commerce.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.JsonReader;

import com.google.common.collect.ImmutableList;

import co.signal.commerce.model.Category;
import co.signal.commerce.model.Product;

/**
 * Manages the API calls and json processing to Magento on commerce.signal.ninja
 */
public class ApiManager {
  private static final String BOUTIQUE_111_URL = "http://commerce.signal.ninja/api/rest/";

  public ApiManager() { }

  public List<Category> getMainCategories() throws IOException {
    JsonReader reader = callServer("categories", null, null, null);
    try {
      return getCategories(reader);
    } finally {
      reader.close();
    }
  }

  public List<Category> getSubCategories(String categoryId) throws IOException {
    JsonReader reader = callServer("categories", categoryId, null, null);
    try {
      return getCategories(reader);
    } finally {
      reader.close();
    }
  }

  private List<Category> getCategories(JsonReader reader) throws IOException {
    ImmutableList.Builder<Category> builder = ImmutableList.builder();
    reader.beginObject();
    while (reader.hasNext()) {
      builder.add(readCategory(reader));
    }
    reader.endObject();
    return builder.build();
  }

  public List<Product> getProducts(String categoryId) throws IOException {
    JsonReader reader = callServer("products", null, "category_id", categoryId);
    try {
      ImmutableList.Builder<Product> builder = ImmutableList.builder();
      reader.beginObject();
      while (reader.hasNext()) {
        builder.add(readProduct(reader));
      }
      reader.endObject();
      return builder.build();
    } finally {
      reader.close();
    }
  }

  public Product getProductDetails(String productId) throws IOException {
    JsonReader reader = callServer("products", productId, null, null);
    Product product;
    try {
      product = readProduct(reader);
    } finally {
      reader.close();
    }
    return product;
  }

  /**
   * Call the server API and return the json data, or throw an exception
   *
   * @param resource The REST resource to call
   * @param key The resource key, if retrieving a single entity
   * @param param The query param to add to the URL
   * @param value The value of the query param
   * @return The JSON response from the server
   */
  private JsonReader callServer(String resource, @Nullable String key,
        @Nullable String param, @Nullable String value) throws IOException {
    JsonReader result = null;
    StringBuilder fullUrl = new StringBuilder(BOUTIQUE_111_URL).append(resource);
    if (!TextUtils.isEmpty(key)) {
      fullUrl.append('/').append(key);
    }
    if (!TextUtils.isEmpty(param) && !TextUtils.isEmpty(value)) {
      fullUrl.append('?').append(param).append('=').append(value);
    }

    URL url = new URL(fullUrl.toString());
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    try {
      result = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
    } finally {
      conn.disconnect();
    }
    return result;
  }

  private Category readCategory(JsonReader reader) throws IOException {
//  "26": {
//    "entity_id": "26",
//    "parent_id": "8",
//    "name": "Woman",
//    "children_count": "0"
//  },
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
      }
    }
    reader.endObject();
    return builder.build();
  }

  private Product readProduct(JsonReader reader) throws IOException {
//    "428": {
//      "entity_id": "428",
//      "type_id": "configurable",
//      "sku": "wpd010c",
//      "occasion": null,
//      "apparel_type": "37",
//      "gender": "94",
//      "color": null,
//      "size": null,
//      "description": "Wide leg pant, front pleat detail. Sits on natural waist. Wool, unlined. Dry clean.",
//      "meta_keyword": null,
//      "short_description": "Break away from the trend with these elegant pleat front pants. The high waistline and wide leg creates a feminine sihouette that's flattering on any figure. Pair with d'Orsay pumps and a waist belt.",
//      "name": "Park Avenue Pleat Front Trousers",
//      "meta_title": null,
//      "meta_description": null,
//      "regular_price_with_tax": 265.21,
//      "regular_price_without_tax": 245,
//      "final_price_with_tax": 265.21,
//      "final_price_without_tax": 245,
//      "is_saleable": false,
//      "image_url": "http://commerce.signal.ninja/media/catalog/product/cache/0/image/9df78eab33525d08d6e5fb8d27136e95/w/p/wpd010t.jpg"
//    }
    DecimalFormat formatter = new DecimalFormat("#0.00");
    Product.Builder builder = new Product.Builder();

    reader.beginObject();
    while (reader.hasNext()) {
      String key = reader.nextName();
      if ("entity_id".equals(key)) {
        builder.productId(reader.nextString());
      } else if ("sku".equals(key)) {
        builder.sku(reader.nextString());
      } else if ("name".equals(key)) {
        builder.title(reader.nextString());
      } else if ("description".equals(key)) {
        builder.description(reader.nextString());
      } else if ("short_description".equals(key)) {
        builder.details(reader.nextString());
      } else if ("regular_price_without_tax".equals(key)) {
        builder.regularPrice(reader.nextString());
      } else if ("regular_price_with_tax".equals(key)) {
        builder.regularPriceWithTax(formatter.format(reader.nextDouble()));
      } else if ("final_price_without_tax".equals(key)) {
        builder.finalPrice(formatter.format(reader.nextDouble()));
      } else if ("final_price_with_tax".equals(key)) {
        builder.finalPriceWithTax(formatter.format(reader.nextDouble()));
      } else if ("is_saleable".equals(key)) {
        builder.onSale(reader.nextBoolean());
      } else if ("image_url".equals(key)) {
        builder.imageUrl(reader.nextString());
      }
    }
    reader.endObject();
    return builder.build();
  }
}
