package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.objetos.Tabela;

public class CadastroLocal extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_local);

        listView = (ListView) findViewById(R.id.listViewCadastro);


        List<Tabela> tables = new ArrayList<Tabela>();

        tables.add(new Tabela("Bloco", "blocos"));
        tables.add(new Tabela("Sala", "salas"));
        tables.add(new Tabela("Serviço", "servicos"));
        tables.add(new Tabela("Campus", "campi"));


        ArrayAdapter<Tabela> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tables);

        listView.setAdapter(adp);

        final Intent cadastro = new Intent(this, Cadastro.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
                Tabela selecionada = (Tabela) listView.getItemAtPosition(position);

                //código para obter o bundle da activity anterior
                Bundle bundle = new Bundle();
                //adiciona alguma informação no bundle
                bundle.putString("tabela", selecionada.getNomeNoBD());

                cadastro.putExtras(bundle);

                startActivity(cadastro);
            }
        });
    }
}
