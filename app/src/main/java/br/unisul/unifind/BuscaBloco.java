package br.unisul.unifind;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class BuscaBloco extends AppCompatActivity implements View.OnClickListener {

    private Button btBuscar;
    private AutoCompleteTextView edBusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_bloco);

        edBusca = (AutoCompleteTextView) findViewById(R.id.edBlocoBusca);
        btBuscar = (Button) findViewById(R.id.btBuscaBloco);
        btBuscar.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //código para obter o bundle da activity anterior
        Bundle bundle = new Bundle();

        //adiciona alguma informação no bundle
        bundle.putString("filtro", edBusca.getText().toString());

        Intent resultBusca = new Intent(this, ResultadoBusca.class);
        resultBusca.putExtras(bundle);
        startActivity(resultBusca);
    }
}
