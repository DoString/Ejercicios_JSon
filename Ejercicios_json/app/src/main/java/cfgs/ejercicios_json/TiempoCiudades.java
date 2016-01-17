package cfgs.ejercicios_json;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class TiempoCiudades extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner _spProvincias;
    TextView temp,pres,hum;
    AsyncHttpClient _cliente;
    String[] _datos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo_ciudades);

        _spProvincias = (Spinner) findViewById(R.id.ciudades);
        temp = (TextView) findViewById(R.id.temp);
        pres = (TextView) findViewById(R.id.pres);
        hum = (TextView) findViewById(R.id.hum);
        _cliente = new AsyncHttpClient();

        loadSpinnerProvincias();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tiempo_ciudades, menu);
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

    private void loadSpinnerProvincias() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.provincias, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        _spProvincias.setAdapter(adapter);

        // This activity implements the AdapterView.OnItemSelectedListener
        _spProvincias.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String ciudad = String.valueOf(parent.getItemAtPosition(position));
        getJson(ciudad.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //nada
    }

    private void getJson(String ciudad) {
        String request = "http://api.openweathermap.org/data/2.5/weather?q=" + ciudad + "&appid=2de143494c0b295cca9337e1e96b00e0";
        _cliente.get(request, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    _datos = Analisis.getWeatherStats(response);
                    String tempData = _datos[0];
                    double ntemp = Double.parseDouble(tempData);
                    double celsius = -273.15;
                    temp.setText(String.format("%.1f", (ntemp + celsius)) + " Cº");
                    pres.setText(_datos[1] + " Pa");
                    hum.setText(_datos[2] + " %");
                    _datos = null;
                } catch (JSONException e) {
                    Toast.makeText(TiempoCiudades.this, "Error al leer el fichero json", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(TiempoCiudades.this, "Los datos recibidos no tienen el formato correcto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(TiempoCiudades.this, "Error al descargar el fichero json", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
