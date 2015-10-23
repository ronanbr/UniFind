package br.unisul.unifind;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        startGPS();
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
            case R.id.action_map_unifind:
                startMapsActivity();
                return true;

            case R.id.option3:
                startBuscaActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void startMapsActivity() {

        Intent mapsActivity = new Intent(this, MapsActivity.class);
        startActivity(mapsActivity);
    }

    public void startBuscaActivity() {

        Intent buscaSala = new Intent(this, BuscaSala.class);
        startActivity(buscaSala);
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
        LatLng location = new LatLng(-28.479075, -49.022547);

        mMap.addMarker(new MarkerOptions().position(new LatLng(-28.475490, -49.026258)).title("Cettal"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-28.480209, -49.021578)).title("Saúde"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-28.480684, -49.021079)).title("Shopping Unisul"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-28.480798, -49.020101)).title("Ginásio"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-28.482543, -49.019273)).title("Bloco Sede"));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

    }

}
