package joseocampo.VehicleTrackerSystemApp.com;
import java.util.Calendar;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FailureReport extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONArray> {

    private ImageView save_failure;
    private Button exit_btn;

    private String userId;
    private String userName;
    private String userSurname;
    //private String loan;

    private EditText damageType_et, damageDetails_et, incidentDetails_et;
    private TextView user_tv;

    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue request;

    private Spinner lista;

    private static String idSolicitud = "consulta";
    private String[] vehicles = {"Selecciona un vehículo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failure_report);
        getSupportActionBar().setBackgroundDrawable(
                new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.fondos)));
        getSupportActionBar().setTitle("Reporte de averías");

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            userId = bundle.getString("usuario");
            userName= bundle.getString("name");
            userSurname = bundle.getString("surname");

            Toast.makeText(getApplicationContext(), " usuario: " + userName, Toast.LENGTH_LONG).show();
        }

        lista = findViewById(R.id.vehicleList);

        save_failure = (ImageView) findViewById(R.id.save_failure);
        exit_btn=findViewById(R.id.btnExit);

        damageType_et = findViewById(R.id.et_damageType);
        damageDetails_et = findViewById(R.id.et_damageDetails);
        incidentDetails_et = findViewById(R.id.et_incidentDetails);

        user_tv = findViewById(R.id.userRequest);
        user_tv.setText("Usuario actual: "+userName+" "+userSurname);

        loadVehicles();

        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_failure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFailure();
            }
        });
    }

    public String getPlate(String cadena) {
        char[] auxilar = cadena.toCharArray();
        String plate = "";
        for (int i = 0; i < auxilar.length; i++) {
            if (auxilar[i] == ' ') {
                return plate;
            } else {
                plate += auxilar[i];
            }
        }
        return plate;

    }

    public void loadVehicles() {


        String url = "http://vtsmsph.com/loadVehicles.php?" + "user=david";

        url.replace(" ", "%20");


        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

        request = Volley.newRequestQueue(getApplicationContext());

        request.add(jsonArrayRequest);


    }

    private void saveFailure() {
        //Toast.makeText(getApplicationContext(), "Conectarse a la bd ", Toast.LENGTH_LONG).show();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha = simpleDateFormat.format(new Date());


        String URL = "http://vtsmsph.com/incidentsReport.php?user=david"
                + "&incident_date=" + fecha
                + "&damage_details=" + damageDetails_et.getText().toString()
                + "&identification=" + userId
                + "&vehicle=" + getPlate(lista.getSelectedItem().toString())
                + "&damage_type=" + damageType_et.getText().toString()
                + "&incident_details=" + incidentDetails_et.getText().toString();



        URL.replace(" ", "%20");
        idSolicitud = "solicitud";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, this, this);

        request = Volley.newRequestQueue(getApplicationContext());
        request.add(jsonArrayRequest);

    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONArray response) {

//----------------------------------------------------------------------------------------------------------------------------------------------//

        try {

            if (idSolicitud.equals("solicitud")) {
                JSONObject jsonObject = new JSONObject();
                jsonObject = response.getJSONObject(0);

                if (jsonObject.getString("Resultado").equals("1")) {

                    Toast.makeText(getApplicationContext(), "Se registro el reporte exitosamente!", Toast.LENGTH_LONG).show();

                } else {
                    if (jsonObject.getString("Resultado").equals("0")) {

                        Toast.makeText(getApplicationContext(), "Error el regitrar el reporte ", Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(getApplicationContext(), "Error desconocido", Toast.LENGTH_LONG).show();

                    }
                }

            } else {
                JSONObject jsonObject = new JSONObject();


                vehicles = new String[response.length()];
                for (int i = 0; i < response.length(); i++) {

                    jsonObject = response.getJSONObject(i);
                    vehicles[i] = jsonObject.getString("PK_License_plate") + " " + jsonObject.getString("Brand") + " " + jsonObject.getString("Model");

                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicles);
                lista.setAdapter(adaptador);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//----------------------------------------------------------------------------------------------------------------------------------------------//

    }
}
