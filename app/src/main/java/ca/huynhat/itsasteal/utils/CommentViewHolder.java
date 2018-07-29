package ca.huynhat.itsasteal.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



import ca.huynhat.itsasteal.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public ImageView thumbnail;
    public TextView userComment;

    public CommentViewHolder(View itemView) {
        super(itemView);
        userComment = (TextView) itemView.findViewById(R.id.textViewComment);


    }

    public void setUserComment(String q){
        userComment.setText(q);
    }

}
