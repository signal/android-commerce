package co.signal.commerce;

import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import co.signal.commerce.api.ApiManager;
import co.signal.commerce.api.UserManager;
import co.signal.commerce.db.DBManager;
import co.signal.commerce.model.Product;
import co.signal.commerce.view.ProductListView;
import co.signal.util.SignalLogger;

public class ProductsActivity extends BaseActivity {
  public static final String PRODUCT_ID = "productId";
  public static final String PRODUCT_TITLE = "productTitle";

  private LinearLayout productList;
  private String categoryId;
  private String categoryTitle;

  @Inject
  ApiManager apiManager;
  @Inject
  DBManager dbManager;
  @Inject
  UserManager userManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_products);
    productList = (LinearLayout)findViewById(R.id.product_list);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

//    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//    fab.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//            .setAction("Action", null).show();
//      }
//    });
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

  private void drawProducts(List<Product> products) {
    for (final Product product : products) {
      ProductListView view = new ProductListView(ProductsActivity.this);
      view.setProduct(product, userManager.isPreferred());
      view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(ProductsActivity.this, ProductDetailsActivity.class);
          intent.putExtra(PRODUCT_ID, product.getProductId());
          intent.putExtra(PRODUCT_TITLE, categoryTitle);
          startActivity(intent);
          tracker.publish("click:product", "productId", product.getProductId());
        }
      });
      productList.addView(view);
    }
  }

  private class RetrieveProductsTask extends AsyncTask<Void, Void, List<Product>> {
    @Override
    protected List<Product> doInBackground(Void ... v) {
      List<Product> result = null;
      try {
        dbManager.deleteProducts(categoryId);
        result = apiManager.getProducts(categoryId);
      } catch (Exception e) {
        Log.e("commerce", "Retrieve Products Failed", e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<Product> products) {
      super.onPostExecute(products);
      if (products == null) {
        Toast.makeText(ProductsActivity.this,
            "Failed to load product list... please try again.",
            Toast.LENGTH_LONG)
            .show();
        return;
      }
      SignalLogger.df("products", "Retrieved %d products from %s", products.size(), categoryId);
      tracker.publish("load:products",
          "qty", String.valueOf(products.size()),
          "categoryId", categoryId);

      dbManager.saveProducts(categoryId, products);
      drawProducts(products);
    }
  }
}
