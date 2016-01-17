package cfgs.ejercicios_json;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Analisis {
    static String[] fechas;
    static String ciudadNombre;
    public static StringBuilder sb;

    public static String[] getWeatherStats(JSONObject object) throws JSONException {
        JSONObject item = object.getJSONObject("main");
        String[] datos = new String[3];
        datos[0] = String.valueOf(item.get("temp"));
        datos[1] = String.valueOf(item.get("pressure"));
        datos[2] = String.valueOf(item.get("humidity"));
        return datos;
    }


    public static ArrayList<TiempoDia> get7daysXml(File file, String fileName) throws XmlPullParserException, IOException {
        fechas = new String[7];
        sb = new StringBuilder();
        XmlPullParser xml = Xml.newPullParser();
        xml.setInput(new FileReader(file));
        int event = xml.getEventType();
        String ciu = "noCiudad";
        int indice = 0;
        ArrayList<TiempoDia> dias = new ArrayList<>();
        TiempoDia dia = null;
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    /*if (xml.getName().equalsIgnoreCase("name"))
                        sb.append("<ciudad>" + xml.nextText() + "</ciudad>");*/
                    if (xml.getName().equalsIgnoreCase("name")) {
                        ciudadNombre = xml.nextText();
                        sb.append("<ciudad nombre=" + "\"" + ciudadNombre + "\"" + ">" + "\n");
                    }


                    if (xml.getName().equalsIgnoreCase("time")) {
                        sb.append("\t<fecha dia=" + "\"" + xml.getAttributeValue(0) + "\"" + ">\n");
                        dia = new TiempoDia();
                        dia.setFecha(xml.getAttributeValue(0));
                        fechas[indice++] = xml.getAttributeValue(0);
                    }

                    if (xml.getName().equalsIgnoreCase("temperature")) {
                        sb.append("\t\t<tempMin>" + xml.getAttributeValue(1) + "</tempMin>\n");
                        sb.append("\t\t<tempMax>" + xml.getAttributeValue(2) + "</tempMax>\n");
                        if (dia != null) {
                            dia.getTemperatura().setTempMin(xml.getAttributeValue(1));
                            dia.getTemperatura().setTempMax(xml.getAttributeValue(2));
                        }
                    }
                    if (xml.getName().equalsIgnoreCase("pressure")) {
                        sb.append("\t\t<presion>" + xml.getAttributeValue(1) + " " + xml.getAttributeValue(0) + "</presion>\n");
                        if (dia != null) {
                            dia.setPresion(xml.getAttributeValue(1));
                        }
                    }

                    break;
                case XmlPullParser.END_TAG:
                    if (xml.getName().equalsIgnoreCase("pressure")) {
                        sb.append("\t</fecha>\n\n");
                        if (dia != null)
                            dias.add(dia);
                    }
                    break;
            }
            event = xml.next();
        }
        sb.append("</ciudad>\n\n");

        //Escribimos el fichero
        OutputStreamWriter out;
        File miFichero, tarjeta;
        tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fileName);
        out = new FileWriter(miFichero);
        String res = sb.toString();
        out.write(res); //tabulación de 5 caracteres
        out.flush();
        out.close();
        Log.i("info", res);

        //devolvemos el string
        return dias;
    }


    public static String get7daysJson(ArrayList<TiempoDia> dias, String fichero) throws JSONException, IOException {

        OutputStreamWriter out;
        File miFichero, tarjeta;
        tarjeta = Environment.getExternalStorageDirectory();
        miFichero = new File(tarjeta.getAbsolutePath(), fichero);
        out = new FileWriter(miFichero);

        Ciudad ciudad = new Ciudad();
        ciudad.setCiudad(ciudadNombre);
        ciudad.setDias(dias);


        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setDateFormat("dd-MM-yyyy");
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        //escribimos el fichero
        String res = gson.toJson(ciudad);
        out.write(res); //tabulación de 5 caracteres
        out.flush();
        out.close();
        Log.i("info", res);

        // devolvemos el string
        return res;
    }

    public static Double getJSonConverterRatio(JSONObject objeto) throws JSONException {
        JSONObject rates = objeto.getJSONObject("rates");
        return Double.parseDouble(rates.getString("EUR"));
    }

    public static ArrayList<Equipo> getJSonEquipos(Context context, JSONObject objeto) throws JSONException {
        JSONArray equiposJson = objeto.getJSONArray("teams");//obtenemos el array de equipos.
        Equipo equipo;
        ArrayList<Equipo> listaEquipos = new ArrayList<>();

        //recorremos los equipos y creamos un equipo por cada objeto recorrido
        for (int i = 0; i < equiposJson.length(); i++) {
            equipo = new Equipo();
            equipo.setNombre(equiposJson.getJSONObject(i).getString("name"));
            equipo.setEscudoURL(equiposJson.getJSONObject(i).getString("crestUrl"));
            equipo.setValorMercado(equiposJson.getJSONObject(i).getString("squadMarketValue"));
            equipo.getEnlaces().setJornadas(equiposJson.getJSONObject(i).getJSONObject("_links").getJSONObject("fixtures").getString("href"));
            equipo.getEnlaces().setJugadores(equiposJson.getJSONObject(i).getJSONObject("_links").getJSONObject("players").getString("href"));
            listaEquipos.add(equipo);
        }
        return listaEquipos;
    }

    public static ArrayList<Jornada> getJSonJornadas(JSONObject objeto) throws JSONException {
        JSONArray fixtures = objeto.getJSONArray("fixtures");
        Jornada jornada;
        ArrayList<Jornada> jornadas = new ArrayList<>();

        //recorremos las jornadas y creamos un objeto jornada por cada objeto recorrido
        for (int i = 0; i < fixtures.length(); i++) {
            jornada = new Jornada();

            // sumamos 1 a la hora del partido.
            setFecha(fixtures, jornada, i);

            jornada.setEstado(fixtures.getJSONObject(i).getString("status"));
            jornada.setJornada(fixtures.getJSONObject(i).getString("matchday"));
            jornada.setLocal(fixtures.getJSONObject(i).getString("homeTeamName"));
            jornada.setVisitante(fixtures.getJSONObject(i).getString("awayTeamName"));
            jornada.getResultado().setLocal(fixtures.getJSONObject(i).getJSONObject("result").getString("goalsHomeTeam"));
            jornada.getResultado().setVisitante(fixtures.getJSONObject(i).getJSONObject("result").getString("goalsAwayTeam"));
            jornadas.add(jornada);
        }
        return jornadas;
    }

    private static void setFecha(JSONArray fixtures, Jornada jornada, int i) throws JSONException {
        Calendar calendar = Calendar.getInstance();
        int anio, mes, dia, hora, min;
        try {
            String fecha = fixtures.getJSONObject(i).getString("date").substring(0, 16).replace("T", " ");

            anio = Integer.parseInt(fecha.substring(0, 4));
            mes = Integer.parseInt(fecha.substring(5, 7));
            dia = Integer.parseInt(fecha.substring(8, 10));
            hora = Integer.parseInt(fecha.substring(11, 13));
            min = Integer.parseInt(fecha.substring(14, 16));

            calendar.set(anio, mes, dia, hora, min);
            calendar.add(Calendar.HOUR, 1); // sumamos 1 hora.
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

            jornada.setFecha(format.format(calendar.getTime()));
        }catch (NumberFormatException e) {
            jornada.setFecha("N/D");
            Log.d(MiApp.ERROR, e.getLocalizedMessage());
        }
    }

    public static ArrayList<Jugador> getJSonJugadores(JSONObject objeto) throws JSONException {
        JSONArray players = objeto.getJSONArray("players");
        Jugador jugador;
        ArrayList<Jugador> jugadores = new ArrayList<>();

        //recorremos los jugadores y creamos un obeto jugador por cada objeto recorrido
        for (int i = 0; i < players.length(); i++) {
            jugador = new Jugador();
            jugador.setValorMercado(players.getJSONObject(i).getString("marketValue"));
            jugador.setNombre(players.getJSONObject(i).getString("name"));
            jugador.setContratoHasta(players.getJSONObject(i).getString("contractUntil"));
            try {
                jugador.setDorsal(String.valueOf(players.getJSONObject(i).getInt("jerseyNumber")));  //puede ser null segun el json.
            } catch (JSONException e) {
                jugador.setDorsal("N/D");
            }
            jugador.setFechaNacimiento(players.getJSONObject(i).getString("dateOfBirth"));
            jugador.setPosicion(players.getJSONObject(i).getString("position"));
            jugadores.add(jugador);
        }
        return jugadores;
    }
}
