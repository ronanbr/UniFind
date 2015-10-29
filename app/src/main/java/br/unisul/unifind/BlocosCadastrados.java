package br.unisul.unifind;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.viewsDB.DbHelper;

public class BlocosCadastrados extends AppCompatActivity{

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocos_cadastrados);

        listView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DbHelper dbHelper = new DbHelper(this);
        List<Bloco> blocos = dbHelper.selectTodosBlocos();
        ArrayAdapter<Bloco> adp = new ArrayAdapter<Bloco>(this, android.R.layout.simple_list_item_1, blocos);
        listView.setAdapter(adp);
    }

}