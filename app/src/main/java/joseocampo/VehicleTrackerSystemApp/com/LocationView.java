package joseocampo.VehicleTrackerSystemApp.com;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationView extends FragmentActivity implements OnMapReadyCallback,
        Response.ErrorListener, Response.Listener<JSONObject> {

    private GoogleMap mMap;
    private Button btnGPS, btnNormal, btnHybrid, btnLand, btnSatelital;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private String street = "";
    private String vehicle = "";
    private TextView txtDireccion, txtCoordenadas;
    private int loanNumber;
    private String userId;
    private String userLoginName;
    private String userLoginSurname;
    private MarkerOptions myMarker;
    private double latitude, longitude;
    private ImageView image_terminar;
    private ArrayList<LatLng> milista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnNormal = (Button) findViewById(R.id.btn_normal);
        btnHybrid = (Button) findViewById(R.id.btnHybrid);
        btnLand = (Button) findViewById(R.id.btn_land);
        btnSatelital = (Button) findViewById(R.id.btnSatelital);
        image_terminar = (ImageView) findViewById(R.id.image_terminar);

        //agregamos eventos a los botones de cambiar el tipo de vista para el ampa.
        addEvents();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("usuario");
            userLoginName= bundle.getString("name");
            userLoginSurname= bundle.getString("surname");
            loanNumber = bundle.getInt("loanNumber");
            vehicle = bundle.getString("vehiclePlaque");

            Toast.makeText(getApplicationContext(), " usuario: " + userId + " LoanNumber: " + loanNumber, Toast.LENGTH_LONG).show();
        }

        //iniciamos el recorrido
        initTravel();


    }

    private void initTravel() {
        int permissionCheck =
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                //si el permiso no está denegado, hacemos la solicitud del permiso.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }

        //Toast.makeText(getApplicationContext(), "Hola dentro de onclick", Toast.LENGTH_SHORT).show();

        LocationManager locationManager =
                (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        final boolean gpsActivado = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsActivado) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Para que la aplicacion funcione es recomandable activar GPS !");
            builder.setMessage("Desea activar GPS ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "Activando GPS", Toast.LENGTH_SHORT).show();
                    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settingsIntent);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "La aplicacion no funciona sin GPS", Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();


        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                sendData(vehicle+";"+location.getLatitude()+";"+location.getLongitude());
                //Format: VehiclePlate;Latitud;Longitud
                setDirection(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        //se registra el listener con el loctaion manager para recivir actualizacion de localizacion.

        permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    public void sendData(String message) {

        BackgroundTask bt = new BackgroundTask();
        bt.execute(message);

    }

    private void failureReport() {

        //Se cambia el estado de la solicitud de vehiculo a finalizada.
        String url = "http://vtsmsph.com/changeStatus.php?user=david" + "&route=" + loanNumber + "&state=0";
        url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request = Volley.newRequestQueue(getApplicationContext());
        request.add(jsonObjectRequest);//aqui le enviamos la respuesta de la bd, a el metodo response.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reportar Averías !");
        builder.setMessage("Desea reportar alguna avería del vehículo ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent settingsIntent = new Intent(LocationView.this, FailureReport.class);
                settingsIntent.putExtra("usuario", userId);
                settingsIntent.putExtra("name", userLoginName);
                settingsIntent.putExtra("surname", userLoginSurname);
                startActivity(settingsIntent);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishTravel();
            }
        });

        builder.show();
    }

    public void setDirection(Location location) {
        if (location != null) {
            if (location.getLongitude() != 0 && location.getLatitude() != 0) {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (!addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        // txtDireccion.setText("Direccion:  " + address.getAddressLine(0));

                        if (street.equals(address.getAddressLine(0))) {


                        } else {

                            saveStreet(address.getAddressLine(0));
                            setCurrentLocation(location.getLatitude(), location.getLongitude());
                            street = address.getAddressLine(0);

                        }

                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void saveStreet(String calle) {

        String url = new StringBuilder().append("http://vtsmsph.com/guardarCalle.php?user=tony").append("&route=").append(String.valueOf(loanNumber)).append("&calle=").append(calle).toString();

        url.replace(" ", "%20");


        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);

        request = Volley.newRequestQueue(getApplicationContext());
        request.add(jsonObjectRequest);//aqui le enviamos la respuesta de la bd, a el metodo response.
    }

    private void changeTypeHybrid() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    private void changeTypeLand() {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    }

    private void changeTypeSatelital() {
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }

    private void changeTypeNormal() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    public void addEvents() {

        btnHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeHybrid();
            }
        });
        btnNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeNormal();
            }
        });
        btnLand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeLand();
            }
        });
        btnSatelital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeSatelital();
            }
        });
        image_terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                failureReport();
            }
        });
    }

    private void finishTravel() {

        Intent intent = new Intent(getApplicationContext(), FailureReport.class);
        intent.putExtra("usuario", userId);
        intent.putExtra("name", userLoginName);
        intent.putExtra("surname", userLoginSurname);
        startActivity(intent);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(9.9948033, -84.0982678);

        myMarker = new MarkerOptions().position(myLocation).title("Mi ubicacion: " + myLocation.latitude + " " + myLocation.longitude).icon(BitmapDescriptorFactory.fromResource(R.drawable.car3));

        mMap.addMarker(myMarker);
        milista.add(myLocation);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        //activamos los botones de zomm en el mapa.
        mMap.getUiSettings().setZoomControlsEnabled(true);


        //preguntamos si tenemos los permisos de usar mi ubicacion del celular.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void setCurrentLocation(double x, double y) {
        LatLng myLocation = new LatLng(x, y);
        myMarker.position(myLocation);
        milista.add(myLocation);

        mMap.addMarker(new MarkerOptions().position(myLocation).title("Mi ubicacion" + x + " " + y).icon(BitmapDescriptorFactory.fromResource(R.drawable.mark)));
        drawRoute();
    }

    private void drawRoute() {

        for (int i = 0; i < milista.size() - 1; i++) {
            mMap.addPolyline(new PolylineOptions().width(20)
                    .add(milista.get(i))
                    .add(milista.get(i + 1)).width(5).color(Color.BLACK));
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Error " + error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getApplicationContext(), "Response " + response.toString(), Toast.LENGTH_LONG).show();
    }


    class BackgroundTask extends AsyncTask<String,Void,Void> {
        PrintWriter writer;
        Socket socket;
        @Override
        protected Void doInBackground(String... voids) {
            try{
                String message = voids[0];
                socket = new Socket("192.168.0.3",6000);
                writer = new PrintWriter(socket.getOutputStream());
                writer.write(message.toString());
                writer.flush();
                writer.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}
