package co.signal.commerce.db;

import javax.inject.Singleton;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB Helper to manage create and upgrades of the DB
 */
@Singleton
public class DBHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "commerce.db";
  private static final int CUR_VERSION = 1;
  private static final String CREATE_TABLE_CATEGORIES = "create table category (created integer, category_id text, parent_id text, name text)";
  private static final String CREATE_TABLE_PRODUCTS  = "create table product (created integer, category_id text, product_id text, title text, description text, price text, in_stock text)";
  private static final String CREATE_TABLE_CART  = "create table cart (created integer, product_id text, title text, price text, tax text, qty numeric)";

  public static final String CATEGORY_TABLE = "category";
  public static final String PRODUCT_TABLE = "product";
  public static final String CART_TABLE = "cart";

  public DBHelper(Context context) {
    super(context, DATABASE_NAME, null, CUR_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TABLE_CATEGORIES);
    db.execSQL(CREATE_TABLE_PRODUCTS);
    db.execSQL(CREATE_TABLE_CART);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
