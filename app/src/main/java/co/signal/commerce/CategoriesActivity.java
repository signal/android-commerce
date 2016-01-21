package co.signal.commerce;

import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import co.signal.commerce.api.ApiManager;
import co.signal.commerce.model.Category;
import co.signal.commerce.view.CategoryView;
import co.signal.util.SignalLogger;

public class CategoriesActivity extends BaseActivity {
  public static final String CATEGORY_ID = "categoryId";
  public static final String CATEGORY_TITLE = "categoryTitle";

  private LinearLayout categorylist;
  private String categoryId = null;
  private String categoryTitle;

  @Inject
  ApiManager apiManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_categories);
    categorylist = (LinearLayout)findViewById(R.id.category_list);

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
      tracker.publish("load:categories",
          "type", categoryId == null ? "main" : "sub",
          "qty", String.valueOf(categories.size()),
          "categoryId", categoryId);

      for (final Category category : categories) {
        CategoryView categoryView = new CategoryView(CategoriesActivity.this, null);
        categoryView.setTitleText(category.getName());

        categoryView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Intent intent;
            if (category.getChildren() > 0) {
              intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
              tracker.publish("click:category", "type", "main");
            } else {
              intent = new Intent(CategoriesActivity.this, ProductsActivity.class);
              tracker.publish("click:category", "type", "sub", "categoryId", category.getCategoryId());
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
