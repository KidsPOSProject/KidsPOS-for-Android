package info.nukoneko.cuc.kidspos.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import info.nukoneko.cuc.kidspos.R;
import info.nukoneko.cuc.kidspos.common.CommonActivity;
import info.nukoneko.cuc.kidspos.navigation.NavigationView;

/**
 * created at 2015/06/13.
 */
public class TopPageActivity extends CommonActivity {
    @InjectView(R.id.tool_bar) Toolbar toolbar;
    @InjectView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @InjectView(R.id.navigation_view) NavigationView mNavigation;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher);

        mDrawerToggle = getDrawerToggle();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setDrawerIndicatorEnabled(true);


        mNavigation.setStoreName("もんげー");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle getDrawerToggle(){
        return new ActionBarDrawerToggle(
                        this,
                        mDrawerLayout,
                        R.string.navigation_open,
                        R.string.navigation_close);
    }
}
