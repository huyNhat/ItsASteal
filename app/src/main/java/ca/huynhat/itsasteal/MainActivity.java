package ca.huynhat.itsasteal;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import ca.huynhat.itsasteal.utils.BottomNavHelper;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    //Widgets
    private ActionBar actionBar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        BottomNavHelper.removeShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //Load the default "Home Fragment"
        if(savedInstanceState==null){
            loadFragment(new Fragment_Home());
        }
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
        Fragment fragment;
        switch (item.getItemId()){
            case R.id.nav_home:
                actionBar.setTitle("It's a Steal!");
                fragment = new Fragment_Home();
                loadFragment(fragment);
                return true;

            case R.id.nav_pinned:
                actionBar.setTitle("My Pinned Deal");
                fragment = new Fragment_Pinned();
                loadFragment(fragment);
                return true;

            case R.id.nav_post:
                actionBar.setTitle("Posting a Deal");
                fragment = new Fragment_Post();
                loadFragment(fragment);
                return true;

            case R.id.nav_notification:
                actionBar.setTitle("Notification");
                fragment = new Fragment_Notification();
                loadFragment(fragment);
                return true;

            case R.id.nav_profile:
                actionBar.setTitle("My Profile");
                fragment = new Fragment_Profile();
                loadFragment(fragment);
                return true;
        }
        return false;
    }
}
