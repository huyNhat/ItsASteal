package ca.huynhat.itsasteal.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
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

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.models.Comment;
import ca.huynhat.itsasteal.models.Deal;
import ca.huynhat.itsasteal.utils.CommentViewHolder;
import ca.huynhat.itsasteal.utils.Constants;
import ca.huynhat.itsasteal.utils.DealViewHolder;
import ca.huynhat.itsasteal.utils.RecyclerItemTouchHelper;

public class FragmentProfile extends Fragment  implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    private static final String TAG = FragmentProfile.class.getSimpleName();

    private Context context;

    private ActionBar actionBar;

    //Widget
    private TextView userNameTextview;
    private RecyclerView mRecylerView;
    private CoordinatorLayout container;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private DatabaseReference userDealReference;

    FirebaseRecyclerAdapter<Deal,DealViewHolder> adapter;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_profile_layout, container,false);

        init(rootView);

        return rootView;
    }

    private void init(View rootView) {
        context = getActivity();
        container = (CoordinatorLayout) rootView.findViewById(R.id.mainContainer);
        userNameTextview = (TextView) rootView.findViewById(R.id.userNameTextView);
        userNameTextview.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        setHasOptionsMenu(true);

        mRecylerView = (RecyclerView) rootView.findViewById(R.id.myItemRecyclerList);
        mRecylerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecylerView.setItemAnimator(new DefaultItemAnimator());

        userDealReference = FirebaseDatabase.getInstance().getReference(Constants.USERS_DEALS_LOCATION)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_option:
                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "signed out succesfully ... ", Toast.LENGTH_SHORT).show();
                            }
                        });
                return true;
            case R.id.edit_profile_option:

                return true;
        }
        return false;
    }

    private void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Deal> options =
                new FirebaseRecyclerOptions.Builder<Deal>().setQuery(userDealReference, Deal.class).build();

        adapter =
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
                        holder.setTimeStamp(model.getTimeStamp());
                        holder.setThumpUp(String.valueOf(model.getThumpsUp()));

//                        holder.mCardview.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(getContext(), DealDetailActivity.class);
//                                intent.putExtra("deal_id", model.getDeal_id());
//                                startActivity(intent);
//                            }
//                        });
                    }


                };
        adapter.startListening();

        mRecylerView.setAdapter(adapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecylerView);

    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DealViewHolder) {
            String deal_id=adapter.getRef(position).getKey();
            adapter.getRef(position).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    //Remove from the Main_Deal Location
                    FirebaseDatabase.getInstance().getReference(Constants.DEALS_LOCATION).child(deal_id).removeValue();
                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();
                }
            });


            //TODO: support for UNDO?

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach is called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach is called");
    }

    private void loadMyDeals() {
        userDealReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
