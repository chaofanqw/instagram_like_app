package net.dunrou.mobile.bean;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import net.dunrou.mobile.R;

import java.util.ArrayList;

/**
 * Created by yvette on 2018/9/19.
 */

public class DiscoverUserAdapter extends RecyclerView.Adapter<DiscoverUserAdapter.DiscoverUserViewHolder> {

    private transient final static String TAG = DiscoverUserAdapter.class.getSimpleName();

    private Context mContext;

    private ArrayList<String> mockUsers = new ArrayList<String>() {{
        add("username1");
        add("username2");
        add("username3");
    }};

    public DiscoverUserAdapter() {}

    public DiscoverUserAdapter(Context context) {
        mContext = context;
    }

    @Override
    public DiscoverUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiscoverUserViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_search_item, null));
    }

    @Override
    public void onBindViewHolder(DiscoverUserViewHolder holder, int position) {
        String user = mockUsers.get(position);
        holder.mUsername_TV.setText(user);
    }

    @Override
    public int getItemCount() {
        return mockUsers.size();
    }

    public class DiscoverUserViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mUserProfilePhoto_IV;
        private final TextView mUsername_TV;

        private DiscoverUserViewHolder(View view) {
            super(view);
            mView = view;
            mUserProfilePhoto_IV = (ImageView) view.findViewById(R.id.userProfilePhoto_IV);
            mUsername_TV = (TextView) view.findViewById(R.id.username_TV);

        }
    }
}
