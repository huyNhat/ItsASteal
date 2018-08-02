package ca.huynhat.itsasteal.ui;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.models.Deal;
import ca.huynhat.itsasteal.utils.Constants;
import ca.huynhat.itsasteal.utils.DealViewHolder;
import ca.huynhat.itsasteal.utils.HomeRecyclerAdapter;
import ca.huynhat.itsasteal.viewmodels.HomeFeedViewModel;

/**
 * Ref: http://androiddhina.blogspot.com/2017/11/how-to-use-google-map-in-fragment.html
 */

public class FragmentHome extends Fragment implements GoogleMap.OnMarkerClickListener {
    private static final String TAG = FragmentHome.class.getSimpleName();

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private ArrayList<LatLng> markerPoints;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;

    //Widgets
    private RecyclerView homeRecyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    private ActionBar actionBar;

    private HomeRecyclerAdapter homeRecyclerAdapter;

    //Current location
    private LatLng currentLocation;

    //Firebase
    DatabaseReference dealReference;

    private Marker marker;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_home_layout, container,false);

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);

        initMap(rootView,savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from(rootView.findViewById(R.id.bottom_sheet));

        bottomSheetBehavior.setPeekHeight(380);
        //Setting up RecyclerView
        homeRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView_NearByDeals);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        homeRecyclerView.setLayoutManager(linearLayoutManager);
        homeRecyclerView.setItemAnimator(new DefaultItemAnimator());


        dealReference = FirebaseDatabase.getInstance().getReference(Constants.DEALS_LOCATION);


        //final ProgressBar mProgressBar = (ProgressBar) rootView.findViewById(R.id.loading_bar);

        /*

        //ViewModel & LiveData
        HomeFeedViewModel homeFeedViewModel = ViewModelProviders.of(this).get(HomeFeedViewModel.class);
        homeFeedViewModel.getDeals().observe(this, (List<Deal> deals) -> {
            //Update UI
            homeRecyclerAdapter = new HomeRecyclerAdapter(getActivity(), deals, new HomeRecyclerAdapter.OnDealItemClickListener() {
                @Override
                public void onItemClick(Deal deal) {
                    Toast.makeText(getActivity(), deal.getDealName() + " tapped", Toast.LENGTH_SHORT).show();

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container,new FragmentDealDetail())
                            .addToBackStack(null)
                            .commit();

                }
            });
            homeRecyclerView.setAdapter(homeRecyclerAdapter);
            mProgressBar.setVisibility(View.GONE);

        });

        */

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Deal> options =
                new FirebaseRecyclerOptions.Builder<Deal>().setQuery(dealReference, Deal.class).build();

        FirebaseRecyclerAdapter<Deal,DealViewHolder> adapter =
                new FirebaseRecyclerAdapter<Deal, DealViewHolder>(options) {
                    @NonNull
                    @Override
                    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.deal_row_item,parent,false);

                        return new DealViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull DealViewHolder holder, int position, @NonNull Deal model) {
                        holder.setDealName(model.getDealName());
                        holder.setImage(model.getDeal_id(),getContext());
                        holder.setPrice(String.valueOf(model.getPrice()));
                        holder.setStore(model.getStoreName());
                        holder.setTimeStamp(model.getTimeStamp().toString());
                       // holder.setThumpUp(String.valueOf(model.getThumpsUp()));
                        holder.setThumpUp(String.valueOf(model.getThumpsUp()-model.getThumpsDown()));

                        holder.mCardview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DealDetailActivity.class);
                                intent.putExtra("deal_id", model.getDeal_id());
                                startActivity(intent);
                            }
                        });
                    }
                };
        adapter.startListening();

        homeRecyclerView.setAdapter(adapter);

    }

    private void initMap(View rootView, @Nullable Bundle savedInstanceState){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                mGoogleMap = googleMap;

//                mLocationRequest = new LocationRequest();
//                mLocationRequest.setInterval(120000); // two minute interval
//                mLocationRequest.setFastestInterval(120000);
//                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

                if(checkLocationPermission()){
                    if(ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED){
                        //Request location update:

                        mGoogleMap.setMyLocationEnabled(true);
                    }
                }

                markerPoints = new ArrayList<LatLng>();

                googleMap.getUiSettings().setCompassEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);



                //TODO: it's getting "last known location" -->check whether it's getting accurate?
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location location = task.getResult();
                            currentLocation = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLocation,
                                    12);
                            googleMap.moveCamera(update);
                        }
                    }
                });

                //Plot deal location on GMaps
                final List<Deal> mList = new ArrayList<>();

                dealReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Deal deal = snapshot.getValue(Deal.class);
                            mList.add(deal);

                            for (int i = 0; i<mList.size();i++){
                                LatLng latLng = new LatLng(Double.valueOf(deal.getLatitude()),Double.valueOf(deal.getLongtitude()));
                                if(mGoogleMap!=null){
                                     marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(deal.getDealName()));
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * Ref: http://androiddhina.blogspot.com/2017/11/how-to-use-google-map-in-fragment.html
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mGoogleMap.setMyLocationEnabled(true);
                    }
                } else {

                }
                return;
            }

        }
    }

    /**
     * Ref: http://androiddhina.blogspot.com/2017/11/how-to-use-google-map-in-fragment.html
     */
    private boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("")
                        .setMessage("")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),new String[]
                                        {Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_add_option,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_item_option:
                Intent intent = new Intent(getActivity(),PostADealActivity.class);
                intent.putExtra("myLat",String.valueOf(currentLocation.latitude));
                intent.putExtra("myLong", String.valueOf(currentLocation.longitude));
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("It's a Steal!");


    }


    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
