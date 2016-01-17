package cfgs.ejercicios_json;

//Lista de TODOS:
// TODO fix: hora de los partidos, hay que sumarle 1 a la hora.
// TODO fix: imagenes no renderizadas correctamente (Real madird, Sevilla, Eibar, Depor, Celta)
// TODO add: crear una actividad inicial que muestre si hay partidos hoy y mañana.
// TODO add: añadir icono de partido de liga o partido de champions en encuentros
// TODO fix: traducir las demarcaciones de los jugadores.
// TODO fix: si jornada = -1 cambiar mensaje a previa de champions en lista de encuentros


import android.app.Application;

import java.util.ArrayList;



// Aplicacion global del proyecto
// Utilizamos esta clase para salvar
// el estado de la app
public class MiApp extends Application {
    private ArrayList<Equipo> equipos;
    private Equipo equipoSeleccionado;
    private Jugador jugadorSelecionado;
    // datos para utilizar la clave API
    // estos datos se deben introducir
    // en el header del request en el cliente http
    // cliente.addHeader(TOKEN,API_KEY)
    public static String API_KEY = "3921229f383a4b71a8cb7b85a5c13e10";
    public static String TOKEN = "X-Auth-Token";

    // Token para depurar
    public static String ERROR = "FUT_ERR";

    public ArrayList<String> getNombreEquipos() {
        if (equipos == null)
            return null;

        ArrayList<String> nombres = new ArrayList<>();

        for (Equipo equipo : equipos) {
            nombres.add(equipo.getNombre());
        }
        return nombres;
    }

    public Jugador getJugadorSelecionado() {
        return jugadorSelecionado;
    }

    public void setJugadorSelecionado(Jugador jugadorSelecionado) {
        this.jugadorSelecionado = jugadorSelecionado;
    }

    public Equipo getEquipoSeleccionado() {
        return equipoSeleccionado;
    }

    public void setEquipoSeleccionado(Equipo equipoSeleccionado) {
        this.equipoSeleccionado = equipoSeleccionado;
    }

    public ArrayList<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(ArrayList<Equipo> equipos) {
        this.equipos = equipos;
    }
}
