package joseocampo.VehicleTrackerSystemApp.com;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoutesRequests extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONArray> {


    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<Loan> myLoans;
    private String userId;
    private String userName;
    private String userSurname;
    private List<ExpandObjects> loansItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_requests);

        getSupportActionBar().setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(),R.drawable.fondoazul)));
        getSupportActionBar().setTitle("Prestamos Activos");


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("usuario");
            userName= bundle.getString("name");
            userSurname= bundle.getString("surname");
        }


        myLoans = new ArrayList<>();
        loansItemView = new ArrayList<ExpandObjects>();

        readLoans();//load the loans from the DB
    }

    public void readLoans() {// in this method we connect to the database and load all the loan of the logged user and the selected vehicle.

        String url = "http://vtsmsph.com/loadCarLoans.php?user=david" + "&ident=" + userId;
        url.replace(" ", "%20");

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onResponse(JSONArray response) {

        createLoans(response);// we fill the loan list, for later use.
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "Ocurrio un error con los prestamos." , Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "No existen prestamos: " + error.toString(), Toast.LENGTH_SHORT).show();
    }

    public void createLoans(JSONArray pmyLoans) {

        for (int i = 0; i < pmyLoans.length(); i++) { //we fill a list with all the read lends of the bd.
            try {

                JSONObject jsonObject = pmyLoans.getJSONObject(i);
                Loan loan = new Loan();
                loan.setConsecutive(jsonObject.getInt("consecutivo"));
                loan.setDate(jsonObject.getString("PK_Date"));
                loan.setJustification(jsonObject.getString("Justification"));
                loan.setDetails(jsonObject.getString("Details"));
                loan.setUser(jsonObject.getString("name") + " " +
                        jsonObject.getString("surname")
                        + " " + jsonObject.getString("second_surname"));
                loan.setVehicle(jsonObject.getString("FK_Vehicle"));
                loan.setBeginHour(jsonObject.getString("beginHour"));
                loan.setEndHour(jsonObject.getString("endHour"));

                loansItemView.add(new ExpandObjects(loan.toString(), "", R.drawable.route, R.drawable.inicio));

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        fillListView();
        addEventListeners();
    }

    private void fillListView() {
        ArrayAdapter<ExpandObjects> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.loans_list_view);
        list.setAdapter(adapter);
    }

    private void addEventListeners() {
        ListView list = (ListView) findViewById(R.id.loans_list_view);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long id) {
                ExpandObjects ChosenObj = loansItemView.get(position);

                Mensaje("Seleccione el ícono de la derecha para iniciar el recorrido!");
            }
        });
    }

    public void Mensaje(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private class MyListAdapter extends ArrayAdapter<ExpandObjects> {
        public MyListAdapter() {
            super(RoutesRequests.this, R.layout.expandingobjects, loansItemView);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.expandingobjects, parent, false);
            }
            ExpandObjects ObjetoActual = loansItemView.get(position);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.ivdibujo);
            imageView.setImageResource(ObjetoActual.getNumDraw1());
            TextView elatributo01 = (TextView) itemView.findViewById(R.id.paraelatributo01);
            elatributo01.setText(ObjetoActual.getAtributte01());

            ImageView imageView2 = (ImageView) itemView.findViewById(R.id.ivdibujo2);
            imageView2.setImageResource(ObjetoActual.getNumDraw2());
            imageView2.setOnClickListener(new ButtonsOnClickListener(getContext()));

            return itemView;
        }
    }

    class ButtonsOnClickListener implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
        Context context;
        private RequestQueue requestQueue1;
        private JsonObjectRequest jsonObjectRequest;

        public ButtonsOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            ImageView imageView = (ImageView) v;
            ConstraintLayout constraintLayout = (ConstraintLayout) imageView.getParent();
            TextView textView = (TextView) constraintLayout.getChildAt(1);

            String plate = vehiclePlate(textView.getText().toString());

            int loanNumber = Integer.parseInt(loanNumber(textView.getText().toString()));

            String url = "http://vtsmsph.com/changeStatus.php?user=david" + "&route=" + loanNumber + "&state=1";
            url.replace(" ", "%20");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            requestQueue1 = Volley.newRequestQueue(getApplicationContext());
            requestQueue1.add(jsonObjectRequest);

            Intent intent = new Intent(getApplicationContext(), LocationView.class);
            intent.putExtra("usuario", userId);
            intent.putExtra("name", userName);
            intent.putExtra("surname", userSurname);
            intent.putExtra("loanNumber", loanNumber);
            intent.putExtra("vehiclePlaque",plate);

            startActivity(intent);
        }


        public String loanNumber(String string) {
            char[] aux = string.toCharArray();

            String loan_Number = "";
            for (int i = 0; i < aux.length; i++) {
                if (aux[i] == ' ') {
                    return loan_Number;
                } else {
                    loan_Number += aux[i];
                }
            }
            return loan_Number;
        }
        public String vehiclePlate(String string) {

            char[] aux = string.toCharArray();


            String plate = "";
            for (int i = (aux.length-1); i >0; i--) {
                if (aux[i] == ' ') {
                    for(int x=(i+1); x<aux.length; x++){
                        plate += aux[x];
                    }
                    i=0;
                } else {

                }
            }
            return plate;
        }

        @Override
        public void onResponse(JSONObject response) {}

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "No se encontraron prestamos."+error.toString() , Toast.LENGTH_LONG).show();
        }
    }

}
