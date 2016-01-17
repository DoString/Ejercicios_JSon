package cfgs.ejercicios_json;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParseException;
import com.larvalabs.svgandroid.SVGParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

public class EquipoInfo extends AppCompatActivity implements View.OnClickListener {

    TextView nombre, valor;
    Button encuentros, jugadores;
    AsyncHttpClient client;
    MiApp app;
    Equipo equipo;
    ImageView escudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipo_info);

        escudo = (ImageView) findViewById(R.id.escudo);
        // activamos la renderizacion de software
        // en la vista de imagenes
        escudo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        encuentros = (Button) findViewById(R.id.encuentros);
        encuentros.setOnClickListener(this);
        jugadores = (Button) findViewById(R.id.jugadores);
        jugadores.setOnClickListener(this);
        nombre = (TextView) findViewById(R.id.nombre);
        valor = (TextView) findViewById(R.id.valor);
        client = new AsyncHttpClient();

        app = (MiApp) getApplicationContext();

        // datos para usar el api
        client.addHeader(MiApp.TOKEN, MiApp.API_KEY);

        // obtenemos el equipo guardado
        equipo = app.getEquipoSeleccionado();

        // recuperamos la imagen del escudo
        // previamente guardada.
        if (equipo.getEscudo() != null) {
            escudo.setImageDrawable(equipo.getEscudo());
        } else {
            // descargamos el escudo del equipo
            // en segundo plano.
            // El enlace en json aparece como una imagen svg
            // Android no soporta el uso de imagenes vectoriales
            // pero usamos la libreria SVG para poder renderizar
            // las imagenes, no obstante algunas de ellas se renderizan mal...
            descargarEscudo(equipo);
        }


        // asignamos los valores nombre y valor
        nombre.setText(equipo.getNombre());
        valor.setText(equipo.getValorMercado());
    }

    private void descargarEscudo(final Equipo equipo) {

        client.get(equipo.getEscudoURL(), new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                //Toast.makeText(EquipoInfo.this, throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                try {
                    SVG svg = SVGParser.getSVGFromInputStream(new FileInputStream(file));
                    Drawable drawable = svg.createPictureDrawable();
                    escudo.setImageDrawable(drawable);
                    equipo.setEscudo(drawable);
                } catch (FileNotFoundException e) {
                    Toast.makeText(EquipoInfo.this, "La imagen no existe", Toast.LENGTH_SHORT).show();
                } catch (SVGParseException e) {
                    Toast.makeText(EquipoInfo.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                } 
            }
        });
    }

    private void descargarEncuentros(String url) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(EquipoInfo.this, "No se han podido obtener los datos del equipo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    equipo.setJornadas(Analisis.getJSonJornadas(response));
                    verEncuentros();
                } catch (JSONException e) {
                    Toast.makeText(EquipoInfo.this, "No se han podido obtener los datos del equipo", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void descargarJugadores(String url) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(EquipoInfo.this, "No se han podido obtener los datos de jugadores", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    equipo.setJugadores(Analisis.getJSonJugadores(response));
                    verJugadores();
                } catch (JSONException e) {
                    Toast.makeText(EquipoInfo.this, "No se han podido obtener los datos de jugadores", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == encuentros) {
            // descargamos los encuentros
            // para el equipo seleccionado
            // si la descarga tiene exito llevara
            // a la actividad con la informacion
            if (equipo.getJornadas() == null)
                descargarEncuentros(equipo.getEnlaces().getJornadas());
            else {
                verEncuentros();
            }
        } else if (v == jugadores) {
            // descargamos los jugadores
            // para el equipo seleccionado
            // si la descarga tiene exito llevara
            // a la actividad con la lista de jugadores
            if (equipo.getJugadores() == null)
                descargarJugadores(equipo.getEnlaces().getJugadores());
            else {
                verJugadores();
            }
        }
    }

    private void verJugadores() {
        Intent i = new Intent(EquipoInfo.this, ListaJugadores.class);
        EquipoInfo.this.startActivity(i);
    }

    private void verEncuentros() {
        Intent i = new Intent(EquipoInfo.this, EncuentrosInfo.class);
        EquipoInfo.this.startActivity(i);
    }
}
