package cfgs.ejercicios_json;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class Tiempo7Dias extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Spinner lista;
    TextView result, resultJson;
    Button ficheros;
    AsyncHttpClient cliente;
    static final String FICHERO_XML = "OpenWeather7Dias.xml";
    static final String FICHERO_JSON = "OpenWeather7Dias.json";
    ArrayList<TiempoDia> dias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo7_dias);

        lista = (Spinner) findViewById(R.id.ciudades7);
        result = (TextView) findViewById(R.id.result);
        resultJson = (TextView) findViewById(R.id.resultJson);
        ficheros = (Button) findViewById(R.id.ficheros);
        ficheros.setOnClickListener(this);

        cliente = new AsyncHttpClient();

        loadSpinnerProvincias();
    }

    @Override
    public void onClick(View v) {
        if (v == ficheros) {
            if (index < 0){
                Toast.makeText(Tiempo7Dias.this, "Debes seleccionar una provicia", Toast.LENGTH_SHORT).show();
                return;
            }

            String ciudad = String.valueOf(lista.getItemAtPosition(index)).toLowerCase(Locale.ENGLISH);
            String urlXML = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + ciudad + ",es"+
                    "&mode=xml&units=metric&cnt=7&appid=2de143494c0b295cca9337e1e96b00e0";
            String urlJSon = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + ciudad + ",es"+
                    "&mode=json&units=metric&cnt=7&appid=2de143494c0b295cca9337e1e96b00e0";
            descargarFicheroXml(urlXML);
            //descargarFicheroJSon(urlJSon);
        }
    }

    private void loadSpinnerProvincias() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.provincias, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        lista.setAdapter(adapter);

        // This activity implements the AdapterView.OnItemSelectedListener
        lista.setOnItemSelectedListener(this);

    }

    private void descargarFicheroXml(String url){
       cliente.get(url, new FileAsyncHttpResponseHandler(this) {
           @Override
           public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
               Toast.makeText(Tiempo7Dias.this, "Error al descargar el fichero xml", Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onSuccess(int statusCode, Header[] headers, File file) {
               try {
                   result.setText("");
                   dias = Analisis.get7daysXml(file, FICHERO_XML);
                   result.setText(Analisis.sb.toString());
                   Toast.makeText(Tiempo7Dias.this, "Fichero XML creado correctamente", Toast.LENGTH_SHORT).show();
                   resultJson.setText("");
                   resultJson.setText(Analisis.get7daysJson(dias, FICHERO_JSON));
                   Toast.makeText(Tiempo7Dias.this, "Fichero JSON creado correctamente", Toast.LENGTH_SHORT).show();
               } catch (XmlPullParserException e) {
                   Toast.makeText(Tiempo7Dias.this, "Error al examinar el fichero xml", Toast.LENGTH_SHORT).show();
                   result.setText(e.getLocalizedMessage());
               } catch (IOException e) {
                   Toast.makeText(Tiempo7Dias.this, "Error al abrir el fichero xml", Toast.LENGTH_SHORT).show();
                   result.setText(e.getLocalizedMessage());
               } catch (JSONException e) {
                   Toast.makeText(Tiempo7Dias.this, "Error al procesar el contenido json", Toast.LENGTH_SHORT).show();
                   result.setText(e.getLocalizedMessage());
               }
           }
       });
    }

    int index = -1;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        index = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        index = -1;
    }
}
