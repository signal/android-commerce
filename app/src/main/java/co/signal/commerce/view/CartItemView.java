package co.signal.commerce.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.androidquery.AQuery;

import co.signal.commerce.R;
import co.signal.commerce.model.CartItem;
import co.signal.commerce.model.Product;

public class CartItemView extends LinearLayout {

  public CartItemView(Context context) {
    this(context, null);
  }

  public CartItemView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.cart_list_item, this);
  }

  public void setCartItem(final CartItem cartItem) {
    AQuery aq = new AQuery(this);
    Product product = cartItem.getProduct();
    aq.id(R.id.product_text).text(product.getTitle());
    aq.id(R.id.product_price).text("$" + product.getFinalPrice().toPlainString());
    aq.id(R.id.product_thumbnail).image(product.getThumbnailUrl(), true, true);
    aq.id(R.id.cart_qty).text(String.valueOf(cartItem.getQuantity()));
  }
}
