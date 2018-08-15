package com.android.sagot.go4lunch.Controllers.Activities;

import com.android.sagot.go4lunch.Controllers.Base.BaseActivity;
import com.android.sagot.go4lunch.R;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

import butterknife.OnClick;

/**
 * Created by Michaël SAGOT on 15/08/2018.
 */

public class MainActivity extends BaseActivity {

    // For debugging Mode
    private static final String TAG = MainActivity.class.getSimpleName();

    // Adding @BindView in order to indicate to ButterKnife to get & serialise it
    // Of the SearchActivity
    //@BindView(R.id.main_activity_facebook_login_button) Button mGoogleLoginButton;
    //@BindView(R.id.main_activity_google_login_button) Button mFaceBookLoginButton;

    // For Authentication
    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 100;

    // -------------------------
    // DECLARATION BASE METHODS
    // -------------------------
    // BASE METHOD Implementation
    // Get the activity layout
    // CALLED BY BASE METHOD 'onCreate(...)'
    @Override
    protected int getActivityLayout() {
        return R.layout.activity_main;
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.main_activity_facebook_login_button)
    public void onClickFacebookLoginButton() {
        this.startSignInActivity();
    }

    @OnClick(R.id.main_activity_google_login_button)
    public void onClickGoogleLoginButton() {
        this.startSignInActivity();
    }

    // --------------------
    // NAVIGATION
    // --------------------

    // 2 - Launch Sign-In Activity
    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(  new AuthUI.IdpConfig.GoogleBuilder().build(),    //GOOGLE
                                                new AuthUI.IdpConfig.FacebookBuilder().build())) // FACEBOOK
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.pic_logo_restaurant_400x400)
                        .build(),
                RC_SIGN_IN);
    }
}
