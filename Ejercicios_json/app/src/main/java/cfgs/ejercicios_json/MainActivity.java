package cfgs.ejercicios_json;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn1,btn2,btn3,btn4;
    Button[] botones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button[] botones = {
                btn1 = (Button) findViewById(R.id.ej1),
        btn2 = (Button) findViewById(R.id.ej2),
        btn3 = (Button) findViewById(R.id.ej3),
        btn4 = (Button) findViewById(R.id.ej4)
        };

        for (Button boton:botones) {
            boton.setOnClickListener(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id) {
            case R.id.ej1:
                Intent i = new Intent(this, TiempoCiudades.class);
                this.startActivity(i);
                break;
            case R.id.ej2:
                Intent i2 = new Intent(this, Tiempo7Dias.class);
                this.startActivity(i2);
                break;
            case R.id.ej3:
                Intent i3 = new Intent(this, ConversorXML_JSON.class);
                this.startActivity(i3);
                break;
            case R.id.ej4:
                Intent i4 = new Intent(this, DatosFutbol.class);
                this.startActivity(i4);
                break;
        }
    }
}
