package cfgs.ejercicios_json;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Ciudad {
    private String ciudad;
    @SerializedName ("lista")
    private ArrayList<TiempoDia> dias;

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public ArrayList<TiempoDia> getDias() {
        return dias;
    }

    public void setDias(ArrayList<TiempoDia> dias) {
        this.dias = dias;
    }
}
