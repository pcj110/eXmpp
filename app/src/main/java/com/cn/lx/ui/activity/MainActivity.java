package com.cn.lx.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.lx.entries.UserInfoEntrie;
import com.cn.lx.ui.base.BaseActivity;
import com.cn.lx.ui.fragment.ContactsFragment;
import com.cn.lx.ui.fragment.MsgContactsFragment;
import com.cn.lx.xmpp.XmppManager;
import com.cn.lx.xmpp.XmppService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import estar.com.xmpptest.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    public boolean isDefaultToolBar() {
        return false;
    }

    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.userName)
    TextView userName;
    @BindView(R.id.describe)
    TextView describe;
     FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         fab= (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,new ContactsFragment()).addToBackStack("ContactsFragment").commit();
                fab.setVisibility(View.GONE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);
        userName = (TextView) view.findViewById(R.id.userName);
        describe = (TextView) view.findViewById(R.id.describe);
//        ButterKnife.bind(navigationView);

        init();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentLayout,new MsgContactsFragment()).commit();
    }

    private void init(){

    }

    @Override
    public void manageInitSuccess() {
        super.manageInitSuccess();
        UserInfoEntrie userInfoEntrie = manager.getClient().getUserInfo(null);
        if (userInfoEntrie!=null) {
            userName.setText(userInfoEntrie.getNiceName());
            describe.setText("naxieren");
        }
    }

    long lastTime = 0;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getSupportFragmentManager().popBackStackImmediate()){
            fab.setVisibility(View.VISIBLE);
        }else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime < 2000) {
                if (manager!=null) {
                    manager.getClient().loginOut();
                }
                super.onBackPressed();
            } else {
                lastTime = currentTime;
                Toast.makeText(this, "请再按一次退出系统", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(this,SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_person) {
            startActivity(UserInfoActivity.callingIntent(this,getUserName()));

        } else if (id == R.id.nav_maskPerson) {

        }  else if (id == R.id.nav_setting) {

            startActivity(new Intent(this,SettingsActivity.class));
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
