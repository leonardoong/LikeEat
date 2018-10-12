package com.example.android.likeeatapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.likeeatapplication.Adapter.PageAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TabFragment1.OnFragmentInteractionListener,
        TabFragment2.OnFragmentInteractionListener {

    private FirebaseAuth mAuth;
    private NavigationView navigationView;

    private TextView user_name, user_email;
    private ImageView user_pic;
    private GoogleSignInClient mGoogleSignInClient;

    private FloatingActionButton floatingActionButton;

    public static final String table1 = "Post";
    public static final String table2 = "Comment";
    public static final String table3 = "user";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase authentication & deklarasi variabel
        mAuth = FirebaseAuth.getInstance();

        this.setTitle("Home");

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //membuat drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main,null);
        user_name = (TextView) header.findViewById(R.id.user_name);
        user_email = (TextView) header.findViewById(R.id.user_email);
        user_pic = (ImageView) header.findViewById(R.id.imageView);
        navigationView.addHeaderView(header);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Post Terbaru"));
        tabLayout.addTab(tabLayout.newTab().setText("Recommended"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_logout){
            mAuth.signOut();
            Intent toLogin = new Intent(MainActivity.this, MainActivity.class);
            startActivity(toLogin);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_beranda) {
            Intent home = new Intent(MainActivity.this, MainActivity.class);
            startActivity(home);
        }else if(id == R.id.nav_profile){
            Intent profile = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(profile);
        }else if(id == R.id.nav_weather) {
            Intent recommend = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(recommend);
        }else if(id == R.id.nav_news) {
            Intent recommend = new Intent(MainActivity.this, activity_news.class);
            startActivity(recommend);
        }else if (id == R.id.nav_login) {
            Intent login = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(login);
        } else if (id == R.id.nav_logout){
            FirebaseUser user = mAuth.getCurrentUser();
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (user != null){
                mAuth.signOut();
            }else if(acct != null){
                mGoogleSignInClient.signOut();
            }
            Toast.makeText(MainActivity.this, "Logout Berhasil", Toast.LENGTH_SHORT).show();
            Intent toLogin = new Intent(MainActivity.this, MainActivity.class);
            SharedPreferences.Editor prefs = getSharedPreferences("userSession", MODE_PRIVATE).edit();
            prefs.clear();
            prefs.commit();
            startActivity(toLogin);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences prefs = getSharedPreferences("userSession", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(user != null){
            floatingActionButton.setVisibility(View.VISIBLE);
            /*for(int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++){
                MenuItem menuItem= menu.getItem(menuItemIndex);
                if(menuItem.getItemId() == R.id.nav_login){
                    menuItem.setVisible(false);
                }else if (menuItem.getItemId() == R.id.nav_logout){
                    menuItem.setVisible(true);
                }
            }**/
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            loadProfile();
        }else if(user == null && acct == null){
            floatingActionButton.setVisibility(View.INVISIBLE);
            /*for(int menuItemIndex = 0; menuItemIndex < menu.size(); menuItemIndex++){
                MenuItem menuItem= menu.getItem(menuItemIndex);
                if(menuItem.getItemId() == R.id.nav_login){
                    menuItem.setVisible(true);
                }else if (menuItem.getItemId() == R.id.nav_logout){
                    menuItem.setVisible(false);
                }

            }**/
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
        }else if(user != null && email.equals("admin@admin.com")){
            Toast.makeText(MainActivity.this,"YEAHH", Toast.LENGTH_SHORT).show();
        }else if(acct != null){
            floatingActionButton.setVisibility(View.VISIBLE);
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            loadProfile();
        }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void searchFood(View view) {
        Intent toSearch = new Intent(MainActivity.this, SearchPost.class);
        startActivity(toSearch);
    }

    private void loadProfile() {
        SharedPreferences prefs = getSharedPreferences("userSession", MODE_PRIVATE);
        String name = prefs.getString("nama", "");
        String profile_pic = prefs.getString("profile_pic", "");
        String email = prefs.getString("email", "");
        String ttl = prefs.getString("ttl", "");
        String phone = prefs.getString("phone", "");

        Picasso.get().load(profile_pic).placeholder(R.drawable.ic_launcher_foreground).into(user_pic);

        user_name.setText(name);
        user_email.setText(email);
    }
}
