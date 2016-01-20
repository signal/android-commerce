package co.signal.commerce.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import co.signal.commerce.R;

public class ProductListView extends LinearLayout {

  private TextView titleText;
  private AQuery aq;

  public ProductListView(Context context) {
    this(context, null);
  }

  public ProductListView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.product_list_item, this);

    aq = new AQuery(this);
    titleText = (TextView)findViewById(R.id.product_text);
  }

  public void setTitleText(String title) {
    titleText.setText(title);
  }

  public void setThumbnailUrl(String url) {
    aq.id(R.id.product_thumbnail).image(url, true, true);
  }
}
