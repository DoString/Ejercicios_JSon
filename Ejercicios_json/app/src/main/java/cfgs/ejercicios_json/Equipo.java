package cfgs.ejercicios_json;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Equipo {
    Enlaces enlaces;
    String nombre, valorMercado, escudoURL;
    Drawable escudo;
    ArrayList<Jornada> jornadas;
    ArrayList<Jugador> jugadores;

    public ArrayList<Jornada> getJornadas() {
        return jornadas;
    }

    public void setJornadas(ArrayList<Jornada> jornadas) {
        this.jornadas = jornadas;
    }

    public ArrayList<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public Drawable getEscudo() {
        return escudo;
    }

    public void setEscudo(Drawable escudo) {
        this.escudo = escudo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValorMercado() {
        return valorMercado;
    }

    public void setValorMercado(String valorMercado) {
        this.valorMercado = valorMercado;
    }

    public String getEscudoURL() {
        return escudoURL;
    }

    public void setEscudoURL(String escudoURL) {
        this.escudoURL = escudoURL;
    }

    public Enlaces getEnlaces() {
        return enlaces;
    }

    public Equipo() {
        this.enlaces = new Enlaces();
    }
}
class Enlaces {
    String jornadas, jugadores;

    public String getJornadas() {
        return jornadas;
    }

    public void setJornadas(String jornadas) {
        this.jornadas = jornadas;
    }

    public String getJugadores() {
        return jugadores;
    }

    public void setJugadores(String jugadores) {
        this.jugadores = jugadores;
    }
}
