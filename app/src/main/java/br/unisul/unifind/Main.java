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

public class Main extends AppCompatActivity {

    DbHelper dbHelper = new DbHelper(this);
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //iniica o mapa se não houver nenhum iniciado
        //e começa a leitura dos dados do GPS
        setUpMapIfNeeded();
        startGPS();

        //Para acompanhamento de dados:
        //Imprime no log os dados da tabela Blocos
        List<Bloco> blocos = dbHelper.selectTodosBlocos();
        for (Bloco bloco : blocos) {
            Log.i("teste", "Local no BD (Bloco): "+bloco.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Método que define qual ação será executada quando selecionada uma opção do menu;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.option1:
                Intent buscaBloco = new Intent(this, BuscaBloco.class);
                startActivity(buscaBloco);
                return true;

            case R.id.option2:
                Intent buscaServico = new Intent(this, BuscaServico.class);
                startActivity(buscaServico);
                return true;

            case R.id.option3:
                Intent buscaSala = new Intent(this, BuscaSala.class);
                startActivity(buscaSala);
                return true;

            case R.id.option4:
                Intent admin = new Intent(this, Admin.class);
                startActivity(admin);
                return true;

            case R.id.exit:
                finalizar();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

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
        List<Bloco> blocos = dbHelper.selectTodosBlocos();
        for (Bloco bloco : blocos) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(bloco.getLatitude(), bloco.getLongitude())).title(bloco.getDescricao()));
        }
        //Define o centro do mapa na LatLong do campus de TB e define o zoom;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-28.479075, -49.022547), 16));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
    }

    @Override
    public void onBackPressed() { finalizar(); }

    private void finalizar(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
