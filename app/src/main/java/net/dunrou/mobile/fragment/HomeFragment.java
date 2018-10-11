package net.dunrou.mobile.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lzy.ninegrid.NineGridView;

import net.dunrou.mobile.R;
import net.dunrou.mobile.activity.MainActivity;
import net.dunrou.mobile.base.message.CommentMessage;
import net.dunrou.mobile.base.message.LikeMessage;
import net.dunrou.mobile.base.message.RefreshMessage;
import net.dunrou.mobile.bean.EventAdapter;
import net.dunrou.mobile.bean.EventItem;
import net.dunrou.mobile.bean.IGetLocation;
import net.dunrou.mobile.bean.PicassoImageLoader;
import net.dunrou.mobile.network.firebaseNetwork.FirebaseUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    boolean isDisplayed;
    boolean isTime;

    @BindView(R.id.ptr)
    PtrClassicFrameLayout ptr;
    @BindView(R.id.user_feed_list_view)
    ListView listView;
    @BindView(R.id.panel_arrow)
    TextView arrow;
    @BindView(R.id.panel)
    LinearLayout panel;
    @BindView(R.id.sort_time)
    TextView sortTime;
    @BindView(R.id.sort_location)
    TextView sortLocation;

    @OnClick(R.id.panel_arrow)
    public void arrowListener() {
        if (isDisplayed) {
            isDisplayed = false;
            arrow.setBackground(getActivity().getDrawable(R.drawable.arrow));
            panel.setVisibility(View.GONE);
        } else {
            isDisplayed = true;
            arrow.setBackground(getActivity().getDrawable(R.drawable.back_arrow));
            panel.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.sort_time)
    public void timeListener() {
        arrow.setBackground(getActivity().getDrawable(R.drawable.arrow));
        panel.setVisibility(View.GONE);
        if (!isTime) {
            isDisplayed = false;
            sortTime.setClickable(false);
            sortLocation.setClickable(false);
            sortTime.setTextColor(Color.rgb(255, 220, 112));
            sortLocation.setTextColor(Color.rgb(255, 255, 255));
            isTime = true;
            initData();
        }
    }

    @OnClick(R.id.sort_location)
    public void locationListener() {
        arrow.setBackground(getActivity().getDrawable(R.drawable.arrow));
        panel.setVisibility(View.GONE);
        if (isTime) {
            isDisplayed = false;
            sortLocation.setClickable(false);
            sortTime.setClickable(false);
            sortLocation.setTextColor(Color.rgb(255, 220, 112));
            sortTime.setTextColor(Color.rgb(255, 255, 255));
            isTime = false;
            initData();
        }
    }

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
        isDisplayed = false;
        isTime = true;

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
        while (getActivity() == null) {}
        ((IGetLocation) getActivity()).getLocation();
    }

    public void initData(Location location) {
        String username = userInfo.getString("username", "1");
        firebaseUtil.getEventPost(username, isTime, location);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshMessage(RefreshMessage refreshMessage) {
        data = refreshMessage.getEventItems();
        adapter.setData(data);
        ptr.refreshComplete();
        sortTime.setClickable(true);
        sortLocation.setClickable(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLikeMessage(LikeMessage likeMessage) {
        firebaseUtil.refreshLike(likeMessage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentMessage(CommentMessage commentMessage) {
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
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
