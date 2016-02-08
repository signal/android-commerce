package co.signal.commerce;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import co.signal.commerce.model.CartItem;
import co.signal.commerce.view.CartItemView;

public class PurchaseActivity extends BaseActivity {
  private LinearLayout cartList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_purchase);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    cartList = (LinearLayout) findViewById(R.id.cart_item_list);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    drawCart();
  }

  private void drawCart() {
    for (final CartItem cartItem : cart.getItems()) {
      final CartItemView cartItemView = new CartItemView(this, null);
      cartItemView.setCartItem(cartItem);
      cartList.addView(cartItemView);

      // clicks need to be here since they need access to the parent views
      cartItemView.findViewById(R.id.cart_add).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          cartItem.getCart().addProduct(cartItem.getProduct());
          updateQty(cartItemView, cartItem);
        }
      });
      cartItemView.findViewById(R.id.cart_remove).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
          int qty = cartItem.getQuantity();
          if (qty == 1) {
            Snackbar.make(view, cartItem.getProduct().getTitle() + " removed from cart.", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
            cartList.removeView(cartItemView);
          } else {
            cartItem.getCart().removeProduct(cartItem.getProduct().getProductId());
            updateQty(cartItemView, cartItem);
          }
        }
      });
    }
  }

  private void updateQty(View view, CartItem item) {
    ((TextView)view.findViewById(R.id.cart_qty)).setText(String.valueOf(item.getQuantity()));
  }
}
