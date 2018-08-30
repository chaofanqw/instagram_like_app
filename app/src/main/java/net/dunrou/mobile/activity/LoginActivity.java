package net.dunrou.mobile.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.dunrou.mobile.R;
import net.dunrou.mobile.bean.BaseActivity;
import net.dunrou.mobile.network.HttpResult;
import net.dunrou.mobile.network.InsNetwork;
import net.dunrou.mobile.network.InsService;
import net.dunrou.mobile.network.RetrofitUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.READ_CONTACTS;

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

    private TextView mSignupView;
    private TextView mRegisterView;

    private View mLoginOrRegisterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
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
                flag = flag == 0 ? 1:0;
                loginOrRegister();
                mEmailView.setText("");
                mPasswordView.setText("");
            }
        });

        mProgressView = findViewById(R.id.login_progress);
        mSignupView = findViewById(R.id.signup_switch);
        mRegisterView = findViewById(R.id.register_switch);
    }

    private void loginOrRegister(){
        if (flag == 0){
            mEmailSignInButton.setText(R.string.action_sign_in_short);
            mSignupView.setTextColor(getResources().getColor(R.color.white));
            mSignupView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mRegisterView.setTextColor(getResources().getColor(R.color.colorPrimary));
            mRegisterView.setBackgroundColor(getResources().getColor(R.color.backgroundNone));
        }else{
            mEmailSignInButton.setText(R.string.action_sign_up);
            mSignupView.setTextColor(getResources().getColor(R.color.colorPrimary));
            mSignupView.setBackgroundColor(getResources().getColor(R.color.backgroundNone));
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
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
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
//            if (flag == 0) {
//                vertifcation(email, password);
//            }else{
//                register(email, password);
//            }
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            test();
        }
    }

    private boolean isEmailValid(String email) {
        //Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //Replace this with your own logic
        return password.length() > 4;
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

    private void vertifcation(String email, String password){

        Observable login = InsService.getInstance().getInsNetwork().login(email, password);

        Observer<HttpResult<String>> deal = new Observer<HttpResult<String>>() {

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                Log.d("result", "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
//                Toast.makeText(LoginActivity.this, "Login Completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribe(Disposable d) {
                showProgress(true);
                Toast.makeText(LoginActivity.this, "Login begin", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNext(HttpResult<String> result) {
                showProgress(false);
                Log.d("result", "onNext: " + result.getResultCode());
            }
        };

        RetrofitUtil.bind(login, deal);
    }

    private void register(String email, String password){

        Observable login = InsService.getInstance().getInsNetwork().register(email, password);

        Observer<HttpResult<String>> deal = new Observer<HttpResult<String>>() {

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                Log.d("result", "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
//                Toast.makeText(LoginActivity.this, "Login Completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribe(Disposable d) {
                showProgress(true);
                Toast.makeText(LoginActivity.this, "Sign up begin", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNext(HttpResult<String> result) {
                showProgress(false);
                Log.d("result", "onNext: " + result.getResultCode());
            }
        };

        RetrofitUtil.bind(login, deal);
    }

    private void test(){
        Observable login = InsService.getInstance().getInsNetwork().test(6919);

        Observer<String> deal = new Observer<String>() {

            @Override
            public void onError(Throwable e) {
                showProgress(false);
                Log.d("result", "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
//                Toast.makeText(LoginActivity.this, "Login Completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSubscribe(Disposable d) {
                showProgress(true);
                Toast.makeText(LoginActivity.this, "fetch begin", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNext(String result) {
                showProgress(false);
                Log.d("result", "onNext: " + result);
            }
        };

        RetrofitUtil.bind(login, deal);
    }
}

