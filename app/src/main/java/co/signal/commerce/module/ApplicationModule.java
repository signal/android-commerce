package co.signal.commerce.module;

import javax.inject.Named;
import javax.inject.Singleton;

import co.signal.commerce.CategoriesActivity;
import co.signal.commerce.MainActivity;
import co.signal.commerce.api.ApiManager;
import co.signal.commerce.api.CategoryParser;
import co.signal.commerce.api.ProductParser;
import dagger.Module;
import dagger.Provides;

@Module(
    library=true,
    injects = {
      MainActivity.class,
      CategoriesActivity.class
    }
)
public class ApplicationModule {
  private static final String BOUTIQUE_111_URL = "http://commerce.signal.ninja/api/rest/";

  @Provides @Named("API_URL")
  public String provideBoutique111Url() {
    return BOUTIQUE_111_URL;
  }

  @Provides
  public ProductParser provideProductParser() {
    return new ProductParser();
  }

  @Provides
  public CategoryParser provideCategoryParser() {
    return new CategoryParser();
  }

//  @Provides @Singleton
//  public ApiManager provideApiManager(@Named("API_URL") String baseUrl) {
//    return new ApiManager();
//  }
}
