package co.signal.commerce;

import java.util.List;

import javax.inject.Inject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.AQuery;

import co.signal.commerce.api.ApiManager;
import co.signal.commerce.model.Product;
import co.signal.util.SignalLogger;

public class ProductDetailsActivity extends BaseActivity {
  private AQuery aq;
  private String productId;
  private String productTitle;
  private LinearLayout productImages;

  @Inject
  ApiManager apiManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_product_details);
    productImages = (LinearLayout)findViewById(R.id.product_images);

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

    productId = getIntent().getExtras().getString(ProductsActivity.PRODUCT_ID);
    productTitle = getIntent().getExtras().getString(ProductsActivity.PRODUCT_TITLE);

    new RetrieveProductDetailsTask().execute();
    new RetrieveProductImagesTask().execute();
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();

    if (productTitle != null) {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      toolbar.setTitle(productTitle);
    }
  }

  private void drawView(Product product) {
    aq = new AQuery(this);
    aq.id(R.id.product_image).image(product.getImageUrl(), false, true, 200, 0);
    aq.id(R.id.product_title).text(product.getTitle());
    aq.id(R.id.product_description).text(product.getDescription());
    aq.id(R.id.product_details).text(product.getDetails());
    aq.id(R.id.product_price).text("$" + product.getFinalPrice()).visible();
    aq.id(product.isInStock() ? R.id.product_in_stock : R.id.product_out_of_stock).visible();
  }

  private void drawImages(List<String> imageUrls) {
    boolean first = true;
    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

    for (String url : imageUrls) {
      if (first) {
        // The first image is the same as the one in the Product class
        first = false;
      } else {
        ImageView img = new ImageView(this);
        img.setLayoutParams(layoutParams);
        aq.id(img).image(url, false, true, 200, 0);
        productImages.addView(img);
      }
    }
  }

  private class RetrieveProductDetailsTask extends AsyncTask<Void, Void, Product> {
    @Override
    protected Product doInBackground(Void ... v) {
      Product result = null;
      try {
        result = apiManager.getProductDetails(productId);
      } catch (Exception e) {
        Log.e("product", "Retrieve Product Details Failed", e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(Product product) {
      super.onPostExecute(product);
      if (product != null) {
        SignalLogger.df("product", "Retrieved product from %s (%s)", productId, productTitle);
        tracker.publish("load:productDetails", "productId", productId);
        drawView(product);
      } else {
        Toast.makeText(ProductDetailsActivity.this,
            "Failed to load Product Details... please try again.",
            Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  private class RetrieveProductImagesTask extends AsyncTask<Void, Void, List<String>> {
    @Override
    protected List<String> doInBackground(Void ... v) {
      List<String> result = null;
      try {
        result = apiManager.getProductImages(productId);
      } catch (Exception e) {
        Log.e("product", "Retrieve Product Images Failed", e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<String> imageUrls) {
      super.onPostExecute(imageUrls);
      if (imageUrls == null) {
        Toast.makeText(ProductDetailsActivity.this,
            "Failed to load Product images... please try again.",
            Toast.LENGTH_LONG)
            .show();
        return;
      }

      SignalLogger.df("product", "Retrieved %d images from %s (%s)", imageUrls.size(), productId, productTitle);
      tracker.publish("load:productImages", "productId", productId, "qty", String.valueOf(imageUrls.size()));
      drawImages(imageUrls);
    }
  }
}
