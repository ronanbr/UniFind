package br.unisul.unifind;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.viewsDB.DbHelper;

public class InfoGps extends FragmentActivity implements View.OnClickListener {

//    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //variáveis que usaremos durante o processo
    private EditText edLatitude;
    private EditText edLongitude;
    private EditText edAltitude;
    private EditText edVelocidade;
    private EditText edDescricao;
    private Button btSalvar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_gps);
        setupElements();

        startGPS();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // Método usado para importar os elementos da classe R

    public void setupElements() {
        edLatitude = (EditText) findViewById(R.id.edLatitude);
        edLongitude = (EditText) findViewById(R.id.edLongitude);
        edAltitude = (EditText) findViewById(R.id.edAltitude);
        edVelocidade = (EditText) findViewById(R.id.edVelocidade);
        edDescricao = (EditText) findViewById(R.id.txtDescricao);
        btSalvar = (Button) findViewById(R.id.addBloco);
        btSalvar.setOnClickListener(this);


    }

    //Método que faz a leitura de fato dos valores recebidos do GPS
    public void startGPS(){
        LocationManager lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                updateView(locat);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
    }

    //  Método que faz a atualização da tela para o usuário.
    public void updateView(Location locat){
        Double latitude = locat.getLatitude();
        Double longitude = locat.getLongitude();
        Double velocidade = (double) locat.getSpeed();
        Double altitude = locat.getAltitude();


        edLatitude.setText(latitude.toString());
        edLongitude.setText(longitude.toString());
        edVelocidade.setText(velocidade.toString());
        edAltitude.setText(altitude.toString());
    }

    @Override
    public void onClick(View v) {
        if((edLatitude.getText().toString().length()>3) && (edDescricao.getText().toString().length()>3) && (edLongitude.getText().toString().length()>3)){
        Bloco bloco = new Bloco();
        bloco.setId(0);
        bloco.setDescricao(edDescricao.getText().toString());
        bloco.setLatitude(Double.parseDouble(edLatitude.getText().toString()));
        bloco.setLongitude(Double.parseDouble(edLongitude.getText().toString()));
        DbHelper dbh = new DbHelper(this);

            dbh.insertBloco(bloco);
            edDescricao.setText("");
            finish();
        }

    }
}