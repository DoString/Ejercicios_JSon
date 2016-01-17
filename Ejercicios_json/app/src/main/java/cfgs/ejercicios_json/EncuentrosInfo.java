package cfgs.ejercicios_json;

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParseException;
import com.larvalabs.svgandroid.SVGParser;

import java.io.IOException;
import java.util.ArrayList;

public class EncuentrosInfo extends ListActivity {
    Equipo equipo;
    public static MiApp app;
    TextView equipoNombre;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuentros_info);

        equipoNombre = (TextView) findViewById(R.id.team);
        imageView = (ImageView) findViewById(R.id.encuentrosEquipo);
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // obtenemos el equipo seleccionado
        // de la aplicacion global
        app = (MiApp) getApplicationContext();
        equipo = app.getEquipoSeleccionado();
        equipoNombre.setText(equipo.getNombre());

        if (equipo.getEscudo() != null) {
            imageView.setImageDrawable(equipo.getEscudo());
        }

        // asignamos el adaptador a la actividad (lista)
        getListView().setAdapter(new EncuentrosAdapter(this, R.layout.lista_encuentros, equipo.getJornadas(), getLayoutInflater(), equipo));
    }
}

class EncuentrosAdapter extends ArrayAdapter<Jornada> {
    LayoutInflater layoutInflater;
    ViewHolder holder;
    Equipo equipo;
    Context context;

    public EncuentrosAdapter(Context context, int resource, ArrayList<Jornada> objects, LayoutInflater inflater, Equipo equipo) {
        super(context, resource, objects);
        this.layoutInflater = inflater;
        this.equipo = equipo;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.lista_encuentros, null);
            holder = new ViewHolder();
            holder.padre = (LinearLayout) convertView.findViewById(R.id.padre);
            holder.local = (TextView) convertView.findViewById(R.id.local);
            holder.visitante = (TextView) convertView.findViewById(R.id.visi);
            holder.fecha = (TextView) convertView.findViewById(R.id.fecha);
            holder.estado = (TextView) convertView.findViewById(R.id.estado);
            holder.jornada = (TextView) convertView.findViewById(R.id.jornada);
            holder.resLocal = (TextView) convertView.findViewById(R.id.resLocal);
            holder.resVisi = (TextView) convertView.findViewById(R.id.resVisi);
            holder.logo = (ImageView) convertView.findViewById(R.id.logo);
            holder.logo.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            holder.stJornada = (TextView) convertView.findViewById(R.id.staticJornada);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Jornada jornada = getItem(position);
        holder.local.setText(jornada.getLocal());
        holder.visitante.setText(jornada.getVisitante());
        boolean local;

        // resaltamos nuestro equipo en mayusculas
        local = resaltarEquipo(jornada);

        // comprobamos si es partido de liga
        // o de champions
        setLogoJornada(jornada, local);

        holder.fecha.setText(jornada.getFecha());

        String estado;
        if (jornada.getEstado().equalsIgnoreCase("finished"))
            estado = "terminado";
        else
            estado = "Sin comenzar";

        holder.estado.setText(estado);
        if (jornada.getJornada().equalsIgnoreCase("-1")) {
            holder.jornada.setText("");
            holder.stJornada.setText("PREVIA CHAMPIONS");
        }else {
            holder.jornada.setText(jornada.getJornada());
            holder.stJornada.setText("JORNADA");
        }


        // Comprobamos si hubo partido,
        // en caso de que haya resultado
        // se cambia el color de la fila
        // a verde si el marcado es favorable a nuestro equipo,
        // rojo sino y normal en caso de empate
        int resl = 0, resv = 0;
        boolean jugado = false;
        try {
            resl = Integer.parseInt(jornada.getResultado().getLocal());
            resv = Integer.parseInt(jornada.getResultado().getVisitante());
            jugado = true;
        } catch (NumberFormatException e) {
            jugado = false;
        }
        if (jugado) {
            holder.resLocal.setText(jornada.getResultado().getLocal());
            holder.resVisi.setText(jornada.getResultado().getVisitante());
            if (local) {
                if (resl > resv)
                    holder.padre.setBackgroundResource(R.color.victoria);
                else if (resl < resv)
                    holder.padre.setBackgroundResource(R.color.derrota);
                else
                    holder.padre.setBackgroundResource(R.color.empate);
            } else {
                if (resl > resv)
                    holder.padre.setBackgroundResource(R.color.derrota);
                else if (resl < resv)
                    holder.padre.setBackgroundResource(R.color.victoria);
                else
                    holder.padre.setBackgroundResource(R.color.empate);
            }
        } else {
            holder.resLocal.setText("");
            holder.resVisi.setText("");
            holder.padre.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    private boolean resaltarEquipo(Jornada jornada) {
        boolean local = false;
        if (jornada.getLocal().equalsIgnoreCase(equipo.getNombre())) {
            holder.local.setAllCaps(true);
            holder.visitante.setAllCaps(false);
            local =  true;
        } else {
            holder.local.setAllCaps(false);
            holder.visitante.setAllCaps(true);
        }
        return local;
    }

    private void setLogoJornada(Jornada jornada, boolean local) {
        SVG svg;
        if (jornada.getLogo() == null) {
            try {

                if (isPartidoLiga(jornada, local)) {
                    //svg = SVGParser.getSVGFromAsset(context.getAssets(), "lfp_logo.png");
                    //jornada.setLogo(svg.createPictureDrawable());
                    holder.logo.setImageResource(R.drawable.lfp_logo);
                    jornada.setLogo(holder.logo.getDrawable());
                } else {
                    holder.logo.setImageResource(R.drawable.champions_logo);
                    jornada.setLogo(holder.logo.getDrawable());
                }

            } catch (Exception e) {
                Log.d(MiApp.ERROR, e.toString());
                Log.d(MiApp.ERROR, e.getLocalizedMessage());
                holder.logo.setImageDrawable(null);
            }
        } else {
            holder.logo.setImageDrawable(jornada.getLogo());
        }
    }

    private boolean isPartidoLiga(Jornada jornada, boolean local) {
        for (String nombre : EncuentrosInfo.app.getNombreEquipos()) {
            if (nombre.equalsIgnoreCase(local ? jornada.getVisitante() : jornada.getLocal()))
                return true;
        }
        return false;
    }

    class ViewHolder {
        TextView local, visitante, fecha, estado, jornada, resLocal, resVisi, stJornada;
        LinearLayout padre;
        ImageView logo;
    }
}
