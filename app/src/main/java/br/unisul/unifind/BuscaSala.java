package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.viewsDB.DbHelper;



public class BuscaSala extends AppCompatActivity implements View.OnClickListener {

    private Button btBuscar;
    private EditText edBusca;
    private Spinner spinnerCampus;
    private Spinner spinnerBlocos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_sala);
        final DbHelper dbh = new DbHelper(this);

        edBusca = (EditText) findViewById(R.id.edBuscaSala);
        btBuscar = (Button) findViewById(R.id.btBuscaSala);
        btBuscar.setOnClickListener(this);

        //spinner Campus
        spinnerCampus = (Spinner) findViewById(R.id.spinnerCampusBuscaSala);

        ArrayList<Campus> campi = dbh.selectTodosCampi();

        ArrayAdapter<Campus> adp = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, campi);

        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCampus.setAdapter(adp);

        spinnerCampus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //spinner Blocos
                spinnerBlocos = (Spinner) findViewById(R.id.spinnerBlocoBuscaSala);

                Campus campus = (Campus) spinnerCampus.getItemAtPosition(position);

                ArrayList<Bloco> blocos = dbh.selectBlocos("", campus.getId());

                ArrayAdapter<Bloco> adp2 = new ArrayAdapter<>(BuscaSala.this,
                        android.R.layout.simple_spinner_item, blocos);

                adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerBlocos.setAdapter(adp2);
                //
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //



    }

    @Override
    public void onClick(View v) {

        Bloco bloco = (Bloco) spinnerBlocos.getSelectedItem();

        //código para obter o bundle da activity anterior
        Bundle bundle = new Bundle();

        //adiciona alguma informação no bundle
        bundle.putBoolean("sala", true);
        bundle.putString("filtro", edBusca.getText().toString());
        bundle.putInt("idBloco", bloco.getId());

        Intent resultBusca = new Intent(this, ResultadoBusca.class);
        resultBusca.putExtras(bundle);
        startActivity(resultBusca);
    }
}
