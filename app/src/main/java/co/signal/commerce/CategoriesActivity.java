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

public class CategoriesActivity extends BaseActivity {
  public static final String CATEGORY_ID = "categoryId";

  LinearLayout categorylist;

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
    task.execute(extras!=null ? extras.getString(CATEGORY_ID) : null);
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
        if (id == null || id.length == 0) {
          result = apiManager.getMainCategories();
        } else {
          result = apiManager.getSubCategories(id[0]);
        }
      } catch (IOException e) {
        Log.e("commerce", "Retrieve Categories Failed", e);
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<Category> categories) {
      super.onPostExecute(categories);
      if (categories != null) {
        tracker.publish("load:categories", "type", "main", "qty", String.valueOf(categories.size()));
        Log.d("commerce", "Retrieved " + categories.size() + " categories");
        for (final Category category : categories) {
          TextView view = new TextView(CategoriesActivity.this);
          view.setText(category.getName());
          view.setPadding(20, 20, 20, 20);
          view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent;
              if (category.getChildren() > 0) {
                intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
                intent.putExtra(CATEGORY_ID, category.getCategoryId());
              } else {
                intent = new Intent(CategoriesActivity.this, ProductsActivity.class);
                intent.putExtra(CATEGORY_ID, category.getCategoryId());
              }
              startActivity(intent);
            }
          });
          categorylist.addView(view);
        }
      }
    }
  }
}
