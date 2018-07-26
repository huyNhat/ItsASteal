package ca.huynhat.itsasteal.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import java.util.List;

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.models.Deal;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.AppViewHolder> {
    private Context context;
    private List<Deal> dealList;
    private OnDealItemClickListener onDealItemClickListener;


    public interface OnDealItemClickListener {
        void onItemClick(Deal deal);
    }

    //Empty Constructor
    public HomeRecyclerAdapter(){

    }

    public HomeRecyclerAdapter(Context context, List<Deal> dealList, OnDealItemClickListener onDealItemClickListener) {
        this.context = context;
        this.dealList = dealList;
        this.onDealItemClickListener = onDealItemClickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.deal_row_item, parent, false);

        return new AppViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.bind(dealList.get(position),onDealItemClickListener);


//        Deal mDeal = dealList.get(position);
//
//
//        holder.deal_name.setText(mDeal.getDealName());
//        holder.deal_store.setText(mDeal.getStoreName());
//        holder.price.setText(String.valueOf(mDeal.getPrice()));
//        holder.num_thumps_up.setText(String.valueOf(mDeal.getThumpsUp()));
//
//        String sampleImgUrl = "https://images.unsplash.com/photo-1458862768540-8b091824fe2d?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=8aae34cf35df31a592f0bef16e6342ef";
//        Picasso.get().load(sampleImgUrl).resize(60, 80).into(holder.thumbnail);


    }

    @Override
    public int getItemCount() {
        return dealList.size();
    }



    public class AppViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnail;
        public TextView deal_name, deal_store, time_stamp, price, num_thumps_up;


        public AppViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.deal_image);
            deal_name =(TextView) itemView.findViewById(R.id.deal_name);
            deal_store= (TextView) itemView.findViewById(R.id.deal_store);
            price = (TextView) itemView.findViewById(R.id.deal_price);
            time_stamp = (TextView) itemView.findViewById(R.id.deal_timestamp);
            num_thumps_up =(TextView) itemView.findViewById(R.id.thumps_up_count);
        }

        public void bind (final Deal mDeal, final OnDealItemClickListener onDealItemClickListener){

            deal_name.setText(mDeal.getDealName());
            deal_store.setText(mDeal.getStoreName());
            price.setText(String.valueOf(mDeal.getPrice()));
            num_thumps_up.setText(String.valueOf(mDeal.getThumpsUp()));

            String sampleImgUrl = "https://images.unsplash.com/photo-1458862768540-8b091824fe2d?ixlib=rb-0.3.5&q=80&fm=jpg&crop=entropy&cs=tinysrgb&w=200&fit=max&s=8aae34cf35df31a592f0bef16e6342ef";
            Glide.with(context).load(sampleImgUrl).into(thumbnail);


            itemView.setOnClickListener(v -> onDealItemClickListener.onItemClick(mDeal));
        }
    }
}
