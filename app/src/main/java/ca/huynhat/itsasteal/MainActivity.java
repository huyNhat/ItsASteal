package ca.huynhat.itsasteal;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import ca.huynhat.itsasteal.utils.BottomNavHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    //Widgets
    private DrawerLayout mDrawerLayout;
    private ActionBar actionbar;


    //Vars
    private boolean isMenuTapped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setTitle("It's a Steal!");

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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
                fragment = new Fragment_Home();
                loadFragment(fragment);
                return true;

            case R.id.nav_pinned:
                actionbar.setTitle("My Pinned Deal");
                fragment = new Fragment_Pinned();
                loadFragment(fragment);
                return true;

            case R.id.nav_post:
                actionbar.setTitle("Posting a Deal");
                fragment = new Fragment_Post();
                loadFragment(fragment);
                return true;

            case R.id.nav_notification:
                actionbar.setTitle("Notification");
                fragment = new Fragment_Notification();
                loadFragment(fragment);
                return true;

            case R.id.nav_profile:
                actionbar.setTitle("My Profile");
                fragment = new Fragment_Profile();
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
