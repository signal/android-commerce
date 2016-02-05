package co.signal.commerce.api;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import android.util.JsonReader;
import android.util.JsonToken;

import co.signal.commerce.model.Product;
import co.signal.commerce.module.ApplicationModule;

/**
 * Parses the json for a Product object
 */
@Singleton
public class ProductParser extends BaseParser<Product> {

  @Inject @Named(ApplicationModule.NAME_THUMB_URL)
  String thumbnailRootUrl;

  @Override
  public Product parse(JsonReader reader) throws IOException {
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
        builder.regularPrice(parseMoney(reader.nextDouble()));
      } else if ("regular_price_with_tax".equals(key)) {
        builder.regularPriceWithTax(parseMoney(reader.nextDouble()));
      } else if ("final_price_without_tax".equals(key)) {
        builder.finalPrice(parseMoney(reader.nextDouble()));
      } else if ("final_price_with_tax".equals(key)) {
        builder.finalPriceWithTax(parseMoney(reader.nextDouble()));
      } else if ("is_saleable".equals(key)) {
        // Magento sends boolean for False, and a "1" string for True
        if (reader.peek() == JsonToken.BOOLEAN) {
          builder.onSale(reader.nextBoolean());
        } else {
          builder.onSale("1".equals(reader.nextString()));
        }
      } else if ("is_in_stock".equals(key)) {
        if (reader.peek() == JsonToken.BOOLEAN) {
          builder.inStock(reader.nextBoolean());
        } else {
          builder.inStock("1".equals(reader.nextString()));
        }
      } else if ("image_url".equals(key)) {
        String url = reader.nextString();
        builder.imageUrl(url);
        builder.thumbnailUrl(getThumbnailUrl(url, 100));
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();
    return builder.build();
  }

  protected String getThumbnailUrl(String imageUrl, int size) {
    return thumbnailRootUrl + size + "/" + imageUrl;
  }
}
/**
 * Parses the json for a Product object
 *
 * "428": {
 *   "entity_id": "428",
 *   "type_id": "configurable",
 *   "sku": "wpd010c",
 *   "occasion": null,
 *   "apparel_type": "37",
 *   "gender": "94",
 *   "color": null,
 *   "size": null,
 *   "description": "Wide leg pant, front pleat detail. Sits on natural waist. Wool, unlined. Dry clean.",
 *   "meta_keyword": null,
 *   "short_description": "Break away from the trend with these elegant pleat front pants. The high waistline and wide leg creates a feminine sihouette that's flattering on any figure. Pair with d'Orsay pumps and a waist belt.",
 *   "name": "Park Avenue Pleat Front Trousers",
 *   "meta_title": null,
 *   "meta_description": null,
 *   "regular_price_with_tax": 265.21,
 *   "regular_price_without_tax": 245,
 *   "final_price_with_tax": 265.21,
 *   "final_price_without_tax": 245,
 *   "is_saleable": false,
 *   "is_in_stock": false,
 *   "image_url": "http://commerce.signal.ninja/media/catalog/product/cache/0/image/9df78eab33525d08d6e5fb8d27136e95/w/p/wpd010t.jpg"
 * }
 */
