package ca.huynhat.itsasteal.utils;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.choota.dev.ctimeago.TimeAgo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.huynhat.itsasteal.R;
import ca.huynhat.itsasteal.ui.DealDetailActivity;

public class DealViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail;
    public TextView deal_name1, deal_store1, time_stamp1, price1, num_thumps_up1;
    public CardView mCardview;
    public ConstraintLayout view_fore, view_back;


    public DealViewHolder(View itemView) {
        super(itemView);
        thumbnail = (ImageView) itemView.findViewById(R.id.deal_image);
        deal_name1 =(TextView) itemView.findViewById(R.id.deal_name);
        deal_store1= (TextView) itemView.findViewById(R.id.deal_store);
        time_stamp1 = (TextView) itemView.findViewById(R.id.deal_timestamp);
        price1 = (TextView) itemView.findViewById(R.id.deal_price);
        num_thumps_up1 =(TextView) itemView.findViewById(R.id.thumps_up_count);
        mCardview = (CardView) itemView.findViewById(R.id.my_card_view);
        view_fore = (ConstraintLayout) itemView.findViewById(R.id.view_foreground);
        view_back = (ConstraintLayout) itemView.findViewById(R.id.view_background);

    }

    public void setDealName(String deal_name){
        deal_name1.setText(deal_name);
    }

    public void setPrice(String price){
        price1.setText("$"+price);
    }

    public void setStore(String store){
        deal_store1.setText(store.split(",")[0]);
    }

    public void setTimeStamp(String time){
//        //TimeAgo timeAgo = new TimeAgo();
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        long milsec =0;
//        try {
//            milsec = dateFormat.parse(time).getTime();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        time_stamp1.setText(ca.huynhat.itsasteal.utils.TimeAgo.getTimeAgo(time));
    }

    public void setThumpUp(String thumps){
        num_thumps_up1.setText(thumps);
    }

    public void setImage(String deal_id, Context context){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(deal_id);
        Glide.with(context).load(storageReference).into(thumbnail);
    }

}
