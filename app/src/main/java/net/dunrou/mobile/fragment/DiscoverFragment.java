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
import android.widget.Toast;

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

//    private DiscoverFragment.OnFragmentInteractionListener mListener;

    private View mView;

    private SearchManager mSearchManager;
    private DiscoverUserAdapter mDiscoverUserAdapter;
    private Context mContext;

    private SearchView mSearch_SV;
    private TabLayout mDiscoverNavBar_TL;
    private RecyclerView mResults_RV;
    private Button mSearchCancel_BT;

    private TabLayout mDiscover_TL;

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
    }

    public void searchUsers(String query) {
        new FirebaseUtil().searchUser(query);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
//        Intent intent = new Intent(getActivity(), SearchableActivity.class);
//        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_discover, container, false);
        mSearch_SV = mView.findViewById(R.id.search_SV);
        mSearchCancel_BT = mView.findViewById(R.id.searchCancel_BT);

        mSearchCancel_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO exit search mode
                mSearch_SV.clearFocus();
            }
        });

        mSearchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearch_SV.setSearchableInfo(mSearchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearch_SV.setIconifiedByDefault(false);

        mSearch_SV.setSubmitButtonEnabled(true);
        mSearch_SV.setOnQueryTextListener(this);

        mDiscover_TL = mView.findViewById(R.id.discover_TL);
        TabLayout.Tab tab = mDiscover_TL.getTabAt(1);
        tab.select();
        mDiscover_TL.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0:
                        Log.d(TAG, "click top");
                        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TOP);
                        break;
                    case 1:
                        Log.d(TAG, "click people");
                        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.COMMON_FRIENDS);
                        break;
                    case 2:
                        Log.d(TAG, "click nearby");
                        mDiscoverUserAdapter.setSuggestMode(DiscoverUserAdapter.TEST);
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

        //TODO initialize in MainActivity
//        mDiscoverUserAdapter = new DiscoverUserAdapter();
        initializeAdapter();
        mResults_RV = mView.findViewById(R.id.results_RV);
        mResults_RV.setAdapter(mDiscoverUserAdapter);
        mResults_RV.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mView;
    }

    public void initializeAdapter() {
        mDiscoverUserAdapter = new DiscoverUserAdapter(getActivity().getApplicationContext());
    }

    //TODO bug in UI click search, search bar disappear


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "receive query: " + query);
        searchUsers(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "receive text change: " + newText);
        searchUsers(newText);
        return false;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onRelationAdded(DiscoverUserAdapter.RelationAddedEvent relationAddedEvent) {
////        TODO change follow button status
//    }

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
        mDiscoverUserAdapter.userSearchGet(userSearchGetEvent.getSuggestedUsers());
    }
}
