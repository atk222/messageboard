package edu.lehigh.cse216.snf221;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {

    private ArrayList<Datum> mData;
    private LayoutInflater mLayoutInflater;
    private OnAddMessageListener mOnAddMessageListener;

    MessageListAdapter(Context context, ArrayList<Datum> data, OnAddMessageListener onAddMessageListener) {
        mData = data;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mOnAddMessageListener = onAddMessageListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView subject;
        TextView likes;
        TextView dislikes;
        Button like_button;
        Button dislike_button;
        OnAddMessageListener onAddMessageListener;

        ViewHolder(View itemView, OnAddMessageListener onMessageListener) {
            super(itemView);
            this.subject = (TextView) itemView.findViewById(R.id.subject);
            this.likes = (TextView) itemView.findViewById(R.id.likes);
            this.dislikes = (TextView) itemView.findViewById(R.id.dislikes);
            this.like_button = (Button) itemView.findViewById(R.id.like_button);
            this.dislike_button = (Button) itemView.findViewById(R.id.dislike_button);
            this.onAddMessageListener = onMessageListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAddMessageListener.onAddMessageClick(getAdapterPosition());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.message_view, null);
        return new ViewHolder(view, mOnAddMessageListener);
    }

    interface ClickListener{
        void onLike(Datum d);
        void onDislike(Datum d);
    }

    private ClickListener mClickListener;
    ClickListener getClickListener() {return mClickListener;}
    void setClickListener(ClickListener c) { mClickListener = c;}

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Datum d = mData.get(position);

        holder.subject.setText(d.mTitle);
        holder.likes.setText(String.format("Likes: %d", d.mLikes));
        holder.dislikes.setText(String.format("Dislikes: %d", d.mDislikes));
        holder.like_button.setText(String.format("Like"));
        holder.dislike_button.setText(String.format("Dislike"));

        // Attach click listeners to the view we are configuring
        //onClickListener for likes
        final View.OnClickListener likeListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mClickListener.onLike(d);
            }

        };

        //onClickListener for dislikes
        final View.OnClickListener dislikeListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mClickListener.onDislike(d);
            }

        };

        //Set like button with likeListener
        holder.like_button.setOnClickListener(likeListener);

        //Set dislike button with dislikeListener
        holder.dislike_button.setOnClickListener(dislikeListener);
    }

    /**
     * Interface for adding an onClickListener for each ViewHolder
     * (In this case it would be for each message on RecyclerView on MainActivity)
     */
    public interface OnAddMessageListener {
        void onAddMessageClick(int position);
    }
}