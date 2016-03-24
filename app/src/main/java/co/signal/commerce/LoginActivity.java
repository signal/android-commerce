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

import co.signal.commerce.api.UserManager;
import co.signal.serverdirect.api.Tracker;

public class LoginActivity extends Activity {
  @Inject
  Tracker tracker;
  @Inject
  UserManager userManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((CommerceApplication)getApplication()).inject(this);
    tracker.publish("view:LoginActivity");

    setContentView(R.layout.activity_login);
    final AQuery aq = new AQuery(this);

    // Track changes on email, ensure password has a value
    EditText emailText = (EditText)findViewById(R.id.login_email);
    emailText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) { }

      @Override
      public void afterTextChanged(Editable s) {
        boolean valid = UserManager.isValidEmail(s.toString()) &&
            !TextUtils.isEmpty(aq.id(R.id.login_password).getText().toString());
        aq.id(R.id.btn_login).enabled(valid);
      }
    });

    // Track changes on password, ensure email has a value
    EditText passText = (EditText)findViewById(R.id.login_password);
    passText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) { }

      @Override
      public void afterTextChanged(Editable s) {
        boolean valid = UserManager.isValidEmail(aq.id(R.id.login_email).getText().toString()) &&
            !TextUtils.isEmpty(s.toString());
        aq.id(R.id.btn_login).enabled(valid);
      }
    });

    aq.id(R.id.btn_login).clicked(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String email = aq.id(R.id.login_email).getText().toString();
        String pwd = aq.id(R.id.login_password).getText().toString();
        userManager.userLogin(email, pwd);
        tracker.publish("event:login");
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
}
