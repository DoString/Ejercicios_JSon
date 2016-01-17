package cfgs.ejercicios_json;

/**
 * Created by juan on 13/01/2016.
 */
public class TiempoDia {
    String presion, fecha;
    Temperatura temperatura;

    public TiempoDia(){
        temperatura = new Temperatura();
    }

    public Temperatura getTemperatura() {
        return temperatura;
    }


    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPresion() {
        return presion;
    }

    public void setPresion(String presion) {
        this.presion = presion;
    }
}
