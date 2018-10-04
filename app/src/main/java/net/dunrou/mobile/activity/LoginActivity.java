package net.dunrou.mobile.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.base.message.LoginMessage;
import net.dunrou.mobile.bean.BaseActivity;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private int flag = 0;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private Button mEmailSignInButton;

    private TextView mSignUpView;
    private TextView mRegisterView;

    private View mLoginOrRegisterView;
    private MaterialDialog materialDialog;

    private SharedPreferences userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        userInfo = getSharedPreferences("UserInfo", MODE_PRIVATE);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLoginOrRegister();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLoginOrRegister();
            }
        });

        mLoginOrRegisterView = findViewById(R.id.login_register);
        mLoginOrRegisterView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = flag == 0 ? 1 : 0;
                loginOrRegister();
                mEmailView.setText("");
                mPasswordView.setText("");
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        mSignUpView = findViewById(R.id.signup_switch);
        mRegisterView = findViewById(R.id.register_switch);
        loginOrRegister();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void loginOrRegister() {
        if (flag == 0) {
            mEmailSignInButton.setText(R.string.action_sign_in_short);
            mSignUpView.setTextColor(getResources().getColor(R.color.white));
            mSignUpView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mRegisterView.setTextColor(getResources().getColor(R.color.colorPrimary));
            mRegisterView.setBackgroundColor(getResources().getColor(R.color.backgroundNone));
            String username = userInfo.getString("username", null);
            if (username != null) {
                mEmailView.setText(username);
                mPasswordView.requestFocus();
            } else {
                mEmailView.requestFocus();
            }
        } else {
            mEmailSignInButton.setText(R.string.action_sign_up);
            mSignUpView.setTextColor(getResources().getColor(R.color.colorPrimary));
            mSignUpView.setBackgroundColor(getResources().getColor(R.color.backgroundNone));
            mRegisterView.setTextColor(getResources().getColor(R.color.white));
            mRegisterView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLoginOrRegister() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            if (password.length() < 6) {
                mPasswordView.setError(getString(R.string.error_too_short_password));
            } else if (password.length() > 16) {
                mPasswordView.setError(getString(R.string.error_too_long_password));
            }
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (flag == 0) {
                verification(email, password);
            } else {
                register(email, password);
            }
        }
    }

    private boolean isEmailValid(String email) {
        //Replace this with your own logic
        return email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+$");
    }

    private boolean isPasswordValid(String password) {
        //Replace this with your own logic
        return password.matches("^.{6,16}$");
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void verification(String email, String password) {
        materialDialog = new MaterialDialog.Builder(this)
                .title("Logging in account")
                .content("Please wait for a moment")
                .progress(true, 0)
                .show();
        new FirebaseUtil().getUser(new FirebaseUser(email, password));
    }

    private void register(String email, String password) {
        materialDialog = new MaterialDialog.Builder(this)
                .title("Registering account")
                .content("Please wait for a moment")
                .progress(true, 0)
                .show();
        new FirebaseUtil().UserInsert(new FirebaseUser(email, password));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginMessage(LoginMessage loginMessage) {
        materialDialog.dismiss();
        mEmailView.setError(null);
        mPasswordView.setError(null);

        if (loginMessage.isSuccess()) {
            if (loginMessage.getStatus() == 0) {
                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = userInfo.edit();
                editor.putString("username", loginMessage.getUserID()).apply();
                flag = 0;
                loginOrRegister();
            } else {
                SharedPreferences.Editor editor = userInfo.edit();
                editor.putString("username", loginMessage.getUserID()).apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            if (loginMessage.getStatus() == 1) {
                Toast.makeText(this, "Username or password is not correct!", Toast.LENGTH_LONG).show();
            } else {
                if (loginMessage.getUserID().equals("")) {
                    Toast.makeText(this, "Please try again later!", Toast.LENGTH_LONG).show();
                } else {
                    mEmailView.setError(getString(R.string.error_username_exists));
                    mEmailView.requestFocus();
                }
            }
        }
    }
}

