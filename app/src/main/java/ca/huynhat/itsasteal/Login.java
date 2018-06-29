package ca.huynhat.itsasteal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.huynhat.itsasteal.ui.Fragment_Profile;

public class Login extends AppCompatActivity {

    private static final String TAG = Fragment_Profile.class.getSimpleName() + " LoginAct_FACELOG";

    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;


    @Nullable
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "Inside Facebook Fragment");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();  //The callback manager is like a bridge between FB and the app, that will allow info to pass
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button1);
        loginButton.setReadPermissions("email", "public_profile"); //we can add more permissions here like (image, dateofbirth, handling post, writepostpermission in case we want to get more info from the user profile)
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());   //This will pass the login result.getAccToken that was done by the Callback Manager, and pass it through the handleAccessToken method
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    //TODO: For this method to work on the Fragment I had to make it public instead of protected. Check if this is good for security reason.
    //The following method calls mCallbackManager, and asks for the results whether logind was
    // successfull, or no, or if error. We put it here so that it is proccessesd when the login
    // activity is populated
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            updateUI();
        }
    }

    ///Change not logged in UI to Logged in UI.
    //TODO: What will happen if login successful
    private void updateUI() {
        Toast.makeText(Login.this, "You are logged in", Toast.LENGTH_LONG).show();

    }

    //This will get the access token and create an oAuth Cred with FB Auth provider and then signIn to our  mAuth
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)  //Similar to what we do with "SignInWithUserPAssword, we use here signInWithCredential
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }




}
