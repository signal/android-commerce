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
import co.signal.commerce.db.DBManager;
import co.signal.commerce.model.Category;
import co.signal.commerce.view.CategoryView;
import co.signal.util.SignalLogger;

import static co.signal.commerce.module.Tracking.*;

public class CategoriesActivity extends BaseActivity {
  public static final String CATEGORY_ID = "categoryId";
  public static final String CATEGORY_TITLE = "categoryTitle";

  private LinearLayout categorylist;
  private String categoryId = null;
  private String categoryTitle;

  @Inject
  ApiManager apiManager;
  @Inject
  DBManager dbManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_categories);
    categorylist = (LinearLayout)findViewById(R.id.category_list);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    RetrieveCategoriesTask task = new RetrieveCategoriesTask();
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      categoryId = extras.getString(CATEGORY_ID);
      categoryTitle = extras.getString(CATEGORY_TITLE);
    }
    task.execute();
  }

  @Override
  protected void onPostResume() {
    super.onPostResume();
    showSdkStatus();
    if (categoryTitle != null) {
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      toolbar.setTitle(categoryTitle);
    }
  }

  private class RetrieveCategoriesTask extends AsyncTask<Void, Void, List<Category>> {
    @Override
    protected List<Category> doInBackground(Void ... v) {
      List<Category> result = null;
      try {
        dbManager.deleteCategories();
        if (categoryId == null) {
          result = apiManager.getMainCategories();
        } else {
          result = apiManager.getSubCategories(categoryId);
        }
      } catch (Exception e) {
        Log.e("commerce", "Retrieve Categories Failed for:" + categoryId, e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<Category> categories) {
      super.onPostExecute(categories);
      if (categories == null) {
        Toast.makeText(CategoriesActivity.this,
            "No categories were found... please try again.",
            Toast.LENGTH_LONG)
            .show();
        return;
      }

      SignalLogger.df("category", "Retrieved %d categories from %s", categories.size(), categoryId);
      trackerWrapper.trackEvent(LOAD, CATEGORIES, RESULTS, categories.size(),
          "categoryType", categoryId == null ? "main" : "sub");

      dbManager.saveCategories(categories);

      for (final Category category : categories) {
        CategoryView categoryView = new CategoryView(CategoriesActivity.this, null);
        categoryView.setTitleText(category.getName());

        categoryView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent;
            if (category.getChildren() > 0) {
              intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
              trackerWrapper.trackEvent(CLICK, CATEGORY, "main", null);
            } else {
              intent = new Intent(CategoriesActivity.this, ProductsActivity.class);
              trackerWrapper.trackEvent(CLICK, CATEGORY, "sub", null, "categoryId", categoryId);
            }
            intent.putExtra(CATEGORY_ID, category.getCategoryId());
            intent.putExtra(CATEGORY_TITLE, category.getName());
            startActivity(intent);
          }
        });
        categorylist.addView(categoryView);
      }
    }
  }
}
