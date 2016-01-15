package co.signal.commerce;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.signal.commerce.api.ApiManager;
import co.signal.commerce.model.Category;
import co.signal.commerce.model.Product;

public class ProductsActivity extends BaseActivity {
  private static final String PRODUCT_ID = "productId";

  LinearLayout productList;

  @Inject
  ApiManager apiManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_products);
    productList = (LinearLayout)findViewById(R.id.product_list);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    RetrieveProductsTask task = new RetrieveProductsTask();
    task.execute(getIntent().getExtras().getString(CategoriesActivity.CATEGORY_ID));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  private class RetrieveProductsTask extends AsyncTask<String, Void, List<Product>> {
    @Override
    protected List<Product> doInBackground(String ... id) {
      List<Product> result = null;
      try {
        result = apiManager.getProducts(id[0]);
      } catch (IOException e) {
        Log.e("commerce", "Retrieve Products Failed", e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<Product> products) {
      super.onPostExecute(products);
      if (products != null) {
        tracker.publish("load:product", "type", "main", "qty", String.valueOf(products.size()));
        Log.d("commerce", "Retrieved " + products.size() + " product");
        for (final Product product : products) {
          TextView view = new TextView(ProductsActivity.this);
          view.setText(product.getTitle());
          view.setPadding(20, 20, 20, 20);
          view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(ProductsActivity.this, ProductsActivity.class);
              intent.putExtra(PRODUCT_ID, product.getProductId());
              startActivity(intent);
            }
          });
          productList.addView(view);
        }
      }
    }
  }
}
