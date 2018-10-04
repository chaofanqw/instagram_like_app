package net.dunrou.mobile.bean;


import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.MainActivity;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoAdapterViewHolder> {

    private final static String TAG = DiscoverUserAdapter.class.getSimpleName();

    private Context paContext;

    private ArrayList<FirebaseEventPost> myPosts;

    public PhotoAdapter() {
    }

    public PhotoAdapter(Context context) {
        paContext = context;
        myPosts = new ArrayList<>();


    }

    @Override
    public PhotoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoAdapterViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, null));
    }

    @Override
    public void onBindViewHolder(PhotoAdapterViewHolder holder, int position) {
        FirebaseEventPost tempPost = myPosts.get(getItemCount() - 1 - position);

        holder.comment.setText(tempPost.getComment());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        holder.time.setText(dateFormat.format(tempPost.getTime()));

        Picasso.with(paContext).load(tempPost.getPhotos().get(0)).fit().into(holder.photo1);

        holder.more.setVisibility(View.INVISIBLE);

        int numOfPhotos = tempPost.getPhotos().size();

        if (numOfPhotos >= 2){
            holder.photo2.setVisibility(View.VISIBLE);
            Picasso.with(paContext).load(tempPost.getPhotos().get(1)).fit().into(holder.photo2);
        }else{
            holder.photo2.setVisibility(View.INVISIBLE);
        }

        if (numOfPhotos >= 3){
            holder.photo3.setVisibility(View.VISIBLE);
            Picasso.with(paContext).load(tempPost.getPhotos().get(2)).fit().into(holder.photo3);
            if (numOfPhotos > 3){
                holder.more.setVisibility(View.VISIBLE);
            }

        }else{
            holder.photo3.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return myPosts.size();
    }

    public void updatePosts() {
        notifyDataSetChanged();
    }

    public void addPost(FirebaseEventPost post) {
        this.myPosts.add(post);
        updatePosts();
    }


    public void updatePost(FirebaseEventPost post) {
        for (int i = 0; i < myPosts.size(); i++) {
            if (myPosts.get(i).getEventPostId().equals(post.getEventPostId())) {
                myPosts.set(i, post);
            }
        }
        updatePosts();
    }


    public void removePost(FirebaseEventPost post) {
        ArrayList<FirebaseEventPost> targets = new ArrayList<>();
        for (FirebaseEventPost fu : myPosts) {
            if (!fu.getEventPostId().equals(post.getEventPostId()))
                targets.add(fu);
        }
        myPosts = targets;
        updatePosts();
    }


    public class PhotoAdapterViewHolder extends RecyclerView.ViewHolder {

        private final ImageView photo1;
        private final ImageView photo2;
        private final ImageView photo3;
        private final TextView time;
        private final TextView comment;
        private final TextView more;

        public PhotoAdapterViewHolder(View itemView) {
            super(itemView);
            photo1 = (ImageView) itemView.findViewById(R.id.image1);
            photo2 = (ImageView) itemView.findViewById(R.id.image2);
            photo3 = (ImageView) itemView.findViewById(R.id.image3);
            time = (TextView) itemView.findViewById(R.id.time);
            comment = (TextView) itemView.findViewById(R.id.comment);
            more = (TextView) itemView.findViewById(R.id.More);

        }
    }


}
