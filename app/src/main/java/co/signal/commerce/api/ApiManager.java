package co.signal.commerce.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.JsonReader;

import co.signal.commerce.model.Category;
import co.signal.commerce.model.Product;

/**
 * Manages the API calls and json processing to Magento on commerce.signal.ninja
 */
public class ApiManager {

  @Inject @Named("API_URL")
  String baseUrl;
  @Inject
  CategoryListParser categoryListParser;
  @Inject
  ProductListParser productListParser;
  @Inject
  ProductParser productParser;

//  @Inject
//  public ApiManager(String baseUrl, CategoryListParser categoryListParser, ProductListParser productListParser, ProductParser productParser) {
//    this.baseUrl = baseUrl;
//    this.categoryListParser = categoryListParser;
//    this.productListParser = productListParser;
//    this.productParser = productParser;
//  }

  public List<Category> getMainCategories() throws IOException {
    return callServer(categoryListParser, buildUrl("categories", null, null, null));
  }

  public List<Category> getSubCategories(String categoryId) throws IOException {
    return callServer(categoryListParser, buildUrl("categories", categoryId, null, null));
  }

  public List<Product> getProducts(String categoryId) throws IOException {
    return callServer(productListParser, buildUrl("products", null, "category_id", categoryId));
  }

  public Product getProductDetails(String productId) throws IOException {
    return callServer(productParser, buildUrl("products", productId, null, null));
  }

  /**
   * Create the URL for the server api call
   *
   * @param resource The REST resource to call
   * @param key The resource key, if retrieving a single entity
   * @param param The query param to add to the URL
   * @param value The value of the query param
   */
  private URL buildUrl(String resource, @Nullable String key, @Nullable String param,
                       @Nullable String value) throws IOException {
    StringBuilder fullUrl = new StringBuilder(baseUrl).append(resource);
    if (!TextUtils.isEmpty(key)) {
      fullUrl.append('/').append(key);
    }
    if (!TextUtils.isEmpty(param) && !TextUtils.isEmpty(value)) {
      fullUrl.append('?').append(param).append('=').append(value);
    }
    return new URL(fullUrl.toString());
  }

  /**
   * Call the server API and return the json data, or throw an exception
   *
   * @return The response object from the server
   */
  private static <T> T callServer(BaseParser<T> parser, URL url) throws IOException {
    T result = null;
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
    try {
      JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
      try {
        result = parser.parse(reader);
      } finally {
        reader.close();
      }
    } finally {
      conn.disconnect();
    }
    return result;
  }
}
