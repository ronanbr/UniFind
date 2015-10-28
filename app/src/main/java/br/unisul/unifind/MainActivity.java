package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.viewsDB.DbHelper;

public class MainActivity extends AppCompatActivity {

    DbHelper dbHelper = new DbHelper(this);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //adicionarBlocosNoBD();

        setUpMapIfNeeded();
        startGPS();

        List<Bloco> blocos = dbHelper.selectBlocos();
        for (Bloco bloco : blocos) {
            Log.i("LocaisNoBd", bloco.toString());
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

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.option4:
                startInfoGpsctivity();
                return true;

            case R.id.option1:
                startBuscaBlocoActivity();
                return true;

            case R.id.option2:
                startBuscaServicoActivity();
                return true;

            case R.id.option3:
                startBuscaSalaActivity();
                return true;

            case R.id.option5:
                Intent blocosBD = new Intent(this, BlocosCadastrados.class);
                startActivity(blocosBD);
                return true;

            case R.id.exit:
                finalizar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void startInfoGpsctivity() {

        Intent infoGpsActivity = new Intent(this, InfoGps.class);
        startActivity(infoGpsActivity);
    }

    public void startBuscaSalaActivity() {

        Intent buscaSala = new Intent(this, BuscaSala.class);
        startActivity(buscaSala);
    }


    public void startBuscaBlocoActivity() {

        Intent buscaBloco = new Intent(this, BuscaBloco.class);
        startActivity(buscaBloco);
    }

    public void startBuscaServicoActivity() {

        Intent buscaServico = new Intent(this, BuscaServico.class);
        startActivity(buscaServico);
    }


    //Método que faz a leitura de fato dos valores recebidos do GPS
    public void startGPS(){
        LocationManager lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        LocationListener lListener = new LocationListener() {
            public void onLocationChanged(Location locat) {
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locat.getLatitude(), locat.getLongitude()), 16));

            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
    }


    //maps

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            mMap.setMyLocationEnabled(true);
            mMap.setIndoorEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        LatLng campusTB = new LatLng(-28.479075, -49.022547);

        List<Bloco> blocos = dbHelper.selectBlocos();
        for (Bloco bloco : blocos) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(bloco.getLatitude(), bloco.getLongitude())).title(bloco.getDescricao()));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campusTB, 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

    }

    private void adicionarBlocosNoBD() {
        dbHelper.insertBloco(new Bloco(0, "Laboratorios de Informatica", -28.475490, -49.026258));
        dbHelper.insertBloco(new Bloco(0, "Saúde", -28.480209, -49.021578));
        dbHelper.insertBloco(new Bloco(0, "Shopping Unisul", -28.480684, -49.021079));
        dbHelper.insertBloco(new Bloco(0, "Ginásio", -28.480798, -49.020101));
        dbHelper.insertBloco(new Bloco(0, "Bloco Sede", -28.482543, -49.019273));
    }

    private void finalizar(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
