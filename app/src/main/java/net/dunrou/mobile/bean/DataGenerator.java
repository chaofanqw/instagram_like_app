package net.dunrou.mobile.bean;

import android.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.dunrou.mobile.R;
import net.dunrou.mobile.fragment.DiscoverFragment;
import net.dunrou.mobile.fragment.HomeFragment;
import net.dunrou.mobile.fragment.PhotoFragment;

/**
 * Created by Stephen on 2018/8/29.
 */

public class DataGenerator {

    public static final int []mTabRes = new int[]{R.drawable.home_n, R.drawable.search_n,
                                                    R.drawable.photo_n,R.drawable.message_n,
                                                    R.drawable.profile_n};
    public static final int []mTabResPressed = new int[]{R.drawable.home_p, R.drawable.search_p,
                                                            R.drawable.photo_p,R.drawable.message_p,
                                                            R.drawable.profile_p};
    public static final String []mTabTitle = new String[]{"User Feed","Discover","Upload photo","Activity Feed","Profile"};

    public static Fragment[] getFragments(String from){
        Fragment fragments[] = new Fragment[5];
        fragments[0] = HomeFragment.newInstance();
        fragments[1] = DiscoverFragment.newInstance();
        fragments[2] = null;
        fragments[3] = HomeFragment.newInstance();
        fragments[4] = HomeFragment.newInstance();
        return fragments;
    }

    /**
     * 获取Tab 显示的内容
     * @param context
     * @param position
     * @return
     */
    public static View getTabView(Context context, int position){
        View view = LayoutInflater.from(context).inflate(R.layout.home_tab_content,null);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_content_image);
        tabIcon.setImageResource(DataGenerator.mTabRes[position]);
        TextView tabText = (TextView) view.findViewById(R.id.tab_content_text);
        tabText.setText(mTabTitle[position]);
        return view;
    }
}

