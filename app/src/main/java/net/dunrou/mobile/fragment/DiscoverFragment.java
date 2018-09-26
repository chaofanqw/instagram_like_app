package net.dunrou.mobile.fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.message.DiscoverMessage;
import net.dunrou.mobile.bean.DiscoverUserAdapter;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
//import net.dunrou.mobile.activity.SearchableActivity;

/**
 * Created by yvette on 2018/9/18.
 */

public class DiscoverFragment extends Fragment implements SearchView.OnQueryTextListener {
    private transient final static String TAG = DiscoverFragment.class.getSimpleName();

    private View mView;

    private SearchManager mSearchManager;
    private DiscoverUserAdapter mDiscoverUserAdapter;
    private Context mContext;

    private SearchView mSearch_SV;
    private RecyclerView mResults_RV;
    private Button mSearchCancel_BT;
    private TextView mSearchUser_TV;

    private TabLayout mDiscover_TL;
    private TabLayout.Tab tab_top;
    private TabLayout.Tab tab_commonFriend;
    private TabLayout.Tab tab_nearby;

    public DiscoverFragment() {
    }

    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
        EventBus.getDefault().register(this);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_discover, container, false);

        initializeTabLayout();
        initializeSearch();
        initializeAdapter();
        initializeRecycleLayout();

        return mView;
    }

    public void initializeTabLayout() {
        mDiscover_TL = mView.findViewById(R.id.discover_TL);
        tab_top= mDiscover_TL.getTabAt(0);
        tab_commonFriend = mDiscover_TL.getTabAt(1);
        tab_nearby = mDiscover_TL.getTabAt(2);
        tab_commonFriend.select();
        mDiscover_TL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        Log.d(TAG, "click top");
                        mSearch_SV.clearFocus();
                        mSearch_SV.setQuery("", false);
                        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TOP);
                        mSearchUser_TV.setVisibility(View.GONE);
                        break;
                    case 1:
                        Log.d(TAG, "click people");
                        mSearch_SV.clearFocus();
                        mSearch_SV.setQuery("", false);
                        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.COMMON_FRIENDS);
                        mSearchUser_TV.setVisibility(View.GONE);
                        break;
                    case 2:
                        Log.d(TAG, "click nearby");
                        mSearch_SV.clearFocus();
                        mSearch_SV.setQuery("", false);
                        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TEST);
                        mSearchUser_TV.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void initializeSearch() {
        mSearch_SV = mView.findViewById(R.id.search_SV);
        mSearchCancel_BT = mView.findViewById(R.id.searchCancel_BT);
        mSearchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchUser_TV = mView.findViewById(R.id.searchUser_TV);

        mSearch_SV.setSearchableInfo(mSearchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearch_SV.setIconifiedByDefault(false);

        mSearch_SV.setSubmitButtonEnabled(true);
        mSearch_SV.setOnQueryTextListener(this);

        mSearchCancel_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch_SV.clearFocus();
                mSearchUser_TV.setVisibility(View.GONE);

                mSearch_SV.setQuery("", false);

                if(tab_top.isSelected())
                    mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TOP);
                else if(tab_commonFriend.isSelected())
                    mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.COMMON_FRIENDS);
                else
                    mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TEST);
            }
        });
    }

    public void initializeAdapter() {
        mDiscoverUserAdapter = new DiscoverUserAdapter(getActivity().getApplicationContext());
    }

    public void initializeRecycleLayout() {
        mResults_RV = mView.findViewById(R.id.results_RV);
        mResults_RV.setAdapter(mDiscoverUserAdapter);
        mResults_RV.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        Log.d(TAG, "receive query: " + query);
        searchUsers(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Log.d(TAG, "receive text change: " + newText);
        searchUsers(newText);
        return false;
    }


    public void searchUsers(String query) {
        mDiscoverUserAdapter.getSuggestedUsers_search().clear();
        mSearchUser_TV.setVisibility(View.VISIBLE);

        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.SEARCH);
        if(mDiscoverUserAdapter.getSuggestMode() == DiscoverUserAdapter.SEARCH)
            new FirebaseUtil().searchUser(query);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationAddFail(DiscoverMessage.RelationAddFailEvent relationAddFailEvent) {
        Toast toast = Toast.makeText(mContext, "follow fail", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipAddedEvent(DiscoverMessage.RelationshipAddedEvent relationshipChildAddedEvent) {
        mDiscoverUserAdapter.addRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipChangedEvent(DiscoverMessage.RelationshipChangedEvent relationshipChildAddedEvent) {
        mDiscoverUserAdapter.updateRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationshipRemovedEvent(DiscoverMessage.RelationshipRemovedEvent relationshipChildAddedEvent) {
        mDiscoverUserAdapter.removeRelationship(relationshipChildAddedEvent.getFirebaseRelationship());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserAddedEvent(DiscoverMessage.UserAddedEvent userAddedEvent) {
        mDiscoverUserAdapter.addUser(userAddedEvent.getSuggestedUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserChangedEvent(DiscoverMessage.UserChangedEvent userChangedEvent) {
        mDiscoverUserAdapter.updateUser(userChangedEvent.getSuggestedUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserRemovedEvent(DiscoverMessage.UserRemovedEvent userRemovedEvent) {
        mDiscoverUserAdapter.removeUser(userRemovedEvent.getSuggestedUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserSearchGetEvent(DiscoverMessage.UserSearchGetEvent userSearchGetEvent) {
        mSearchUser_TV.setVisibility(View.GONE);
        if(mDiscoverUserAdapter.getSuggestMode() == DiscoverUserAdapter.SEARCH)
            mDiscoverUserAdapter.userSearchGet(userSearchGetEvent.getSuggestedUsers());
    }
}
