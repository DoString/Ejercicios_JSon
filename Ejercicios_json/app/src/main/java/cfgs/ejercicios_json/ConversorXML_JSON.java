package cfgs.ejercicios_json;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class ConversorXML_JSON extends AppCompatActivity implements View.OnClickListener{

    EditText euros, dolares;
    RadioButton eurDolar, dolarEur;
    Button convertir;
    Conversor conversor;
    AsyncHttpClient cliente;
    static final String URL = "http://api.fixer.io/latest?base=USD";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversor_xml__json);
        inicializar();
    }

    public void inicializar() {
        euros = (EditText) findViewById(R.id.editText1);
        dolares = (EditText) findViewById(R.id.editText2);
        eurDolar = (RadioButton) findViewById(R.id.radio0);
        eurDolar.setChecked(true);
        dolarEur = (RadioButton) findViewById(R.id.radio1);
        convertir = (Button) findViewById(R.id.fconvertir);
        convertir.setOnClickListener(this);
        cliente = new AsyncHttpClient();
    }

    @Override
    public void onClick(View v) {
        if (v == convertir) {
            convertir.setEnabled(false);
            descargarCambio(URL);

        }
    }

    private void descargarCambio(String url) {
        cliente.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    Toast.makeText(ConversorXML_JSON.this, "Error al descargar los datos Json, se utilizará el cambio por defecto", Toast.LENGTH_SHORT).show();
                    calcCambio(false);
                } catch (Exception e) {
                    Toast.makeText(ConversorXML_JSON.this, "Los datos tienen un formato incorrecto", Toast.LENGTH_SHORT).show();
                }
                convertir.setEnabled(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    double cambio = Analisis.getJSonConverterRatio(response);
                    calcCambio(true, cambio);
                } catch (JSONException e) {
                    Toast.makeText(ConversorXML_JSON.this, "No existen datos para EUR", Toast.LENGTH_SHORT).show();
                }
                convertir.setEnabled(true);
            }
        });
    }

    private void calcCambio (boolean jsonSuccess, double... cambio) {
        if (jsonSuccess)
            conversor = new Conversor(cambio[0]);
        else
            conversor = new Conversor();

        if (eurDolar.isChecked()) {
            if (!euros.getText().toString().isEmpty())
                dolares.setText(conversor.convertirADolares(euros.getText().toString().replace(",", ".")));
            else
                Toast.makeText(ConversorXML_JSON.this, "Debes introducir un valor de euros", Toast.LENGTH_SHORT).show();
        }

        if (dolarEur.isChecked()) {
            if (!dolares.getText().toString().isEmpty())
                euros.setText(conversor.convertirAEuros(dolares.getText().toString().replace(",", ".")));
            else
                Toast.makeText(ConversorXML_JSON.this, "Debes introducir un valor de dólares", Toast.LENGTH_SHORT).show();
        }
    }
}
