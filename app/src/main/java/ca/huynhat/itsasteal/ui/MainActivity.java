package ca.huynhat.itsasteal.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import ca.huynhat.itsasteal.R;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    //Widgets
    private DrawerLayout mDrawerLayout;
    private ActionBar actionbar;

    //Firebase Auth
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;

    private FrameLayout frameLayout;

    //Vars
    private boolean isMenuTapped = false;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.frame_container);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //Signed in-->STAY
                    init();

                }else {
                    //frameLayout.setVisibility(View.INVISIBLE);
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build()))
                                    .setLogo(R.mipmap.ic_itsasteal)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (resultCode == RESULT_OK) {

        }
    }

    private void init(){
        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setTitle("It's a Steal!");

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Load the default "Home Fragment"
//        if(savedInstanceState == null){
//            loadFragment(new FragmentHome());
//        }
        loadFragment(new FragmentHome());

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_container,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // set item as selected to persist highlight
        item.setChecked(true);
        // close drawer when item is tapped
        mDrawerLayout.closeDrawers();
        // Add code here to update the UI based on the item selected
        // For example, swap UI fragments here

        Fragment fragment;
        switch (item.getItemId()){
            case R.id.nav_home:
                actionbar.setTitle("It's a Steal!");
                fragment = new FragmentHome();
                loadFragment(fragment);
                return true;

            case R.id.nav_pinned:
                actionbar.setTitle("My Pinned Deal");
                fragment = new FragmentPinned();
                loadFragment(fragment);
                return true;

            case R.id.nav_post:
                actionbar.setTitle("Posting a Deal");
                fragment = new FragmentPost();
                loadFragment(fragment);
                return true;

            case R.id.nav_notification:
                actionbar.setTitle("Notification");
                fragment = new FragmentNotification();
                loadFragment(fragment);
                return true;

            case R.id.nav_profile:
                actionbar.setTitle("My Profile");
                fragment = new FragmentProfile();
                loadFragment(fragment);
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                isMenuTapped = !isMenuTapped;
                if (isMenuTapped)
                    mDrawerLayout.openDrawer(GravityCompat.START);
                  else
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
