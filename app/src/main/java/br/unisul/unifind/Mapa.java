package br.unisul.unifind;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.viewsDB.DbHelper;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    DbHelper dbHelper = new DbHelper(this);
    private GoogleMap mMap;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //daqui pra baixo
        Intent intent = getIntent();
        bundle = intent.getExtras();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);


        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            setUpMap();
        }
    }


    //Método que faz a leitura de fato dos valores recebidos do GPS
    public void startGPS(){
        LocationManager lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        LocationListener lListener = new LocationListener(){
            public void onLocationChanged(Location locat) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
        lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lListener);
    }


    private void setUpMap() {
        //caso tiver informação de local no bundle, mostrar esse local no mapa;
        if(this.bundle.containsKey("descricaoMapa")){
            //Define o perímetro do campus
            LatLng infEsq = new LatLng(-28.482895, -49.019497);
            LatLng infDir = new LatLng(-28.482565, -49.017737);
            LatLng supDir = new LatLng(-28.474452, -49.025879);
            LatLng supEsq = new LatLng(-28.475237, -49.027053);

            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .add(infEsq, infDir, supDir, supEsq));

            //define um marker com o local
            String descricao = bundle.getString("descricaoMapa");
            LatLng local = new LatLng(bundle.getDouble("latitudeMapa"), bundle.getDouble("longitudeMapa"));

            mMap.addMarker(new MarkerOptions().position(local).title(descricao));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 17));

            //retira os extras do bundle
            bundle.remove("localMapa");
            bundle.remove("latitudeMapa");
            bundle.remove("longitudeMapa");

        }else{
            //Define o perímetro do campus
            LatLng infEsq = new LatLng(-28.482895, -49.019497);
            LatLng infDir = new LatLng(-28.482565, -49.017737);
            LatLng supDir = new LatLng(-28.474452, -49.025879);
            LatLng supEsq = new LatLng(-28.475237, -49.027053);

            Polygon polygon = mMap.addPolygon(new PolygonOptions()
                    .add(infEsq, infDir, supDir, supEsq));

            //adiciona um marker para cada bloco do banco de dados;
            List<Bloco> blocos = dbHelper.selectTodosBlocos();
            for (Bloco bloco : blocos) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(bloco.getLatitude(), bloco.getLongitude())).title(bloco.getDescricao()));
            }
            //Define o centro do mapa na LatLong do campus de TB e define o zoom;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-28.479075, -49.022547), 16));
        }

    }

    @Override
    public void onBackPressed() { finish(); }

}
