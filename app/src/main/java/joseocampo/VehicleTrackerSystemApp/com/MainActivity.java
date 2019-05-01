package joseocampo.VehicleTrackerSystemApp.com;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
// hola moi

public class MainActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener {
    private ImageView image_login;
    private EditText txtUser, txtPassword;


    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.fondoazul)));

        image_login = (ImageView) findViewById(R.id.image_login);
        txtUser = (EditText) findViewById(R.id.txtUser);
        txtPassword = (EditText) findViewById(R.id.txtPassWord);

        image_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iniciarSesion(v);
            }
        });
    }
    private boolean validUser(){
        String message = txtUser.getText().toString().trim();
        if(message.isEmpty()){
            txtUser.setError("Campo usuario está vacío");
            return false;
        }else{
            txtUser.setError(null);
            return  true;
        }
    }
    private boolean validPassword(){
        String message = txtPassword.getText().toString().trim();
        if(message.isEmpty()){
            txtPassword.setError("Campo contraseña está vacío");
            return false;
        }else{
            txtPassword.setError(null);
            return  true;
        }
    }

    public void iniciarSesion(View view) {
        if(!validUser() | !validPassword()){
            return;
        }else{
            String url = "http://vtsmsph.com/login.php?" + "user=" + txtUser.getText().toString()
                    + "&password=" + txtPassword.getText().toString();

            url.replace(" ", "%20");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this); //comunication with onErrorResponse() y onResponse() methods

            request = Volley.newRequestQueue(getApplicationContext());


            request.add(jsonObjectRequest);//send the response to the DB, to the response method.
        }


    }

    @Override
    public void onResponse(JSONObject response) {

        try {
            
            if (response.getString("User") != null) {

                Intent intent = new Intent(this, PantallaPrincipal.class);

//-----------------send user credentials to the new/next activity--------------------------------------------//
                intent.putExtra("usuario",response.getString("User"));
                intent.putExtra("name",response.getString("Name"));
                intent.putExtra("surname", response.getString("Surname"));

//----------------------------------------------------------------------------------------------------------//

                Toast.makeText(this, "Ha iniciado sesión con éxito!", Toast.LENGTH_LONG).show();
                startActivity(intent);

            }else{
                Toast.makeText(this,"Usuario o contraseña incorrectos",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Toast.makeText(this,"Usuario o contraseña incorrectos",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Ha ocurrido un Error " + error.toString(), Toast.LENGTH_SHORT).show();
    }
}
