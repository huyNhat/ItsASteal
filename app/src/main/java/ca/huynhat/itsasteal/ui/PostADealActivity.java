package ca.huynhat.itsasteal.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.models.Deal;
import ca.huynhat.itsasteal.utils.Constants;
import ca.huynhat.itsasteal.utils.PlaceAutocompleteAdapter;

public class PostADealActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = PostADealActivity.class.getSimpleName();
    private static final int FROM_GALLERY = 123;

    //Widgets
    private ActionBar actionBar;
    private Button btnPost, btnCancel;
    private ImageView dealImageView;
    private EditText dealName, dealPrice, dealQuantity;
    private ProgressBar progressBar;
    private Bitmap selectedBitmap, thumbBitmap;
    private String imgURL="", myLat, myLong;

    //Vars
    private String dealId;
    private Uri selectedImage;


    //Firebase
    private DatabaseReference databaseDeal, databaseUserDeal;

    /**
     * GeoDataClient wraps service connection to Google Play services and provides access
     * to the Google Places API for Android. gg
     */
    protected GeoDataClient mGeoDataClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView dealStoreName; //gg

    //Set bounds to limit the search Area gg
    private static final LatLngBounds BOUNDS_GREATER_VANCOUVER = new LatLngBounds(
            new LatLng(49.2035681, -122.9148781), new LatLng(49.5627791, -122.3216161));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_adeal);

        myLat = getIntent().getStringExtra("myLat");
        myLong = getIntent().getStringExtra("myLong");

        // Construct a GeoDataClient for the Google Places API for Android. gg
        mGeoDataClient = Places.getGeoDataClient(this, null);

        init();
    }

    private void init() {
        actionBar = getSupportActionBar();
        actionBar.setTitle("Posting a Deal");
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        btnPost = (Button) findViewById(R.id.buttonPost);
        btnPost.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.buttonCancel);
        btnCancel.setOnClickListener(this);
        dealImageView =(ImageView) findViewById(R.id.deal_img);
        dealImageView.setOnClickListener(this);
        dealName = (EditText) findViewById(R.id.deal_name);
        dealPrice = (EditText) findViewById(R.id.inputPrice);
        dealQuantity = (EditText) findViewById(R.id.inputQuantity);
        dealStoreName = (AutoCompleteTextView) findViewById(R.id.inputStore);

        databaseDeal = FirebaseDatabase.getInstance().getReference(Constants.DEALS_LOCATION);
        databaseUserDeal = FirebaseDatabase.getInstance().getReference(Constants.USERS_DEALS_LOCATION)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        dealId = databaseDeal.push().getKey();

        //Retrieve the text view for results gg
        //mPlaceDetailsText = (TextView)findViewById(R.id.mPlaceDetailsText);

        // Register a listener that receives callbacks when a suggestion has been selected gg
//gg        dealStoreName.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data Client. gg
        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, BOUNDS_GREATER_VANCOUVER, null);
        dealStoreName.setAdapter(mAdapter);

    }


    private void addNewDeal(){
        final Deal newDeal = new Deal();

        newDeal.setDeal_id(dealId);
        newDeal.setDeal_img_url(imgURL);
        newDeal.setDealName(dealName.getText().toString().trim());
        newDeal.setLatitude(Double.valueOf(myLat));
        newDeal.setLongtitude(Double.valueOf(myLong));
        newDeal.setPrice(Long.valueOf(dealPrice.getText().toString().trim()));
        newDeal.setQuantity(Integer.valueOf(dealQuantity.getText().toString().trim()));
        newDeal.setStoreName(dealStoreName.getText().toString().trim());
        newDeal.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        newDeal.setTimeStamp(getCurrentDateTime());

        //Add deal to DB
        databaseDeal.child(dealId).setValue(newDeal);
        databaseUserDeal.child(dealId).setValue(newDeal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonPost:
                if(!isEmpty(dealName.getText().toString())
                        && !isEmpty(dealPrice.getText().toString())
                        && !isEmpty(dealQuantity.getText().toString())
                        && !isEmpty(dealStoreName.getText().toString())){

                    if(selectedImage != null){
                        uploadImgToCloudStorage();
                        addNewDeal();
                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                        this.finish();
                    }else {
                        Toast.makeText(this, "Please select an image for this deal", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonCancel:
                this.finish();
                break;

            case R.id.deal_img:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, FROM_GALLERY);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FROM_GALLERY && resultCode == Activity.RESULT_OK){
            Toast.makeText(this, "HERE", Toast.LENGTH_SHORT).show();
            selectedImage = data.getData();
            //thumbBitmap = (Bitmap) data.getExtras().get("data");
            Glide.with(this).load(selectedImage).into(dealImageView);
        }
    }

    private void uploadImgToCloudStorage(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(dealId);
        storageReference.putFile(selectedImage).addOnFailureListener(e -> {
            //Fail to upload
            Toast.makeText(this, "Fail to upload IMG", Toast.LENGTH_SHORT).show();
        }).addOnSuccessListener(taskSnapshot -> {
            //Success Upload
            Toast.makeText(this, "Success to upload IMG", Toast.LENGTH_SHORT).show();

        });
    }

    public boolean isEmpty(String string){
        return string.equals("");
    }


    @Override
    public boolean onNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String getCurrentDateTime(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date).toString();
    }
    /*
    private class GetDealThumb extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
    */

    /**
     * Ref: https://stackoverflow.com/questions/50037127/compressing-image-using-asynctask-before-uploading-to-firebase-storage
     */
    public class ImageResize extends AsyncTask<Uri, Integer, byte[]>{
        Bitmap mBitmap;

        public ImageResize(Bitmap bitmap){
            if (bitmap!= null){
                this.mBitmap = bitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(PostADealActivity.this, "Compressing Image", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            if (mBitmap ==null){
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(PostADealActivity.this.getContentResolver(), uris[0]);
                }catch (IOException e){
                    Log.d(TAG, "doInBackground: IOException: "+ e.getMessage());
                }
            }

            byte[] bytes = null;
            bytes = getBytesFromBitmap(mBitmap,100);
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            //mUploadBytes = bytes;
        }

    }


    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data Client
     * to retrieve more details about the place. gg
     */

/*    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
 */           /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
 /*           final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);
*/
            /*
             Issue a request to the Places Geo Data Client to retrieve a Place object with
             additional details about the place.
              */
/*            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);
            placeResult.addOnCompleteListener(mUpdatePlaceDetailsCallback);

            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };
  */

    //gg
    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsCallback
            = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(Task<PlaceBufferResponse> task) {
            try {
                PlaceBufferResponse places = task.getResult();

                // Get the Place object from the buffer.
                final Place place = places.get(0);


      /*        //Note: We will not display the Powered by Google Logo for this delivery of the project.. For production is very important.
                // Display the third party attributions if set.

                final CharSequence thirdPartyAttribution = places.getAttributions();
                if (thirdPartyAttribution == null) {
                    mPlaceDetailsAttribution.setVisibility(View.GONE);
                } else {
                    mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                    mPlaceDetailsAttribution.setText(
                            Html.fromHtml(thirdPartyAttribution.toString()));
                }
        */

                Log.i(TAG, "Place details received: " + place.getName());

                places.release();
            } catch (RuntimeRemoteException e) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete.", e);
                return;
            }
        }
    };



    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality,stream);
        return stream.toByteArray();
    }
}
