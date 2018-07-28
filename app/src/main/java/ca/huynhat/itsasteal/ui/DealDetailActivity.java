package ca.huynhat.itsasteal.ui;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.models.Deal;
import ca.huynhat.itsasteal.utils.Constants;

public class DealDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = DealDetailActivity.class.getSimpleName();

    //Widgets
    private ActionBar actionBar;
    private TextView dealTitle, dealStore, dealQuantity, dealTimeStamp, dealPrice;
    private ImageView dealThumb;
    private ImageButton thumpUp, thumpDown;


    //Firebase DB
    DatabaseReference dealReference, userDealReference;

    //Vars
    private String deal_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_detail);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("deal_id")){
            deal_id = getIntent().getStringExtra("deal_id");

            dealReference = FirebaseDatabase.getInstance().getReference(Constants.DEALS_LOCATION)
                    .child(deal_id);
            userDealReference = FirebaseDatabase.getInstance().getReference(Constants.USERS_DEALS_LOCATION)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(deal_id);

            init();

            dealReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Deal deal = dataSnapshot.getValue(Deal.class);

                    actionBar.setTitle(deal.getDealName());
                    dealTitle.setText(deal.getDealName());
                    dealStore.setText(deal.getStoreName());
                    dealQuantity.setText("Est. Quantity: " + deal.getQuantity());
                    dealTimeStamp.setText("Posted: " + deal.getTimeStamp());

                    dealPrice.setText("$" + deal.getPrice());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }

    private void init() {
        dealTitle = (TextView) findViewById(R.id.deal_name_detail1);
        dealStore = (TextView) findViewById(R.id.deal_store_name1);
        dealQuantity = (TextView) findViewById(R.id.deal_quantity1);
        dealTimeStamp = (TextView) findViewById(R.id.deal_timestamp1);
        dealPrice = (TextView) findViewById(R.id.deal_price1);
        dealThumb = (ImageView) findViewById(R.id.deal_image_detail1);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(deal_id);
        Glide.with(getApplicationContext()).load(storageReference).into(dealThumb);

        thumpUp = (ImageButton) findViewById(R.id.thumpUpBtn);
        thumpDown = (ImageButton) findViewById(R.id.thumpDownBtn);


    }

    @Override
    public void onClick(View v) {

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

}
