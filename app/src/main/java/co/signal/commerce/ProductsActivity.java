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
import co.signal.commerce.view.ProductListView;
import co.signal.util.SignalLogger;

public class ProductsActivity extends BaseActivity {
  private static final String PRODUCT_ID = "productId";

  private LinearLayout productList;
  private String categoryId;
  private String categoryTitle;

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
    categoryId = getIntent().getExtras().getString(CategoriesActivity.CATEGORY_ID);
    categoryTitle = getIntent().getExtras().getString(CategoriesActivity.CATEGORY_TITLE);
    task.execute();
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();

    if (categoryTitle != null) {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      toolbar.setTitle(categoryTitle);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  private class RetrieveProductsTask extends AsyncTask<Void, Void, List<Product>> {
    @Override
    protected List<Product> doInBackground(Void ... v) {
      List<Product> result = null;
      try {
        result = apiManager.getProducts(categoryId);
      } catch (IOException e) {
        Log.e("commerce", "Retrieve Products Failed", e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<Product> products) {
      super.onPostExecute(products);
      if (products != null) {
        SignalLogger.df("products", "Retrieved %d products from %s", products.size(), categoryId);
        tracker.publish("load:products",
            "qty", String.valueOf(products.size()),
            "categoryId", categoryId);

        for (final Product product : products) {
          ProductListView view = new ProductListView(ProductsActivity.this);
          view.setTitleText(product.getTitle());
          view.setThumbnailUrl(product.getThumbnailUrl());
          view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(ProductsActivity.this, ProductsActivity.class);
              intent.putExtra(PRODUCT_ID, product.getProductId());
              startActivity(intent);
              tracker.publish("click:product", "productId", product.getProductId());
            }
          });
          productList.addView(view);
        }
      }
    }
  }
}
