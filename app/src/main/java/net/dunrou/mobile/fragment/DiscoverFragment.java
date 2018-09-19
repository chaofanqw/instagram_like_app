package net.dunrou.mobile.fragment;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.MainActivity;
import net.dunrou.mobile.bean.DiscoverUserAdapter;
//import net.dunrou.mobile.activity.SearchableActivity;

/**
 * Created by yvette on 2018/9/18.
 */

public class DiscoverFragment extends Fragment implements SearchView.OnQueryTextListener {
    private transient final static String TAG = DiscoverFragment.class.getSimpleName();

    private DiscoverFragment.OnFragmentInteractionListener mListener;

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
        mDiscoverUserAdapter = new DiscoverUserAdapter();
        mResults_RV = mView.findViewById(R.id.results_RV);
        mResults_RV.setAdapter(mDiscoverUserAdapter);
        mResults_RV.setLayoutManager(new LinearLayoutManager(getActivity()));

        return mView;
    }

    public void initializeAdapter(MainActivity mainActivity) {
        mDiscoverUserAdapter = new DiscoverUserAdapter(mainActivity);
    }


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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
