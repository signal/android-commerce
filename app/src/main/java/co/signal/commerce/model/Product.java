package co.signal.commerce.model;

/**
 * Product Details
 */
public class Product {
  private final String productId;
  private final String sku;
  private final String name;
  private final String description;
  private final String imageUrl;
  private final String regularPrice;
  private final String regularPriceWithTax;
  private final String finalPrice;
  private final String finalPriceWithTax;
  private final boolean onSale;

  private Product(String productId, String sku, String name, String description, String imageUrl,
                 String regularPrice, String regularPriceWithTax,
                 String finalPrice, String finalPriceWithTax, boolean onSale) {
    this.productId = productId;
    this.sku = sku;
    this.name = name;
    this.description = description;
    this.imageUrl = imageUrl;
    this.regularPrice = regularPrice;
    this.regularPriceWithTax = regularPriceWithTax;
    this.finalPrice = finalPrice;
    this.finalPriceWithTax = finalPriceWithTax;
    this.onSale = onSale;
  }

  public String getProductId() {
    return productId;
  }

  public String getSku() {
    return sku;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public String getRegularPrice() {
    return regularPrice;
  }

  public String getRegularPriceWithTax() {
    return regularPriceWithTax;
  }

  public String getFinalPrice() {
    return finalPrice;
  }

  public String getFinalPriceWithTax() {
    return finalPriceWithTax;
  }

  public boolean isOnSale() {
    return onSale;
  }

  public static final class Builder {
    private String productId;
    private String sku;
    private String name;
    private String description;
    private String imageUrl;
    private String regularPrice;
    private String regularPriceWithTax;
    private String finalPrice;
    private String finalPriceWithTax;
    private boolean onSale;

    public Builder productId(String productId) {
      this.productId = productId;
      return this;
    }

    public Builder sku(String sku) {
      this.sku = sku;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder imageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public Builder regularPrice(String regularPrice) {
      this.regularPrice = regularPrice;
      return this;
    }

    public Builder regularPriceWithTax(String regularPriceWithTax) {
      this.regularPriceWithTax = regularPriceWithTax;
      return this;
    }

    public Builder finalPriceWithTax(String finalPriceWithTax) {
      this.finalPriceWithTax = finalPriceWithTax;
      return this;
    }

    public Builder finalPrice(String finalPrice) {
      this.finalPrice = finalPrice;
      return this;
    }

    public Builder onSale(boolean onSale) {
      this.onSale = onSale;
      return this;
    }
  }
}

//"384": {
//    "entity_id": "384",
//    "type_id": "simple",
//    "sku": "hdb008",
//    "material": "129",
//    "bed_bath_type": "184",
//    "decor_type": "217",
//    "color": "27",
//    "description": "Woven acrylic/wool/cotton. 50\" x 75\". Spot clean.",
//    "meta_keyword": null,
//    "short_description": "A rustic wool blend leaves our Park Row Throw feeling lofty and warm. Packs perfectly into carry-ons.",
//    "name": "Park Row Throw",
//    "meta_title": null,
//    "meta_description": null,
//    "regular_price_with_tax": 259.8,
//    "regular_price_without_tax": 240,
//    "final_price_with_tax": 129.9,
//    "final_price_without_tax": 120,
//    "is_saleable": true,
//    "image_url": "http://commerce.signal.ninja/media/catalog/product/cache/0/image/9df78eab33525d08d6e5fb8d27136e95/h/d/hdb008_1.jpg"
//    }
