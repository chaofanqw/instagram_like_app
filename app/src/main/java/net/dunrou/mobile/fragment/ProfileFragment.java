package net.dunrou.mobile.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.MainActivity;
import net.dunrou.mobile.base.converter.UriConverter;
import net.dunrou.mobile.base.firebaseClass.FirebaseUser;
import net.dunrou.mobile.base.message.ProfileMessage;
import net.dunrou.mobile.bean.DiscoverUserAdapter;
import net.dunrou.mobile.bean.PhotoAdapter;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URI;


/**
 * Created by wei on 2018/9/29.
 */
public class ProfileFragment extends Fragment {

    private transient final static String TAG = DiscoverFragment.class.getSimpleName();

    private View profileView;
    private Context profileContext;

    private String currentUser;
    private FirebaseUser userProfile;

    private ImageView photoImage;
    private TextView profileName;
    private TextView numOfPosts;
    private TextView numOfFollowers;
    private TextView numOfFollowing;
    private RecyclerView photoGrid;
    private PhotoAdapter photoAdapter;


    public ProfileFragment() {
        // Required empty public constructor

    }

    public static ProfileFragment newInstance(String username) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileContext = getActivity().getApplicationContext();
        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle bundle = getArguments();
        if (bundle != null)
            currentUser = bundle.getString("username");

        initializeProfileUser();
        initializeProfileLayout();
        initializeAdapter();

        new FirebaseUtil().getUserProfile(userProfile);
        new FirebaseUtil().getProfileStats(userProfile);
        new FirebaseUtil().getPostStats(userProfile);
        new FirebaseUtil().getAllMyPostsForProfile(currentUser);

        return profileView;
    }

    public void initializeProfileUser() {
        userProfile = new FirebaseUser(currentUser, null);

    }

    public void initializeProfileLayout() {

        photoImage = profileView.findViewById(R.id.profilePhoto);
        profileName = profileView.findViewById(R.id.profileName);
        numOfPosts = profileView.findViewById(R.id.numPosts);
        numOfFollowers = profileView.findViewById(R.id.numFollowers);
        numOfFollowing = profileView.findViewById(R.id.numFollowing);
        photoGrid = profileView.findViewById(R.id.RecyclerView);

    }

    public void refreshProfileInfo() {

        if (userProfile.getAvatar() == null) {
            photoImage.setImageResource(R.drawable.profile_p);
            // Log.d("ProfileFragment", "Photo Null");

        } else {
            String uriString = (userProfile.getAvatar().toASCIIString());
            Picasso.with(profileContext).load(uriString).fit().into(photoImage);
            // Log.d("ProfileFragment", uriString);
        }

        profileName.setText(userProfile.getUserID());

    }

    public void initializeAdapter() {
        photoAdapter = new PhotoAdapter(getActivity().getApplicationContext());
        photoGrid.setAdapter(photoAdapter);
        photoGrid.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void updateNumOfPosts(int posts) {
        numOfPosts.setText(String.valueOf(posts));
    }

    public void updateNumOfFollowers(int followers) {
        numOfFollowers.setText(String.valueOf(followers));
    }

    public void updateNumOfFollowing(int following) {
        numOfFollowing.setText(String.valueOf(following));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshProfile(ProfileMessage.RefreshProfile newProfile) {
        this.userProfile = newProfile.getProfile();
        Log.d("ProfileFragment", "Profile Received.");
        refreshProfileInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePosts(ProfileMessage.UpdateNumOfPosts newPostsNum) {
        updateNumOfPosts(newPostsNum.getPosts());
        Log.d("ProfileFragment", "Update Number of Posts.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateFollowers(ProfileMessage.UpdateNumOfFollowers newFollowersNum) {
        updateNumOfFollowers(newFollowersNum.getFollowers());
        // Log.d("ProfileFragment", "Update Number of Followers.");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateFollowing(ProfileMessage.UpdateNumOfFollowing newFolloweringNum) {
        updateNumOfFollowing(newFolloweringNum.getFollowing());
        // Log.d("ProfileFragment", "Update Number of Following");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddPosts(ProfileMessage.PostAddedEvent newPost) {
        photoAdapter.addPost(newPost.getPost());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePosts(ProfileMessage.PostChangedEvent newPost) {
        photoAdapter.updatePost(newPost.getPost());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemovePosts(ProfileMessage.PostRemovedEvent newPost) {
        photoAdapter.removePost(newPost.getPost());
    }

}
