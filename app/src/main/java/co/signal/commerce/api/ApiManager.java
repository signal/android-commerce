package co.signal.commerce.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

import co.signal.commerce.model.Category;
import co.signal.commerce.model.Product;

/**
 * Manages the API calls and json processing to Magento on commerce.signal.ninja
 */
@Singleton
public class ApiManager {

  @Inject @Named("API_URL")
  String baseUrl;
  @Inject
  CategoryListParser categoryListParser;
  @Inject
  ProductListParser productListParser;
  @Inject
  ProductParser productParser;
  @Inject
  ProductImageUrlParser productUrlParser;

  public List<Category> getMainCategories() throws IOException {
    return callServer(categoryListParser, buildUrl("categories", null, null, null, null));
  }

  public List<Category> getSubCategories(String categoryId) throws IOException {
    return callServer(categoryListParser, buildUrl("categories", null, categoryId, null, null));
  }

  public List<Product> getProducts(String categoryId) throws IOException {
    return callServer(productListParser, buildUrl("products", null, null, "category_id", categoryId));
  }

  public Product getProductDetails(String productId) throws IOException {
    return callServer(productParser, buildUrl("products", null, productId, null, null));
  }

  public List<String> getProductImages(String productId) throws IOException {
    return callServer(productUrlParser, buildUrl("products", "images", productId, null, null));
  }

  /**
   * Create the URL for the server api call
   *
   * @param resource The REST resource to call
   * @param key The resource key, if retrieving a single entity
   * @param subResource The child resource of a resource
   * @param param The query param to add to the URL
   * @param value The value of the query param
   */
  private URL buildUrl(String resource, @Nullable String subResource,
                       @Nullable String key, @Nullable String param,
                       @Nullable String value) throws IOException {
    StringBuilder fullUrl = new StringBuilder(baseUrl).append(resource);
    if (!TextUtils.isEmpty(key)) {
      fullUrl.append('/').append(key);
    }
    if (!TextUtils.isEmpty(subResource)) {
      fullUrl.append('/').append(subResource);
    }
    if (!TextUtils.isEmpty(param) && !TextUtils.isEmpty(value)) {
      fullUrl.append('?').append(param).append('=').append(value);
    }
    return new URL(fullUrl.toString());
  }

  /**
   * Call the server API and parse the response data returning the corresponding object.
   *
   * @return The response object from the server
   * @throws IOException in the event the server or request fails
   */
  private static <T> T callServer(BaseParser<T> parser, URL url) throws IOException {
    T result = null;
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    conn.setRequestProperty("Accept", "application/json");
    try {
      int resp = conn.getResponseCode();
      if (resp == 200) {
        InputStream inputStream = conn.getInputStream();
        JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
          result = parser.parse(reader);
        } finally {
          reader.close();
        }
      } else {
        Log.e("api", "Request Failed: " + resp + "|" + conn.getResponseMessage());
      }
    } finally {
      conn.disconnect();
    }
    return result;
  }
}
