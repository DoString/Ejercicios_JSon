package cfgs.ejercicios_json;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DatosFutbol extends ListActivity implements AdapterView.OnItemClickListener {
    TextView equipo;
    ImageView foto;
    static final String URL = "http://www.football-data.org/v1/soccerseasons/399/teams"; //equipos de primera division española en formato json
    ArrayList<Equipo> equipos;
    EquiposAdapter adapter;
    public static AsyncHttpClient client;
    MiApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_futbol);

        client = new AsyncHttpClient();
        // agregamos el header que contiene nuestra API KEY

        // asignamos el listener para
        // el evento itemClick de la lista
        this.getListView().setOnItemClickListener(this);

        // nuestros datos estan guardados en la aplicacion global
        // no hacemos una nueva instancia sino que recuperamos
        // mediante casting la que ya existe
        app = (MiApp) getApplicationContext();

        // establecemos el header en el cliente
        // con el token y la clave Api
        client.addHeader(MiApp.TOKEN, MiApp.API_KEY);

        // aqui recuperamos el arraylist de equipos
        // que ha sido previavente salvado
        if (app.getEquipos() != null) {
            this.getListView().setAdapter(new EquiposAdapter(this, R.layout.lista_equipos, app.getEquipos(), getLayoutInflater()));
        } else {
            descargarEquipos(URL);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        app.setEquipoSeleccionado((Equipo)parent.getItemAtPosition(position));
        Intent i = new Intent(this, EquipoInfo.class);
        this.startActivity(i);
    }

    private void descargarEquipos(String url) {
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(DatosFutbol.this, "Error al descargar los datos JSon", Toast.LENGTH_SHORT).show();
                Toast.makeText(DatosFutbol.this, errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    equipos = Analisis.getJSonEquipos(DatosFutbol.this, response);
                    // Asignamos el arraylist al estado de nuestra app
                    // para poder recuperarlo despues
                    app.setEquipos(DatosFutbol.this.equipos);
                    DatosFutbol.this.getListView().setAdapter(new EquiposAdapter(DatosFutbol.this, R.layout.lista_equipos, equipos, getLayoutInflater()));
                } catch (JSONException e) {
                    Toast.makeText(DatosFutbol.this, "Error al examinar los datos JSon", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

class EquiposAdapter extends ArrayAdapter<Equipo> {
    LayoutInflater layoutInflater;
    ViewHolder holder;

    public EquiposAdapter(Context context, int resource, ArrayList<Equipo> objects, LayoutInflater infllater) {
        super(context, resource, objects);
        this.layoutInflater = infllater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.lista_equipos, null);
            holder = new ViewHolder();
            holder.equipo = (TextView) convertView.findViewById(R.id.equipo);
            holder.foto = (ImageView) convertView.findViewById(R.id.foto);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Equipo equipo = getItem(position);
        holder.equipo.setText(equipo.getNombre());
        /*if (holder.foto.getDrawable() == null){
            if (equipo.getEscudo() != null)
                holder.foto.setImageDrawable(equipo.getEscudo());
            else
                setEscudoEquipo(equipo);
        }*/
        return convertView;
    }

    private void setEscudoEquipo(final Equipo equipo) {

        DatosFutbol.client.get(equipo.getEscudoURL(), new FileAsyncHttpResponseHandler(getContext()) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                Toast.makeText(getContext(), throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                try {
                    SVG svg = SVGParser.getSVGFromInputStream(new FileInputStream(file));
                    Picture picture = svg.getPicture();
                    Drawable drawable = svg.createPictureDrawable();
                    //drawable.setBounds(new Rect(5,5,5,5));
                    equipo.setEscudo(drawable);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getContext(), "La imagen no existe", Toast.LENGTH_SHORT).show();
                } catch (SVGParseException e) {
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class ViewHolder {
        ImageView foto;
        TextView equipo;
    }
}

