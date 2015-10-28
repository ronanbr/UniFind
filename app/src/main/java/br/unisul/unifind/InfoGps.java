package br.unisul.unifind;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class InfoGps extends FragmentActivity {

//    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //variáveis que usaremos durante o processo
    private EditText edLatitude;
    private EditText edLongitude;
    private EditText edAltitude;
    private EditText edVelocidade;
    private Button btLocalizar;



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
}