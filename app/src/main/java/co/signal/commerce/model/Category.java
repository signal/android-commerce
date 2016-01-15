package co.signal.commerce.model;

/**
 * Product Category
 */
public class Category {
  private final String categoryId;
  private final String parentId;
  private final String name;
  private final int children;

  private Category(Builder builder) {
    this.categoryId = builder.categoryId;
    this.parentId = builder.parentId;
    this.name = builder.name;
    this.children = builder.children;
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

  public int getChildren() {
    return children;
  }

  public static final class Builder {
    private String categoryId;
    private String parentId;
    private String name;
    private int children;

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

    public Builder children(int children) {
      this.children = children;
      return this;
    }

    public Category build() {
      return new Category(this);
    }
  }
}
