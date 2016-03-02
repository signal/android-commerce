package co.signal.commerce.db;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import co.signal.commerce.model.CartItem;
import co.signal.commerce.model.Category;
import co.signal.commerce.model.Product;
import co.signal.util.SignalLogger;

/**
 * Event store for logging requests and response API calls.  This data is not actually used by
 * the commerce app, but it ensures the app is doing a decent amount of DB activity along side
 * the SDK so proper testing and usage analysis can be done.
 */
@Singleton
public class DBManager {
  @Inject
  DBHelper dbHelper;

  public void deleteCategories() {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      database.delete(DBHelper.CATEGORY_TABLE, null, null);
      database.setTransactionSuccessful();
      SignalLogger.d(SignalLogger.STORE_TAG, "Deleted Categories");
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure deleting category data", e);
    } finally {
      database.endTransaction();
    }
  }

  public void saveCategories(List<Category> categories) {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      String now = String.valueOf(System.currentTimeMillis());
      for (Category category : categories) {
        ContentValues cv = new ContentValues();
        cv.put("created", now);
        cv.put("category_id", category.getCategoryId());
        cv.put("parent_id", category.getParentId());
        cv.put("name", category.getName());
        database.insert(DBHelper.CATEGORY_TABLE, null, cv);
      }
      database.setTransactionSuccessful();
      SignalLogger.df(SignalLogger.STORE_TAG, "Saved Categories | %d", categories.size());
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure saving category data", e);
    } finally {
      database.endTransaction();
    }
  }

  public void deleteProducts(String categoryId) {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      database.delete(DBHelper.PRODUCT_TABLE, "category_id=?", new String[]{categoryId});
      database.setTransactionSuccessful();
      SignalLogger.df(SignalLogger.STORE_TAG, "Deleted Products | %s", categoryId);
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure deleting product data", e);
    } finally {
      database.endTransaction();
    }
  }

  public void saveProducts(String categoryId, List<Product> products) {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      String now = String.valueOf(System.currentTimeMillis());
      for (Product product : products) {
        ContentValues cv = new ContentValues();
        cv.put("created", now);
        cv.put("category_id", categoryId);
        cv.put("product_id", product.getProductId());
        cv.put("title", product.getTitle());
        cv.put("description", product.getDescription());
        cv.put("price", product.getRegularPrice().toPlainString());
        cv.put("in_stock", product.isInStock());
        database.insert(DBHelper.PRODUCT_TABLE, null, cv);
      }
      database.setTransactionSuccessful();
      SignalLogger.df(SignalLogger.STORE_TAG, "Saved Products | %d", products.size());
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure saving product data", e);
    } finally {
      database.endTransaction();
    }
  }

  public void deleteCart() {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      database.delete(DBHelper.CART_TABLE, null, null);
      database.setTransactionSuccessful();
      SignalLogger.d(SignalLogger.STORE_TAG, "Deleted Cart");
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure deleting cart data", e);
    } finally {
      database.endTransaction();
    }
  }

  public void deleteCartItem(String productId) {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      database.delete(DBHelper.CART_TABLE, "product_id=?", new String[]{productId});
      database.setTransactionSuccessful();
      SignalLogger.df(SignalLogger.STORE_TAG, "Deleted CartItem | %s", productId);
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure deleting cart data", e);
    } finally {
      database.endTransaction();
    }
  }

  public void saveCartItem(CartItem cartItem) {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      ContentValues cv = new ContentValues();
      cv.put("created", String.valueOf(System.currentTimeMillis()));
      cv.put("product_id", cartItem.getProduct().getProductId());
      cv.put("title", cartItem.getProduct().getTitle());
      cv.put("price", cartItem.getProduct().getRegularPrice().toPlainString());
      BigDecimal tax = cartItem.getProduct().getRegularPriceWithTax()
          .subtract(cartItem.getProduct().getRegularPrice());
      cv.put("price", tax.toPlainString());
      cv.put("qty", cartItem.getQuantity());
      database.insert(DBHelper.CART_TABLE, null, cv);
      database.setTransactionSuccessful();
      SignalLogger.df(SignalLogger.STORE_TAG, "Saved CartItem | %s", cartItem.getProduct().getProductId());
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure saving cart data", e);
    } finally {
      database.endTransaction();
    }
  }

  public void updateCartItem(CartItem cartItem) {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
    database.beginTransaction();
    try {
      ContentValues cv = new ContentValues();
      cv.put("qty", cartItem.getQuantity());
      int id = database.update(DBHelper.CART_TABLE, cv, "product_id=?", new String[]{cartItem.getProduct().getProductId()});
      if (id != 1) {
        SignalLogger.ef(SignalLogger.STORE_TAG, "No cart records updated | %d", cartItem.getProduct().getProductId());
      }
      database.setTransactionSuccessful();
      SignalLogger.df(SignalLogger.STORE_TAG, "Updated CartItem | %s | %d",
          cartItem.getProduct().getProductId(), cartItem.getQuantity());
    } catch (Exception e) {
      SignalLogger.e(SignalLogger.STORE_TAG, "Failure saving cart data", e);
    } finally {
      database.endTransaction();
    }
  }
}
