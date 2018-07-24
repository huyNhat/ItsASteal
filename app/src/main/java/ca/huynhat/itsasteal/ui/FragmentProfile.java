package ca.huynhat.itsasteal.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ca.huynhat.itsasteal.R;

public class FragmentProfile extends Fragment {
    private static final String TAG = FragmentProfile.class.getSimpleName();

    private Context context;

    private ActionBar actionBar;

    //Widget
    private TextView userNameTextview;


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


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
        userNameTextview = (TextView) rootView.findViewById(R.id.userNameTextView);
        userNameTextview.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        setHasOptionsMenu(true);

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
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach is called");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach is called");
    }


}
