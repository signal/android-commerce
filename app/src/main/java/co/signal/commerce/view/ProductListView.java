package co.signal.commerce.view;

import java.math.BigDecimal;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.androidquery.AQuery;

import co.signal.commerce.R;
import co.signal.commerce.model.Product;

public class ProductListView extends LinearLayout {

  public ProductListView(Context context) {
    this(context, null);
  }

  public ProductListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.product_list_item, this);
  }

  public void setProduct(Product product, boolean preferred) {
    AQuery aq = new AQuery(this);
    aq.id(R.id.product_text).text(product.getTitle());
    aq.id(R.id.product_thumbnail).image(product.getThumbnailUrl(), true, true);
    BigDecimal price = preferred ? product.getFinalPrice() : product.getRegularPrice();
    aq.id(R.id.product_price).text("$" + price.toPlainString());
  }
}
