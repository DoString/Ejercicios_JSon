package cfgs.ejercicios_json;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

public class JugadorInfo extends AppCompatActivity {

    MiApp app;
    Jugador jugador;
    TextView nombre, dorsal,valor,fecha,posicion,contrato;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugador_info);

        // recuperamos el jugador seleccionado
        app = (MiApp) getApplicationContext();
        jugador = app.getJugadorSelecionado();

        nombre = (TextView) findViewById(R.id.jugadorNombre);
        dorsal = (TextView) findViewById(R.id.dor);
        valor = (TextView) findViewById(R.id.val);
        fecha = (TextView) findViewById(R.id.fnac);
        posicion = (TextView) findViewById(R.id.pos);
        contrato = (TextView) findViewById(R.id.con);

        // asignamos los valores
        nombre.setText(jugador.getNombre());
        dorsal.setText(jugador.getDorsal());
        valor.setText(jugador.getValorMercado());
        fecha.setText(jugador.getFechaNacimiento());
        posicion.setText(jugador.getPosicion()); // TODO traducir
        contrato.setText(jugador.getContratoHasta());
    }
}
