package net.dunrou.mobile.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lzy.ninegrid.NineGridView;

import net.dunrou.mobile.R;
import net.dunrou.mobile.base.firebaseClass.FirebaseEventPost;
import net.dunrou.mobile.base.message.CommentMessage;
import net.dunrou.mobile.base.message.LikeMessage;
import net.dunrou.mobile.base.message.RefreshMessage;
import net.dunrou.mobile.bean.EventAdapter;
import net.dunrou.mobile.bean.EventItem;
import net.dunrou.mobile.bean.PicassoImageLoader;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SharedPreferences userInfo;
    View userFeedView;
    FirebaseUtil firebaseUtil;

    @BindView(R.id.ptr) PtrClassicFrameLayout ptr;
    @BindView(R.id.user_feed_list_view) ListView listView;

    private EventAdapter adapter;
    private ArrayList<EventItem> data;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
//        if (getArguments() != null) {
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userFeedView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, userFeedView);
        return userFeedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userInfo = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE);
        firebaseUtil = new FirebaseUtil();
        data = new ArrayList<>();
        View emptyView = View.inflate(this.getActivity(), R.layout.item_empty, null);
        listView.setEmptyView(emptyView);

        adapter = new EventAdapter(this.getActivity(), new ArrayList<EventItem>(), userInfo.getString("username", "1"));
        listView.setAdapter(adapter);

        NineGridView.setImageLoader(new PicassoImageLoader());

        initData();

        ptr.setLastUpdateTimeRelateObject(this);
        ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                initData();
            }
        });
    }

    public void initData() {
        String username = userInfo.getString("username", "1");
        firebaseUtil.getEventPost(username);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshMessage(RefreshMessage refreshMessage){
        data = refreshMessage.getEventItems();
        adapter.setData(data);
        ptr.refreshComplete();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeMessage(LikeMessage likeMessage){
        firebaseUtil.refreshLike(likeMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentMessage(CommentMessage commentMessage){
        firebaseUtil.refreshComment(commentMessage);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
