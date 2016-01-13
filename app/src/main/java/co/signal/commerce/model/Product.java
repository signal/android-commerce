package co.signal.commerce.model;

/**
 * Product Details
 */
public class Product {
  private final String productId;
  private final String sku;
  private final String title;
  private final String description;
  private final String details;
  private final String imageUrl;
  private final String regularPrice;
  private final String regularPriceWithTax;
  private final String finalPrice;
  private final String finalPriceWithTax;
  private final boolean onSale;

  private Product(Builder builder) {
    this.productId = builder.productId;
    this.sku = builder.sku;
    this.title = builder.title;
    this.description = builder.description;
    this.details = builder.details;
    this.imageUrl = builder.imageUrl;
    this.regularPrice = builder.regularPrice;
    this.regularPriceWithTax = builder.regularPriceWithTax;
    this.finalPrice = builder.finalPrice;
    this.finalPriceWithTax = builder.finalPriceWithTax;
    this.onSale = builder.onSale;
  }

  public String getProductId() {
    return productId;
  }

  public String getSku() {
    return sku;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getDetails() {
    return details;
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
    private String title;
    private String description;
    private String details;
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

    public Builder title(String title) {
      this.title = title;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder details(String details) {
      this.details = details;
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

    public Product build() {
      return new Product(this);
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
