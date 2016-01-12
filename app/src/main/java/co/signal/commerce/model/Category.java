package co.signal.commerce.model;

/**
 * Created by jsokel on 1/12/16.
 */
public class Category {
  private final String categoryId;
  private final String parentId;
  private final String name;

  private Category(Builder builder) {
    this.categoryId = builder.categoryId;
    this.parentId = builder.parentId;
    this.name = builder.name;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public String getParentId() {
    return parentId;
  }

  public String getName() {
    return name;
  }

  public static final class Builder {
    private String categoryId;
    private String parentId;
    private String name;

    public Builder categoryId(String categoryId) {
      this.categoryId = categoryId;
      return this;
    }

    public Builder parentId(String parentId) {
      this.parentId = parentId;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }
  }
}
