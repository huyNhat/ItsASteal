package ca.huynhat.itsasteal.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.List;

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.cloudmessaging.MyFirebaseInstanceIDService;
import ca.huynhat.itsasteal.models.User;
import ca.huynhat.itsasteal.utils.BottomNavHelper;
import ca.huynhat.itsasteal.utils.Constants;

import static android.support.design.widget.BottomNavigationView.*;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    //Widgets
    //private DrawerLayout mDrawerLayout;
    private BottomNavigationView bottomNavigationView;
    private ActionBar actionbar;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    //Firebase Auth
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;

    //Firebase DB
    private FirebaseDatabase mFirebaseDatabase;

    private FrameLayout frameLayout;

    //Vars
    private boolean isMenuTapped = false;
    //Current location
    private LatLng currentLocation;

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
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                //Signed in-->STAY
                init();
                createUser(user);

            } else {
                //frameLayout.setVisibility(View.INVISIBLE);
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build(),
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                                .setLogo(R.mipmap.ic_itsasteal)
                                .build(),
                        RC_SIGN_IN);
            }
        };
    }

    private void createUser(FirebaseUser user) {
        final DatabaseReference usersRef = mFirebaseDatabase.getReference()
                .child(Constants.USERS_LOCATION).child(user.getUid());

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    User newUser = new User(user.getUid().toString()
                            , user.getDisplayName().toString().toLowerCase());
                    usersRef.setValue(newUser);
                    usersRef.child("instanceId").setValue(FirebaseInstanceId.getInstance().getToken());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    private void init() {
        actionbar = getSupportActionBar();
        actionbar.setTitle("It's a Steal!");

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //mDrawerLayout = findViewById(R.id.drawer_layout);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        BottomNavHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Getting user location
        getUserCurrentLocation();


        //Load the default "Home Fragment"
//        if(savedInstanceState == null){
//            loadFragment(new FragmentHome());
//        }
        loadFragment(new FragmentHome());

    }

    private void getUserCurrentLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if(checkLocationPermission()){
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
                //Request location update:
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location location = task.getResult();
                            currentLocation = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                        }
                    }
                });
            }
        }

    }

    private void loadFragment(Fragment fragment) {
       if(!isFinishing()){
           FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
           Bundle bundle = new Bundle();
           fragment.setArguments(bundle);
           fragmentTransaction.replace(R.id.frame_container,fragment);
           fragmentTransaction.addToBackStack(null);
           fragmentTransaction.commitAllowingStateLoss();
       }



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // set item as selected to persist highlight
//        item.setChecked(true);
        // close drawer when item is tapped
        //mDrawerLayout.closeDrawers();
        // Add code here to update the UI based on the item selected
        // For example, swap UI fragments here

        Fragment fragment;
        switch (item.getItemId()){
            case R.id.nav_home:
                actionbar.setTitle("It's a Steal!");
                fragment = new FragmentHome();
                loadFragment(fragment);
                return true;

//            case R.id.nav_pinned:
//                actionbar.setTitle("My Pinned Deal");
//                fragment = new FragmentPinned();
//                loadFragment(fragment);
//                return true;

            case R.id.nav_post:
                Intent intent = new Intent(this,PostADealActivity.class);
                intent.putExtra("myLat",String.valueOf(currentLocation.latitude));
                intent.putExtra("myLong", String.valueOf(currentLocation.longitude));
                startActivity(intent);
                return true;

//            case R.id.nav_notification:
//                actionbar.setTitle("Notification");
//                fragment = new FragmentNotification();
//                loadFragment(fragment);
//                return true;

            case R.id.nav_profile:
                actionbar.setTitle("My Profile");
                fragment = new FragmentProfile();
                loadFragment(fragment);
                return true;
        }
        return false;
    }
    /*
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
    */

    /**
     * Ref: http://androiddhina.blogspot.com/2017/11/how-to-use-google-map-in-fragment.html
     */
    private boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getParent(),new String[]
                                        {Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }

}
