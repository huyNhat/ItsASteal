package ca.huynhat.itsasteal.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.huynhat.itsasteal.R;

public class DealCommentAdapter extends RecyclerView.Adapter<DealCommentAdapter.CommentViewHolder>  {


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_row_layout, parent, false);

        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        public CommentViewHolder(View itemView) {
            super(itemView);
        }
    }
}
