package net.dunrou.mobile.activity;

import android.os.Bundle;

import net.dunrou.mobile.R;
import net.dunrou.mobile.bean.BaseActivity;
import net.dunrou.mobile.fragment.ProfileFragment;

public class ProfileActivity extends BaseActivity {

    public static String CURRENT_USERID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CURRENT_USERID = getIntent().getStringExtra("CURRENT_USERID");
        openFragment(R.id.profile_container, ProfileFragment.newInstance(CURRENT_USERID));
    }
}
