package co.signal.commerce.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.signal.commerce.R;

public class CategoryView extends LinearLayout {

  private TextView letterText;
  private TextView titleText;

  public CategoryView(Context context) {
    this(context, null);
  }

  public CategoryView(Context context, AttributeSet attrs) {
    super(context, attrs);
    inflate(context, R.layout.category, this);
    letterText = (TextView)findViewById(R.id.category_letter);
    titleText = (TextView)findViewById(R.id.category_text);
  }

  public void setTitleText(String title) {
    String letter = title.substring(0, 1);
    letterText.setText(letter);
    titleText.setText(title);
  }

  public void setColor(String color) {

  }
}
