package ca.huynhat.itsasteal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Fragment_Profile extends Fragment {
    private static final String TAG = Fragment_Profile.class.getSimpleName() + " FACELOG";

    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    //TODO: For this method to work on the Fragment I had to make it public instead of protected. Check if this is good for security reason.
    //The following method calls mCallbackManager, and asks for the results whether logind was
    // successfull, or no, or if error. We put it here so that it is proccessesd when the login
    // activity is populated
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_profile_layout, container,false);

        Log.d(TAG, "Inside Facebook Fragment");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();  //The callback manager is like a bridge between FB and the app, that will allow info to pass
        LoginButton loginButton = (LoginButton)rootView.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile"); //we can add more permissions here like (image, dateofbirth, handling post, writepostpermission in case we want to get more info from the user profile)
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
          //      handleFacebookAccessToken(loginResult.getAccessToken());   //pending to create this method
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

        return rootView;
    }
}
