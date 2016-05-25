package co.signal.commerce.module;

/**
 * Enum to centralize tracking data.
 */
public interface Tracking {
  // Main Types
  public static final String TRACK_VIEW = "trackView";
  public static final String TRACK_EVENT = "trackEvent";
  public static final String CATEGORY = "category";
  public static final String ACTION = "action";
  public static final String LABEL = "label";
  public static final String VALUE = "value";
  public static final String VIEW_NAME = "viewName";

  // Categories
  public static final String USER = "user";
  public static final String CLICK = "click";
  public static final String LOAD = "load";
  public static final String SHOP = "shop";
  public static final String PROFILE = "profile";

  // Click/Load types
  public static final String CATEGORIES = "categories";
  public static final String PRODUCT = "product";
  public static final String PRODUCTS = "products";
  public static final String DETAILS = "details";
  public static final String IMAGES = "images";
  public static final String MENU = "menu";
  public static final String RESULTS = "results";
  public static final String LOGIN = "login";

  // Shop Types
  public static final String CART_ADD = "cartAdd";
  public static final String CART_REMOVE = "cartRemove";
  public static final String PURCHASE = "purchase";
  public static final String FRAGMENT = "fragment";
  public static final String CHECKOUT_NEXT = "checkout_next";
  public static final String CHECKOUT_BACK = "checkout_back";
}
