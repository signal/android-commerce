package co.signal.commerce;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.signal.commerce.api.ApiManager;
import co.signal.commerce.model.Category;
import co.signal.util.SignalLogger;

public class CategoriesActivity extends BaseActivity {
  public static final String CATEGORY_ID = "categoryId";

  private LinearLayout categorylist;
  private String categoryId;

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
    categoryId = extras==null ? null : extras.getString(CATEGORY_ID);
    task.execute();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  private class RetrieveCategoriesTask extends AsyncTask<String, Void, List<Category>> {
    @Override
    protected List<Category> doInBackground(String ... id) {
      List<Category> result = null;
      try {
        if (categoryId == null) {
          result = apiManager.getMainCategories();
        } else {
          result = apiManager.getSubCategories(categoryId);
        }
      } catch (IOException e) {
        Log.e("commerce", "Retrieve Categories Failed for:" + categoryId, e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<Category> categories) {
      super.onPostExecute(categories);
      if (categories != null) {
        SignalLogger.df("category", "Retrieved %d categories from %s", categories.size(), categoryId);
        tracker.publish("load:categories",
            "type", categoryId == null ? "main" : "sub",
            "qty", String.valueOf(categories.size()),
            "categoryId", categoryId);
        for (final Category category : categories) {
          TextView view = new TextView(CategoriesActivity.this);
          view.setText(category.getName());
          view.setPadding(20, 20, 20, 20);
          view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent;
              String event;
              if (category.getChildren() > 0) {
                intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
                intent.putExtra(CATEGORY_ID, category.getCategoryId());
                event = "click:category";
              } else {
                intent = new Intent(CategoriesActivity.this, ProductsActivity.class);
                intent.putExtra(CATEGORY_ID, category.getCategoryId());
                event = "click:products";
              }
              startActivity(intent);
              tracker.publish(event, "categoryId", category.getCategoryId());
            }
          });
          categorylist.addView(view);
        }
      }
    }
  }
}
