package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.objetos.Tabela;

public class Admin extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        listView = (ListView) findViewById(R.id.listViewAdmin);

        List<String> options = new ArrayList<String>();
        options.add("Cadastrar Local");
        options.add("Locais Cadastrados");

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, options);

        listView.setAdapter(adp);

        final Intent cadastroLocal = new Intent(this, CadastroLocal.class);
        final Intent locaisCadastrados = new Intent(this, LocaisCadastrados.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
                String selecionado = (String) listView.getItemAtPosition(position).toString();
                if (selecionado=="Cadastrar Local") {
                    startActivity(cadastroLocal);
                }else if (selecionado=="Locais Cadastrados"){
                    startActivity(locaisCadastrados);
                }
            }
        });

    }
}
