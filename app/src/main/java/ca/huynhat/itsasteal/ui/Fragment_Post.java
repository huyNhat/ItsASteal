package ca.huynhat.itsasteal.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import ca.huynhat.itsasteal.R;

public class Fragment_Post extends Fragment {

    private static final String TAG = Fragment_Post.class.getSimpleName();

    //Reference to XML elements
    private EditText mDealName, mDealDescription, mDealQuantity, mDealPrice;
    private TextView mCreate, mCancel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_post_layout, container,false);

        mDealName = rootView.findViewById(R.id.deal_name_title);
        mDealDescription = rootView.findViewById(R.id.deal_description);
        mDealQuantity = rootView.findViewById(R.id.deal_quantity);
        mDealPrice = rootView.findViewById(R.id.deal_price);

        mCancel.setOnClickListener((View.OnClickListener) this);
        mCreate.setOnClickListener((View.OnClickListener)this);

        return rootView;
    }
}
