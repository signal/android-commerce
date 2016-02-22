package co.signal.commerce;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import co.signal.commerce.api.UserManager;
import co.signal.commerce.model.Cart;
import co.signal.commerce.model.CartItem;
import co.signal.commerce.view.CartItemView;

public class CheckoutActivity extends BaseActivity {

  private String orderNum = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_checkout);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // However, if we're being restored from a previous state,
    // then we don't need to do anything and should return or else
    // we could end up with overlapping fragments.
    if (savedInstanceState != null) {
      return;
    }

    // Create a new Fragment to be placed in the activity layout
    CartFragment firstFragment = new CartFragment();
    //firstFragment.setArguments(getIntent().getExtras());

    // Add the fragment to the 'fragment_container' FrameLayout
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.fragment_container, firstFragment)
        .commit();
  }

  /**
   * The base fragment for all others since they all contain the totals
   */
  public static class TotalsFragment extends Fragment {
    AQuery aq;

    void created(View view) {
      aq = new AQuery(view);
    }

    /**
     * Convenience for all the fragment to get activity objects
     * @return The current activity cast to BaseActivity
     */
    CheckoutActivity activity() {
      return (CheckoutActivity)getActivity();
    }

    public void updateTotals() {
      final Cart cart = activity().getCart();

      aq.id(R.id.label_items).text("Items (" + cart.getItemCount() + "):");
      aq.id(R.id.cart_subtotal).text("$" + cart.getCost().toPlainString());
      aq.id(R.id.cart_tax).text("$" + cart.getTax().toPlainString());
      aq.id(R.id.cart_shipping).text("$0.00");
      aq.id(R.id.cart_total).text("$" + cart.getTotal().toPlainString());
    }
  }

  /**
   * The Shopping Cart fragment containing the list of itesm.
   */
  public static class CartFragment extends TotalsFragment {
    private LinearLayout cartList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_cart, container, false);
      created(view);
      cartList = (LinearLayout)view.findViewById(R.id.cart_item_list);
      aq.id(R.id.btn_prev).enabled(false);

      aq.id(R.id.btn_next).clicked(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Fragment nextFragment = activity().userManager.isLoggedIn()
              ? new PlaceOrderFragment()
              : new LoginFragment();
          activity().getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container, nextFragment)
              .commit();
          activity().tracker.publish("click:next", "fragment", nextFragment.getClass().getSimpleName());
        }
      });

      updateTotals();
      drawCart();
      return view;
    }

    private void drawCart() {
      final Cart cart = activity().getCart();
      for (final CartItem cartItem : cart.getItems()) {
        final CartItemView cartItemView = new CartItemView(getContext(), null);
        cartItemView.setCartItem(cartItem);
        cartList.addView(cartItemView);
        cartItemView.findViewById(R.id.cart_add).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (cartItem.getQuantity() < 9) {
              cart.addProduct(cartItem.getProduct());
              updateQty(cartItemView, cartItem);
              updateTotals();
              activity().tracker.publish("click:cart_add", "productId", cartItem.getProduct().getProductId());
            }
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
              cart.removeProduct(cartItem.getProduct().getProductId());
              cartList.removeView(cartItemView);
            } else {
              cart.removeProduct(cartItem.getProduct().getProductId());
              updateQty(cartItemView, cartItem);
            }
            updateTotals();
            activity().tracker.publish("click:cart_remove", "productId", cartItem.getProduct().getProductId());
          }
        });
      }
    }

    private void updateQty(View view, CartItem item) {
      ((TextView)view.findViewById(R.id.cart_qty)).setText(String.valueOf(item.getQuantity()));
    }
  }

  public static class LoginFragment extends TotalsFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_login, container, false);
      created(view);
      updateTotals();

      aq.id(R.id.btn_prev).clicked(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          activity().getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container, new CartFragment())
              .commit();
          activity().tracker.publish("click:back", "fragment", CartFragment.class.getSimpleName());
        }
      });

      aq.id(R.id.btn_login).clicked(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          aq.id(R.id.btn_login).enabled(false);
          aq.id(R.id.btn_prev).enabled(false);

          String email = aq.id(R.id.login_email).getText().toString();
          String pwd = aq.id(R.id.login_password).getText().toString();
          activity().userManager.userLogin(email, pwd);
          activity().tracker.publish("click:login", "fromCart", "true");
          activity().invalidateOptionsMenu();

          // Add a small delay here to mimic an actual login
          new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              activity().getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.fragment_container, new PlaceOrderFragment())
                  .commit();

            }
          }, 750);
        }
      });

      // Track changes on email, ensure password has a value
      EditText emailText = (EditText)view.findViewById(R.id.login_email);
      emailText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
          boolean valid = UserManager.isValidEmail(s.toString()) &&
              !TextUtils.isEmpty(aq.id(R.id.login_password).getText().toString());
          aq.id(R.id.btn_login).enabled(valid);
        }
      });

      // Track changes on password, ensure email has a value
      EditText passText = (EditText)view.findViewById(R.id.login_password);
      passText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
          boolean valid = UserManager.isValidEmail(aq.id(R.id.login_email).getText().toString()) &&
              !TextUtils.isEmpty(s.toString());
          aq.id(R.id.btn_login).enabled(valid);
        }
      });

      return view;
    }
  }

  public static class PlaceOrderFragment extends TotalsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_place_order, container, false);
      created(view);
      updateTotals();

      aq.id(R.id.btn_prev).clicked(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          activity().getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container, new CartFragment())
              .commit();
          activity().tracker.publish("click:back", "fragment", CartFragment.class.getSimpleName());
        }
      });
      aq.id(R.id.btn_purchase).clicked(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Cart cart = activity().cart;
          activity().orderNum = String.valueOf(System.currentTimeMillis()).substring(5);
          activity().tracker.publish("click:purchase",
              "total", cart.getTotal().toPlainString(),
              "tax", cart.getTax().toPlainString(),
              "shipping", "0.00", // Just get a value in there for now
              "numItems", String.valueOf(cart.getItemCount()),
              "orderNum", activity().orderNum // a pseudo sequence number
          );
          aq.id(R.id.btn_prev).enabled(false);
          aq.id(R.id.btn_purchase).enabled(false);

          new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              activity().getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.fragment_container, new ConfirmFragment())
                  .commit();
            }
          }, 1000);
        }
      });

      return view;
    }
  }

  public static class ConfirmFragment extends TotalsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_confirm, container, false);
      created(view);
      activity().cart.clear();
      activity().invalidateOptionsMenu();

      aq.id(R.id.confirm_ordernum).text(activity().orderNum);
      aq.id(R.id.btn_confirm).clicked(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          Intent intent = new Intent(getContext(), MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
          activity().tracker.publish("click:confirm");
        }
      });

      return view;
    }
  }
}
