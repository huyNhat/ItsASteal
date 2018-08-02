package ca.huynhat.itsasteal.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.models.Comment;
import ca.huynhat.itsasteal.models.Deal;
import ca.huynhat.itsasteal.utils.CommentViewHolder;
import ca.huynhat.itsasteal.utils.Constants;
import ca.huynhat.itsasteal.utils.DealViewHolder;
import ca.huynhat.itsasteal.utils.TimeAgo;

public class DealDetailActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = DealDetailActivity.class.getSimpleName();

    //Widgets
    private ActionBar actionBar;
    private TextView dealTitle, dealStore, dealQuantity, dealTimeStamp, dealPrice;
    private ImageView dealThumb;
    private Button thumpUp, thumpDown;
    private EditText sendAComment;
    private RecyclerView commentsOnDealRecyclerView;


    //Firebase DB
    DatabaseReference dealReference, userDealReference, commentOnDealReference;

    //Vars
    private String deal_id;
    private int currentThumpUp, currentThumpDown;

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
            commentOnDealReference = FirebaseDatabase.getInstance().getReference(Constants.COMMENT_LOCATION)
                                        .child(deal_id);

            init();

            dealReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Deal deal = dataSnapshot.getValue(Deal.class);

                    actionBar.setTitle(deal.getDealName());
                    dealStore.setText(deal.getStoreName());
                    dealQuantity.setText("Est. Quantity: " + deal.getQuantity());
                    dealTimeStamp.setText("Posted: " + TimeAgo.getTimeAgo(deal.getTimeStamp()));
                    dealPrice.setText("$" + deal.getPrice());
                    thumpUp.setText(String.valueOf(deal.getThumpsUp()));
                    thumpDown.setText(String.valueOf(deal.getThumpsDown()));
                    currentThumpUp = deal.getThumpsUp();
                    currentThumpDown = deal.getThumpsDown();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }

    private void init() {
        dealStore = (TextView) findViewById(R.id.deal_store_name1);
        dealQuantity = (TextView) findViewById(R.id.deal_quantity1);
        dealTimeStamp = (TextView) findViewById(R.id.deal_timestamp1);
        dealPrice = (TextView) findViewById(R.id.deal_price1);
        dealThumb = (ImageView) findViewById(R.id.deal_image_detail1);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(deal_id);
        Glide.with(getApplicationContext()).load(storageReference).into(dealThumb);

        thumpUp = (Button) findViewById(R.id.thumpUpBtn);
        thumpDown = (Button) findViewById(R.id.thumpDownBtn);
        thumpUp.setOnClickListener(this);
        thumpDown.setOnClickListener(this);

        sendAComment = (EditText) findViewById(R.id.messageToSend);
        sendAComment.clearFocus();

        commentsOnDealRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_CommentsOnDeal);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsOnDealRecyclerView.setLayoutManager(linearLayoutManager);
        commentsOnDealRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.thumpUpBtn:
                Toast.makeText(this, "Thumb up tapped", Toast.LENGTH_SHORT).show();
                dealReference.child("thumpsUp").setValue(currentThumpUp + 1);
                break;
            case  R.id.thumpDownBtn:
                Toast.makeText(this, "Thump Down tapped", Toast.LENGTH_SHORT).show();
                dealReference.child("thumpsDown").setValue(currentThumpDown + 1);
                break;
        }

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

    public void sendMessage(View view) {

        String messageString = sendAComment.getText().toString();

        if(!messageString.equals("")){
            final DatabaseReference pushRef = commentOnDealReference.push();
            final String commentId = pushRef.getKey();



            //Create message object with text/voice etc
            Comment comment = new Comment(commentId,FirebaseAuth.getInstance().getCurrentUser().getUid(),messageString);
            //Create HashMap for Pushing
            HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
            HashMap<String,Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                    .convertValue(comment, Map.class);
            messageItemMap.put("/" + commentId, messageObj);
            commentOnDealReference.updateChildren(messageItemMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendAComment.setText("");
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comment> options =
                new FirebaseRecyclerOptions.Builder<Comment>().setQuery(commentOnDealReference, Comment.class).build();

        FirebaseRecyclerAdapter<Comment,CommentViewHolder> adapter =
                new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(options) {

                    @NonNull
                    @Override
                    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.comment_row_layout,parent,false);
                        return new CommentViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                        holder.setUserComment(model.getContent());
                    }
                };
        adapter.startListening();

        commentsOnDealRecyclerView.setAdapter(adapter);

    }
}
