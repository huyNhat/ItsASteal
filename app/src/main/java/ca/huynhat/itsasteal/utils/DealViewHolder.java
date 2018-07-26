package ca.huynhat.itsasteal.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ca.huynhat.itsasteal.R;

public class DealViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail;
    public TextView deal_name1, deal_store1, time_stamp1, price1, num_thumps_up1;

    public DealViewHolder(View itemView) {
        super(itemView);

        thumbnail = (ImageView) itemView.findViewById(R.id.deal_image);
        deal_name1 =(TextView) itemView.findViewById(R.id.deal_name);
        deal_store1= (TextView) itemView.findViewById(R.id.deal_store);
        time_stamp1 = (TextView) itemView.findViewById(R.id.deal_timestamp);
        price1 = (TextView) itemView.findViewById(R.id.deal_price);
        num_thumps_up1 =(TextView) itemView.findViewById(R.id.thumps_up_count);
    }

    public void setDealName(String deal_name){
        deal_name1.setText(deal_name);
    }

    public void setPrice(String price){
        price1.setText("$"+price);
    }

    public void setStore(String store){
        deal_store1.setText(store);
    }

    public void setTimeStamp(String time){
        time_stamp1.setText(time);
    }

    public void setThumpUp(String thumps){
        num_thumps_up1.setText(thumps);
    }

    public void setImage(String deal_id, Context context){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(deal_id);
        Glide.with(context).load(storageReference).into(thumbnail);
    }

}
