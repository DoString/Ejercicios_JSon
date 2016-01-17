package cfgs.ejercicios_json;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListaJugadores extends ListActivity implements AdapterView.OnItemClickListener {

    MiApp app;
    Equipo equipo;
    JugadoresAdapter jugadoresAdapter;
    ImageView imageView;
    TextView equipoNombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_jugadores);

        imageView = (ImageView) findViewById(R.id.fondo);
        equipoNombre = (TextView) findViewById(R.id.jequipoNombre);

        // recuperamos el equipo seleccionado
        app = (MiApp) getApplicationContext();
        equipo = app.getEquipoSeleccionado();

        equipoNombre.setText(equipo.getNombre());

        // ponemos el escudo como fondo
        // aprovechando que es una imagen vectorial
        if (equipo.getEscudo() != null) {
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            equipo.getEscudo().setAlpha(20);
            imageView.setImageDrawable(equipo.getEscudo());
        }

        // asignamos el adaptador a la lista
        jugadoresAdapter = new JugadoresAdapter(this, R.layout.lista_jugadores, equipo.getJugadores(), getLayoutInflater());
        getListView().setAdapter(jugadoresAdapter);

        // asignamos el listener para los clicks
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        app.setJugadorSelecionado((Jugador) parent.getItemAtPosition(position));
        Intent intent = new Intent(ListaJugadores.this, JugadorInfo.class);
        ListaJugadores.this.startActivity(intent);
    }
}

class JugadoresAdapter extends ArrayAdapter<Jugador>{
    LayoutInflater inflater;
    ViewHolder holder;

    public JugadoresAdapter(Context context, int resource, ArrayList<Jugador> objects, LayoutInflater inf) {
        super(context, resource, objects);
        inflater = inf;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lista_jugadores, null);
            holder = new ViewHolder();
            holder.dorsal = (TextView) convertView.findViewById(R.id.dorsal);
            holder.nombre = (TextView) convertView.findViewById(R.id.playername);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Jugador jugador = getItem(position);
        holder.dorsal.setText(jugador.getDorsal());
        holder.nombre.setText(jugador.getNombre());
        return convertView;
    }

    class ViewHolder {
        TextView dorsal,nombre;
    }
}
