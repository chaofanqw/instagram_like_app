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
import android.widget.SearchView;
import android.widget.Toast;

import net.dunrou.mobile.R;
import net.dunrou.mobile.bean.DiscoverUserAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;
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
        EventBus.getDefault().register(this);
        mContext = getActivity().getApplicationContext();
    }

    public void searchUsers(String query) {
//        TODO search user query
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        Intent intent = new Intent(getActivity(), SearchableActivity.class);
//        startActivity(intent);

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_discover, container, false);
        mSearch_SV = mView.findViewById(R.id.search_SV);

        mSearchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearch_SV.setSearchableInfo(mSearchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearch_SV.setIconifiedByDefault(false);

        mSearch_SV.setSubmitButtonEnabled(true);
        mSearch_SV.setOnQueryTextListener(this);

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

    @OnClick(R.id.top_TI)
    public void clickTop() {
        Log.d(TAG, "click top");
    }

    @OnClick(R.id.people_TI)
    public void clickPeople() {
        Log.d(TAG, "click people");
    }

    @OnClick(R.id.nearby_TI)
    public void clickNearby() {
        Log.d(TAG, "click nearby");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewUser(DiscoverUserAdapter.NewUserEvent newUserEvent) {
        mDiscoverUserAdapter.addUser(newUserEvent.getSuggestedUser());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationAdded(DiscoverUserAdapter.RelationAddedEvent relationAddedEvent) {
//        TODO change follow button status
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRelationAddFail(DiscoverUserAdapter.RelationAddFailEvent relationAddFailEvent) {
        Toast toast = Toast.makeText(mContext, "follow fail", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetRelationships(DiscoverUserAdapter.UpdateRelationshipEvent updateRelationshipEvent) {
        mDiscoverUserAdapter.setRelationship(updateRelationshipEvent.getFirebaseRelationships());
    }
}
