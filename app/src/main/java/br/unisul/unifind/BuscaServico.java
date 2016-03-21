package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.viewsDB.DbHelper;

public class BuscaServico extends AppCompatActivity implements View.OnClickListener {

    private Button btBuscar;
    private EditText edBusca;
    private Spinner spinnerCampus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_servico);

        spinnerCampus = (Spinner) findViewById(R.id.spinnerCampusServico);
        edBusca = (EditText) findViewById(R.id.edServico);
        btBuscar = (Button) findViewById(R.id.btnBuscaServico);
        final DbHelper dbh = new DbHelper(this);


        btBuscar.setOnClickListener(this);
        //spinner Campus

        ArrayList<Campus> campi = dbh.selectTodosCampi();

        ArrayAdapter<Campus> adp = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, campi);

        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCampus.setAdapter(adp);


    }

    @Override
    public void onClick(View v) {
        Campus campus = (Campus) spinnerCampus.getSelectedItem();

        //código para obter o bundle da activity anterior
        Bundle bundle = new Bundle();

        //adiciona alguma informação no bundle
        bundle.putBoolean("servico", true);
        bundle.putString("filtro", edBusca.getText().toString());
        bundle.putInt("idCampus", campus.getId());

        Intent resultBusca = new Intent(this, ResultadoBusca.class);
        resultBusca.putExtras(bundle);
        startActivity(resultBusca);
    }
}
