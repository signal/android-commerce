package co.signal.commerce.model;

/**
 * Shopping Cart Item
 */
public class CartItem {
  private final Product product;
  private int quantity;

  public CartItem(Product product) {
    this.product = product;
    this.quantity = 1;
  }

  public CartItem(Product product, int quantity) {
    this.product = product;
    this.quantity = quantity;
  }

  public Product getProduct() {
    return product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
