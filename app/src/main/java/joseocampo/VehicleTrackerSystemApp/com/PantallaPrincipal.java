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
        FragmentRoutesRequest.OnFragmentInteractionListener,
        FragmentFailure.OnFragmentInteractionListener {

    private TextView txtUserName;
    private String userId;
    private String userName;
    private String userSurname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setBackgroundDrawable(
                new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.fondos)));
        getSupportActionBar().setTitle("Bienvenido ");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //colocamos el userName en la pantalla principal

        //txtUserName.setText("hola");


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userId = extras.getString("usuario");
            userName = extras.getString("name");
            userSurname = extras.getString("surname");

            getSupportActionBar().setTitle("");
            getSupportActionBar().setTitle("Bienvenido(a): " + userName);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pantalla_principal, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragmento = null;
        boolean fragmentoSeleccionado = false;

        if (id == R.id.solicitudVehiculo) {
            Bundle bundle = new Bundle();
            bundle.putString("usuario", userId);
            fragmento = new FragmentSolicitarVhiculo();
            fragmento.setArguments(bundle);
            fragmentoSeleccionado = true;

        } else if (id == R.id.reporteAveria) {
            //probando commits

            Bundle bundle = new Bundle();
            bundle.putString("usuario", userId);
            bundle.putString("name", userName);
            bundle.putString("surname", userSurname);
            Intent intento = new Intent(getApplicationContext(),FailureReport.class);
            intento.putExtra("usuario", userId);
            intento.putExtra("name", userName);
            intento.putExtra("surname", userSurname);
            startActivity(intento);

        } else if (id == R.id.make_loan) {
            Bundle bundle = new Bundle();
            bundle.putString("usuario", userId);
            bundle.putString("name", userName);
            bundle.putString("surname", userSurname);
            Intent intento = new Intent(getApplicationContext(), RoutesRequests.class);
            intento.putExtra("usuario", userId);
            intento.putExtra("name", userName);
            intento.putExtra("surname", userSurname);
            startActivity(intento);


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        if (fragmentoSeleccionado) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main, fragmento).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}