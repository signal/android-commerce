package co.signal.commerce.model;

import java.math.BigDecimal;

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
  private final String thumbnailUrl;
  private final BigDecimal regularPrice;
  private final BigDecimal regularPriceWithTax;
  private final BigDecimal finalPrice;
  private final BigDecimal finalPriceWithTax;
  private final boolean onSale;
  private final boolean inStock;

  private Product(Builder builder) {
    this.productId = builder.productId;
    this.sku = builder.sku;
    this.title = builder.title;
    this.description = builder.description;
    this.details = builder.details;
    this.imageUrl = builder.imageUrl;
    this.thumbnailUrl = builder.thumbnailUrl;
    this.regularPrice = builder.regularPrice;
    this.regularPriceWithTax = builder.regularPriceWithTax;
    this.finalPrice = builder.finalPrice;
    this.finalPriceWithTax = builder.finalPriceWithTax;
    this.onSale = builder.onSale;
    this.inStock = builder.inStock;
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

  public String getThumbnailUrl() {
    return thumbnailUrl;
  }

  public BigDecimal getRegularPrice() {
    return regularPrice;
  }

  public BigDecimal getRegularPriceWithTax() {
    return regularPriceWithTax;
  }

  public BigDecimal getFinalPrice() {
    return finalPrice;
  }

  public BigDecimal getFinalPriceWithTax() {
    return finalPriceWithTax;
  }

  public boolean isOnSale() {
    return onSale;
  }

  public boolean isInStock() {
    return inStock;
  }

  public static final class Builder {
    private String productId;
    private String sku;
    private String title;
    private String description;
    private String details;
    private String imageUrl;
    private String thumbnailUrl;
    private BigDecimal regularPrice;
    private BigDecimal regularPriceWithTax;
    private BigDecimal finalPrice;
    private BigDecimal finalPriceWithTax;
    private boolean onSale = false;
    private boolean inStock = false;

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

    public Builder thumbnailUrl(String thumbnailUrl) {
      this.thumbnailUrl = thumbnailUrl;
      return this;
    }

    public Builder regularPrice(BigDecimal regularPrice) {
      this.regularPrice = regularPrice;
      return this;
    }

    public Builder regularPriceWithTax(BigDecimal regularPriceWithTax) {
      this.regularPriceWithTax = regularPriceWithTax;
      return this;
    }

    public Builder finalPriceWithTax(BigDecimal finalPriceWithTax) {
      this.finalPriceWithTax = finalPriceWithTax;
      return this;
    }

    public Builder finalPrice(BigDecimal finalPrice) {
      this.finalPrice = finalPrice;
      return this;
    }

    public Builder onSale(boolean onSale) {
      this.onSale = onSale;
      return this;
    }

    public Builder inStock(boolean inStock) {
      this.inStock = inStock;
      return this;
    }

    public Product build() {
      return new Product(this);
    }
  }
}
