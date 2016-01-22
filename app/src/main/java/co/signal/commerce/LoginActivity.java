package co.signal.commerce;

import javax.inject.Inject;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.androidquery.AQuery;

import co.signal.serverdirect.api.Hashes;
import co.signal.serverdirect.api.Tracker;

public class LoginActivity extends Activity {
  @Inject
  Tracker tracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((CommerceApplication)getApplication()).inject(this);
    tracker.publish("view:LoginActivity");

    setContentView(R.layout.activity_login);
    final AQuery aq = new AQuery(this);

    EditText emailText = (EditText)findViewById(R.id.login_email);
    emailText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) { }

      @Override
      public void afterTextChanged(Editable s) {
        boolean valid = isValidEmail(s.toString()) &&
            !TextUtils.isEmpty(aq.id(R.id.login_password).getText().toString());
        aq.id(R.id.btn_login).enabled(valid);
      }
    });

    EditText passText = (EditText)findViewById(R.id.login_password);
    passText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) { }

      @Override
      public void afterTextChanged(Editable s) {
        boolean valid = isValidEmail(aq.id(R.id.login_email).getText().toString()) &&
            !TextUtils.isEmpty(s.toString());
        aq.id(R.id.btn_login).enabled(valid);
      }
    });

    aq.id(R.id.btn_login).clicked(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        tracker.addCustomField("hashed-email-sha256", Hashes.sha256(aq.id(R.id.login_email).getText().toString()));
        tracker.publish("click:login"); // will include hashed email just added
        finish();
      }
    });

    aq.id(R.id.btn_cancel).clicked(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        tracker.publish("click:cancel_login");
        finish();
      }
    });
  }

  private static boolean isValidEmail(String target) {
    if (TextUtils.isEmpty(target)) {
      return false;
    }
    //android Regex to check the email address Validation
    return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
  }
}