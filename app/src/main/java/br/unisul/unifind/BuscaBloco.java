package br.unisul.unifind;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.viewsDB.DbHelper;

public class BuscaBloco extends AppCompatActivity implements View.OnClickListener {

    private Button btBuscar;
    private AutoCompleteTextView edBusca;
    private Spinner spinnerCampus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_bloco);

        DbHelper dbh = new DbHelper(this);

        edBusca = (AutoCompleteTextView) findViewById(R.id.edBlocoBusca);
        btBuscar = (Button) findViewById(R.id.btBuscaBloco);
        btBuscar.setOnClickListener(this);

        //spinner
        spinnerCampus = (Spinner) findViewById(R.id.spinnerCampiBloco);

        ArrayList<Campus> campi = dbh.selectTodosCampi();

        ArrayAdapter<Campus> adp = new ArrayAdapter<Campus>(this,
                android.R.layout.simple_spinner_item, campi);

        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCampus.setAdapter(adp);
        //

    }


    @Override
    public void onClick(View v) {

        Campus campus = (Campus) spinnerCampus.getSelectedItem();
        //código para obter o bundle da activity anterior
        Bundle bundle = new Bundle();

        //adiciona alguma informação no bundle
        bundle.putBoolean("bloco", true);
        bundle.putString("filtro", edBusca.getText().toString());
        bundle.putInt("campus", campus.getId());

        Intent resultBusca = new Intent(this, ResultadoBusca.class);
        resultBusca.putExtras(bundle);
        startActivity(resultBusca);
    }
}
