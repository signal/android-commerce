package co.signal.commerce.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Shopping cart
 */
public class Cart {
  public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

  private List<CartItem> items = new LinkedList<>();
  private BigDecimal cost = ZERO;
  private BigDecimal tax = ZERO;
  private BigDecimal total = ZERO;
  private boolean cartChanged = false;

  public Cart() { }

  public List<CartItem> getItems() {
    return items;
  }

  /**
   * Adding a product when you have the full object, usually during shopping
   * @param product The Product object
   */
  public void addProduct(Product product) {
    cartChanged = true;
    boolean found = false;
    for (CartItem cartItem : items) {
      if (cartItem.getProduct().getProductId().equals(product.getProductId())) {
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        found = true;
        break;
      }
    }
    if (!found) {
      items.add(new CartItem(this, product));
    }
    calculate();
  }

  /**
   * Decrement the count, or remove entirely a particular product
   * @param productId The ID of the product to remove
   */
  public void removeProduct(String productId) {
    cartChanged = true;
    Iterator<CartItem> iterator = items.iterator();
    while (iterator.hasNext()) {
      CartItem item = iterator.next();
      if (item.getProduct().getProductId().equals(productId)) {
        if (item.getQuantity() == 1) {
          iterator.remove();
        } else {
          item.setQuantity(item.getQuantity() - 1);
        }
        break;
      }
    }
    calculate();
  }

  public BigDecimal getCost() {
    return cost;
  }

  public BigDecimal getTax() {
    return tax;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public int getItemCount() {
    int count = 0;
    for (CartItem item : items) {
      count += item.getQuantity();
    }
    return count;
  }

  public boolean isEmpty() {
    return items.isEmpty();
  }

  public void clear() {
    cartChanged = true;
    items.clear();
    calculate();
  }

  /**
   * Returnes true if the cart was modified since the last time this was called
   */
  public boolean wasCartChanged() {
    boolean result = cartChanged;
    cartChanged = false;
    return result;
  }

  private void calculate() {
    BigDecimal newCost = ZERO;
    BigDecimal newTotal = ZERO;
    for (CartItem item : items) {
      Product product = item.getProduct();
      BigDecimal qty = item.getQuantity() == 1 ? BigDecimal.ONE : new BigDecimal(item.getQuantity());
      newCost = newCost.add(product.getFinalPrice().multiply(qty));
      newTotal = newCost.add(product.getFinalPriceWithTax().multiply(qty));
    }
    cost = newCost;
    total = newTotal;
    tax = total.subtract(cost);
  }
}
