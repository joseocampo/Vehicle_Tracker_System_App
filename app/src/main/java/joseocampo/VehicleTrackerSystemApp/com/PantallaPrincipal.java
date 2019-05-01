package joseocampo.VehicleTrackerSystemApp.com;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class PantallaPrincipal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentSolicitarVhiculo.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        WelcomePage.OnFragmentInteractionListener
        {

    private String userId;
    private String userName;
    private String userSurname;
    private TextView txtuserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(
                new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.fondoazul)));
        getSupportActionBar().setTitle("Bienvenido ");




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_main,new WelcomePage()).commit();





        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("usuario");
            userName = extras.getString("name");
            userSurname = extras.getString("surname");

            getSupportActionBar().setTitle("");
            getSupportActionBar().setTitle("Bienvenido(a): " + userName);

            View view  = navigationView.getHeaderView(0);
            txtuserName = (TextView)view.findViewById(R.id.txtUserName);
            txtuserName.setText(userName+" "+userSurname);

        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pantalla_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Fragment fragment = null;
        boolean selectedFragment = false;

        if (id == R.id.solicitudVehiculo) {
            Bundle bundle = new Bundle();
            bundle.putString("usuario", userId);
            fragment = new FragmentSolicitarVhiculo();
            fragment.setArguments(bundle);
            selectedFragment  = true;

        } else if (id == R.id.reporteAveria) {

            Bundle bundle = new Bundle();
            bundle.putString("usuario", userId);
            bundle.putString("name", userName);
            bundle.putString("surname", userSurname);
            Intent intent = new Intent(getApplicationContext(),FailureReport.class);
            intent.putExtra("usuario", userId);
            intent.putExtra("name", userName);
            intent.putExtra("surname", userSurname);
            startActivity(intent);

        } else if (id == R.id.make_loan) {
            Bundle bundle = new Bundle();
            bundle.putString("usuario", userId);
            bundle.putString("name", userName);
            bundle.putString("surname", userSurname);
            Intent intent = new Intent(getApplicationContext(), RoutesRequests.class);
            intent.putExtra("usuario", userId);
            intent.putExtra("name", userName);
            intent.putExtra("surname", userSurname);
            startActivity(intent);


        } else if (id == R.id.my_profile) {
            Bundle bundle = new Bundle();
            bundle.putString("user_Id_Profile", userId);
            bundle.putString("user_Name_Profile", userName);
            bundle.putString("user_Surname_Profile", userSurname);
            fragment = new ProfileFragment();
            fragment.setArguments(bundle);
            selectedFragment  = true;

        }else if(id == R.id.welcome_page){
            fragment = new WelcomePage();
            selectedFragment  = true;
        }
        if (selectedFragment) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
