package br.unisul.unifind;

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
import br.unisul.unifind.objetos.Sala;
import br.unisul.unifind.viewsDB.DbHelper;

public class CadastroSala extends AppCompatActivity implements View.OnClickListener {

    private Button btnCadastrar;
    private EditText edSala;
    private Spinner spinnerCampus;
    private Spinner spinnerBlocos;
    final DbHelper dbh = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_sala);

        edSala = (EditText) findViewById(R.id.edCadastroSala);
        btnCadastrar = (Button) findViewById(R.id.btnCadastroSala);
        btnCadastrar.setOnClickListener(this);



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
                spinnerBlocos = (Spinner) findViewById(R.id.spinnerBlocoCadastroSala);

                Campus campus = (Campus) spinnerCampus.getItemAtPosition(position);

                ArrayList<Bloco> blocos = dbh.selectBlocos("", campus.getId());

                ArrayAdapter<Bloco> adp2 = new ArrayAdapter<>(CadastroSala.this,
                        android.R.layout.simple_spinner_item, blocos);

                adp2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerBlocos.setAdapter(adp2);
                //
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        Bloco bloco = (Bloco) spinnerBlocos.getSelectedItem();
        String descricaoSala = edSala.getText().toString();

        Sala salaInsert = new Sala(0, descricaoSala, bloco);

        Toast.makeText(this, descricaoSala+" "+bloco.getId(), Toast.LENGTH_SHORT).show();
        dbh.insertSala(salaInsert);
        finish();

    }
}
