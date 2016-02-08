package co.signal.commerce.model;

/**
 * Shopping Cart Item
 */
public class CartItem {
  private final Cart cart;
  private final Product product;
  private int quantity;

  public CartItem(Cart cart, Product product) {
    this.cart = cart;
    this.product = product;
    this.quantity = 1;
  }

  public Cart getCart() {
    return cart;
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
