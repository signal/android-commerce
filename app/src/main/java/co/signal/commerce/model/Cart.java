package co.signal.commerce.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import co.signal.commerce.api.UserManager;
import co.signal.commerce.db.DBManager;

/**
 * Shopping cart
 */
public class Cart {
  public static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

  private DBManager dbManager;
  private UserManager userManager;
  private List<CartItem> items = new LinkedList<>();
  private BigDecimal cost = ZERO;
  private BigDecimal tax = ZERO;
  private BigDecimal total = ZERO;

  public Cart(DBManager dbManager, UserManager userManager) {
    this.dbManager = dbManager;
    this.userManager = userManager;
  }

  public List<CartItem> getItems() {
    return items;
  }

  /**
   * Adding a product when you have the full object, usually during shopping
   * @param product The Product object
   */
  public void addProduct(Product product) {
    boolean found = false;
    for (CartItem cartItem : items) {
      if (cartItem.getProduct().getProductId().equals(product.getProductId())) {
        cartItem.setQuantity(cartItem.getQuantity() + 1);
        dbManager.updateCartItem(cartItem);
        found = true;
        break;
      }
    }
    if (!found) {
      CartItem cartItem = new CartItem(this, product);
      items.add(cartItem);
      dbManager.saveCartItem(cartItem);
    }
    calculate();
  }

  /**
   * Decrement the count, or remove entirely a particular product
   * @param productId The ID of the product to remove
   */
  public void removeProduct(String productId) {
    Iterator<CartItem> iterator = items.iterator();
    while (iterator.hasNext()) {
      CartItem cartItem = iterator.next();
      if (cartItem.getProduct().getProductId().equals(productId)) {
        if (cartItem.getQuantity() == 1) {
          iterator.remove();
          dbManager.deleteCartItem(cartItem.getProduct().getProductId());
        } else {
          cartItem.setQuantity(cartItem.getQuantity() - 1);
          dbManager.updateCartItem(cartItem);
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
    items.clear();
    calculate();
  }

  private void calculate() {
    BigDecimal newCost = ZERO;
    BigDecimal newTotal = ZERO;
    for (CartItem item : items) {
      Product product = item.getProduct();
      BigDecimal qty = item.getQuantity() == 1 ? BigDecimal.ONE : new BigDecimal(item.getQuantity());
      BigDecimal price = userManager.isPreferred()
          ? product.getFinalPrice() : product.getRegularPrice();
      BigDecimal priceWithTax = userManager.isPreferred()
          ? product.getFinalPriceWithTax() : product.getRegularPriceWithTax();
      newCost = newCost.add(price.multiply(qty));
      newTotal = newTotal.add(priceWithTax.multiply(qty));
    }
    cost = newCost;
    total = newTotal;
    tax = total.subtract(cost);
  }
}
