package br.unisul.unifind;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.objetos.Servico;
import br.unisul.unifind.viewsDB.DbHelper;

public class Cadastro extends AppCompatActivity implements View.OnClickListener {

    private EditText edLatitude;
    private EditText edLongitude;
    private EditText edDescricao;
    private Button btSalvar;
    private Spinner spinnerCampus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final DbHelper dbh = new DbHelper(this);
        spinnerCampus = (Spinner) findViewById(R.id.spinnerCadastro);

        setContentView(R.layout.activity_cadastro);
        setupElements();
        startGPS();

        //spinner Campus
        ArrayList<Campus> campi = dbh.selectTodosCampi();

        ArrayAdapter<Campus> adp = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, campi);

        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCampus.setAdapter(adp);

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    // Método usado para importar os elementos da classe R (layout por id)
    public void setupElements() {
        edLatitude = (EditText) findViewById(R.id.edLatitude);
        edLongitude = (EditText) findViewById(R.id.edLongitude);
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
        edLatitude.setText(latitude.toString());
        edLongitude.setText(longitude.toString());
    }

    @Override
    public void onClick(View v) {
        if((edLatitude.getText().toString().length()>3) && (edLongitude.getText().toString().length()>3)){
            if(edDescricao.getText().toString().length()>3){

                //Recupera dados do Bundle
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                String tabela = bundle.getString("tabela");

                Campus campus = (Campus) spinnerCampus.getSelectedItem();

                if (tabela=="blocos"){
                    Bloco bloco = new Bloco();
                    bloco.setId(0);
                    bloco.setDescricao(edDescricao.getText().toString());
                    bloco.setLatitude(Double.parseDouble(edLatitude.getText().toString()));
                    bloco.setLongitude(Double.parseDouble(edLongitude.getText().toString()));
                    bloco.setCampus(campus);
                    DbHelper dbh = new DbHelper(this);
                    dbh.insertBloco(bloco);
                    finish();

                }else if (tabela=="servicos"){

                    Servico servico = new Servico();
                    servico.setId(0);
                    servico.setDescricao(edDescricao.getText().toString());
                    servico.setLatitude(Double.parseDouble(edLatitude.getText().toString()));
                    servico.setLongitude(Double.parseDouble(edLongitude.getText().toString()));
                    servico.setCampus(campus);
                    DbHelper dbh = new DbHelper(this);
                    dbh.insertServico(servico);
                    finish();

                }
            }else{
                Toast.makeText(Cadastro.this, "Descrição inválida!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(Cadastro.this, "Sem localização definida!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() { finish(); }
}