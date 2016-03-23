package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.adapters.adapter;
import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Local;
import br.unisul.unifind.objetos.Sala;
import br.unisul.unifind.objetos.Servico;
import br.unisul.unifind.viewsDB.DbHelper;

public class ResultadoBusca extends AppCompatActivity {

    private ListView listView;
    private AutoCompleteTextView edBusca;
    DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_busca);

        listView = (ListView) findViewById(R.id.viewResult);



        //obter Bundle:
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String filtro = bundle.getString("filtro");

        //TODO: Elaborar ifs para tipo de resultado
        if(bundle.getBoolean("bloco")){
            List<Bloco> blocos = dbHelper.selectBlocos(filtro, bundle.getInt("campus"));

            listView.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,blocos));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
                {
                    Bloco bloco = (Bloco) listView.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putString("descricaoMapa", bloco.getDescricao());
                    bundle.putDouble("latitudeMapa", bloco.getLatitude());
                    bundle.putDouble("longitudeMapa", bloco.getLongitude());

                    Intent mapa = new Intent(ResultadoBusca.this, Mapa.class);
                    mapa.putExtras(bundle);
                    startActivity(mapa);
                }
            });

        }else if(bundle.getBoolean("sala")){
            List<Sala> salas = dbHelper.selectSalas(bundle.getString("filtro"), bundle.getInt("idBloco"));

            listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1 ,salas));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
                    {
                        Sala sala = (Sala) listView.getItemAtPosition(position);

                        Bundle bundle = new Bundle();
                        bundle.putString("descricaoMapa", sala.getBloco().getDescricao()
                                +", "+sala.getDescricao());
                        bundle.putDouble("latitudeMapa", sala.getBloco().getLatitude());
                        bundle.putDouble("longitudeMapa", sala.getBloco().getLongitude());

                        Intent mapa = new Intent(ResultadoBusca.this, Mapa.class);
                        mapa.putExtras(bundle);
                        startActivity(mapa);
                    }
                });

        }else if(bundle.getBoolean("servico")){
            List<Servico> servicos = dbHelper.selectServicos(filtro, bundle.getInt("idCampus"));

            listView.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,servicos));

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
                {
                    Servico svc = (Servico) listView.getItemAtPosition(position);

                    Bundle bundle = new Bundle();
                    bundle.putString("descricaoMapa", svc.getDescricao());
                    bundle.putDouble("latitudeMapa", svc.getLatitude());
                    bundle.putDouble("longitudeMapa", svc.getLongitude());

                    Intent mapa = new Intent(ResultadoBusca.this, Mapa.class);
                    mapa.putExtras(bundle);
                    startActivity(mapa);
                }
            });

        }


    }
}
