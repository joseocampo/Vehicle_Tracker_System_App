package joseocampo.VehicleTrackerSystemApp.com;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class FragmentSolicitarVhiculo extends Fragment implements Response.Listener<JSONArray>, Response.ErrorListener {
    // TODO: Rename parameter arguments, choose names that match

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String idrequest = "consulta"; //Variable to determine the type of transaction to the database

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText destiny_field, justification_field;
    private Button btnMakeRequest, btnSelectbeginHour, btnSelectEndHour, btnSelectEndDate, btnSelectInitDate;
    private TextView userRequest, vehicleRequest, targetRequest, dateTimerequest;

    private Spinner vehicle_list;
    private String[] vehicles = {"Selecciona un vehículo"};

    private OnFragmentInteractionListener mListener;
    private JsonArrayRequest jsonArrayRequest;
    private RequestQueue request;

    private int beginHour, beginMinutes, endHour, endMinutes = -1;

    private String userNameLogin;

    public FragmentSolicitarVhiculo() {}

    public static FragmentSolicitarVhiculo newInstance(String param1, String param2) {
        FragmentSolicitarVhiculo fragment = new FragmentSolicitarVhiculo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_fragment_solicitar_vhiculo, container, false);

        btnSelectbeginHour = (Button) vista.findViewById(R.id.btnBeginHour);
        btnSelectEndHour = (Button) vista.findViewById(R.id.btnEndHour);
        btnSelectEndDate = (Button) vista.findViewById(R.id.endDate);
        btnSelectInitDate = (Button) vista.findViewById(R.id.initDate);

        vehicle_list = (Spinner) vista.findViewById(R.id.vehicleList);
        destiny_field = (EditText) vista.findViewById(R.id.destinyField);
        justification_field = (EditText) vista.findViewById(R.id.justificationField);
        btnMakeRequest = (Button) vista.findViewById(R.id.btnMakeRequest);

        userRequest = (TextView) vista.findViewById(R.id.userRequest);
        vehicleRequest = (TextView) vista.findViewById(R.id.vehicleRequest);
        targetRequest = (TextView) vista.findViewById(R.id.targetRequest);
        dateTimerequest = (TextView) vista.findViewById(R.id.dateTimeRequest);

        userNameLogin = getArguments().getString("usuario");

        userRequest.setText("       -- ningún vehículo solicitado --"); //we put this text to indicate that the request is empty because no vehicle has been requested.

        loadVehicles(); //The selection of available vehicles is filled when you enter the page to request a vehicle.

        btnMakeRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebService();
            }
        });

//------------------------------------Choses the start and end hour of the travel-----------------------------------------------//

        btnSelectbeginHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                beginHour = calendar.get(Calendar.HOUR_OF_DAY);
                beginMinutes = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int beginHourOfDay, int minute) {
                        btnSelectbeginHour.setText("Hora inicial:  " + beginHourOfDay + ": " + minute);
                        beginHour = beginHourOfDay;
                        beginMinutes = minute;
                    }
                }, beginHour, beginMinutes, false);

                timePickerDialog.show();
            }
        });

        btnSelectEndHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                endHour = calendar.get(Calendar.HOUR_OF_DAY);
                endMinutes = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int beginHourOfDay, int minute) {
                        btnSelectEndHour.setText("Hora final:  " + beginHourOfDay + ": " + minute);
                        endHour = beginHourOfDay;
                        endMinutes = minute;
                    }
                }, endHour, endMinutes, false);

                timePickerDialog.show();
            }
        });
//---------------------------------------------------------------------------------------------------------------------//


//-----------------------------------------Choses the start and end day of the travel---------------------------------//

        btnSelectEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseEndDate();
            }

        });

        btnSelectInitDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseInitDate();
            }

        });
        return vista;
    }


    private void chooseInitDate() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                // +1 because january is zero
                final String selectedDate = year + "-" + (month + 1) + "-" + day;
                btnSelectInitDate.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void chooseEndDate() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                final String selectedDate = year + "-" + (month + 1) + "-" + day;
                btnSelectEndDate.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    private boolean validDestiny(){
        String message = destiny_field.getText().toString().trim();
        if(message.isEmpty()){
            destiny_field.setError("Campo usuario está vacío");
            return false;
        }else{
            destiny_field.setError(null);
            return  true;
        }
    }
    private boolean validJustification(){
        String message = justification_field.getText().toString().trim();
        if(message.isEmpty()){
            justification_field.setError("Campo contraseña está vacío");
            return false;
        }else{
            justification_field.setError(null);
            return  true;
        }
    }

//---------------------------------------------------------------------------------------------------------------------//


//-----------------------------------load the vehicles for the list----------------------------------------------------//

    public String getPlate(String string) {
        char[] aux = string.toCharArray();
        String plate = "";
        for (int i = 0; i < aux.length; i++) {
            if (aux[i] == ' ') {
                return plate;
            } else {
                plate += aux[i];
            }
        }
        return plate;
    }

    public void loadVehicles() {

        String url = "http://vtsmsph.com/loadVehicles.php?" + "user=drocampo";
        url.replace(" ", "%20");

        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request = Volley.newRequestQueue(getContext());
        request.add(jsonArrayRequest);
    }

//-------------------------------------------------------------------------------------------------------------------------//

    private void loadWebService() {

        if (!validDestiny() | !validJustification() |
                beginHour == -1 | beginMinutes == -1 | endHour == -1 | endMinutes == -1) {

            Toast.makeText(getContext(), "Por favor ingrese todos los datos", Toast.LENGTH_LONG).show();
        } else {

            String url = "http://vtsmsph.com/solicitarVehiculo.php?user=tony"

                    + "&cedula=" + userNameLogin
                    + "&vehicle=" + getPlate(vehicle_list.getSelectedItem().toString())
                    + "&destino=" + destiny_field.getText().toString()
                    + "&justificacion=" + justification_field.getText().toString()
                    + "&dateBegin=" + btnSelectInitDate.getText().toString()
                    + "&beginHour=" + beginHour + ":" + beginMinutes + ":00"
                    + "&dateEnd=" + btnSelectEndDate.getText().toString()
                    + "&endHour=" + endHour + ":" + endMinutes + ":00";

            url.replace(" ", "%20");
            idrequest = "solicitud";

            jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);

            request = Volley.newRequestQueue(getContext());
            request.add(jsonArrayRequest);
        }


    }


    @Override
    public void onResponse(JSONArray response) {

        try {

            if (idrequest.equals("solicitud")) {
                JSONObject jsonObject;
                jsonObject = response.getJSONObject(0);

                if (jsonObject.getString("Resultado").equals("1")) {
                    Toast.makeText(getContext(), "La solicitud se realizó con éxito!", Toast.LENGTH_LONG).show();

                    userRequest.setText("Usuario: ");
                    vehicleRequest.setText("Vehículo: " + vehicle_list.getSelectedItem().toString());
                    targetRequest.setText("Destino: " + destiny_field.getText().toString());

                    if (beginHour < 12) {
                        dateTimerequest.setText("Fecha: " + "26/01/2019  -  " + btnSelectbeginHour.getText().toString() + " am");
                    } else {
                        dateTimerequest.setText("Fecha: " + "26/01/2019  -  " + btnSelectbeginHour.getText().toString() + " pm");
                    }

                } else {
                    if (jsonObject.getString("Resultado").equals("0")) {


                        OK_message(
                                "Ya existe un prestamo activo del " +
                                        response.getJSONObject(1).getString("fechaInicio")
                                        + " a las " + response.getJSONObject(2).getString("horaInicio")
                                        + " hasta el " + response.getJSONObject(4).getString("fechaFinal")
                                        + " a las " + response.getJSONObject(3).getString("horaFinal")
                        );

                    } else {
                        Toast.makeText(getContext(), "Ha ocurrido un error con la solicitud", Toast.LENGTH_LONG).show();
                    }
                }
                idrequest = "consulta";

            } else {

                JSONObject jsonObject;
                vehicles = new String[response.length()];


                for (int i = 0; i < response.length(); i++) { //we go through the jsonArray to get one by one each jsonObject

                    jsonObject = response.getJSONObject(i);

                    vehicles[i] = jsonObject.getString("PK_License_plate") + " " + jsonObject.getString("Brand") + " " + jsonObject.getString("Model");//we proceed to fill the vector of strings with the name of each vehicle
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, vehicles); //we send the vector with the names of the vehicles, plate and model to the spinner that we have in the frontEnd
                vehicle_list.setAdapter(adapter);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void OK_message(String msg) {
        View v1 = getActivity().getWindow().getDecorView().getRootView();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(v1.getContext());
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "Ocurrio un error al solicitar el vehiculo", Toast.LENGTH_LONG).show();
        //Toast.makeText(getContext(), "Detalle del error:  " + error.toString(), Toast.LENGTH_LONG).show();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
