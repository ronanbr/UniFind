package br.unisul.unifind.viewsDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.objetos.Sala;
import br.unisul.unifind.objetos.Servico;

/**
 * Created by Ronan Cardoso on 28/10/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String NOME_BASE = "UniFindDataBase";
    private static final int VERSAO_BASE = 3;

    String sqlCreateTabelaCampi = "CREATE TABLE campi("
            +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"descricao TEXT "
            +")";

    String sqlCreateTabelaVersao = "CREATE TABLE tb_versao("
            +"versao INTEGER "
            +")";

    String sqlCreateTabelaBlocos = "CREATE TABLE blocos("
            +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"descricao TEXT,"
            +"latitude DOUBLE,"
            +"longitude DOUBLE, "
            +"id_campus INTEGER NOT NULL, "
            +"FOREIGN KEY(id_campus) REFERENCES campi(id)"
            +")";

    String sqlCreateTabelaSalas = "CREATE TABLE salas("
            +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"descricao TEXT, "
            +"id_bloco INTEGER NOT NULL, "
            +"FOREIGN KEY(id_bloco) REFERENCES blocos(id)"
            +")";

    String sqlCreateTabelaServicos = "CREATE TABLE servicos("
            +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
            +"descricao TEXT, "
            +"latitude TEXT, "
            +"longitude TEXT, "
            +"id_campus INTEGER NOT NULL, "
            +"FOREIGN KEY(id_campus) REFERENCES campi(id)"
            +")";

    public DbHelper(Context context) {
        super(context, NOME_BASE, null, VERSAO_BASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreateTabelaCampi);
        db.execSQL(sqlCreateTabelaBlocos);
        db.execSQL(sqlCreateTabelaSalas);
        db.execSQL(sqlCreateTabelaServicos);
        db.execSQL(sqlCreateTabelaVersao);
        insertVersao(db);

        populaBase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //drop tables
        db.execSQL("DROP TABLE IF EXISTS campi");
        db.execSQL("DROP TABLE IF EXISTS blocos");
        db.execSQL("DROP TABLE IF EXISTS salas");
        db.execSQL("DROP TABLE IF EXISTS servicos");
        db.execSQL("DROP TABLE IF EXISTS tb_versao");
        //cria novamente
        onCreate(db);


    }

    public void atualizar(int versao){
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS campi");
        db.execSQL("DROP TABLE IF EXISTS blocos");
        db.execSQL("DROP TABLE IF EXISTS salas");
        db.execSQL("DROP TABLE IF EXISTS servicos");
        db.execSQL("DROP TABLE IF EXISTS tb_versao");

        db.execSQL(sqlCreateTabelaCampi);
        db.execSQL(sqlCreateTabelaBlocos);
        db.execSQL(sqlCreateTabelaSalas);
        db.execSQL(sqlCreateTabelaServicos);
        db.execSQL(sqlCreateTabelaVersao);

        insertVersao(db);

        this.updateVersao(db, versao);

    }

    public void insertVersao(SQLiteDatabase db){
        ContentValues cv = new ContentValues();

        cv.put("versao", 1);

        db.insert("tb_versao", null, cv);
    }

    public void insertCampus(Campus campus){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("descricao", campus.getDescricao());

        db.insert("campi", null, cv);

        db.close();
    }

    public void insertBloco(Bloco bloco){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("descricao", bloco.getDescricao());
        cv.put("latitude", bloco.getLatitude());
        cv.put("longitude", bloco.getLongitude());
        cv.put("id_campus", bloco.getCampus().getId());

        db.insert("blocos", null, cv);

        db.close();
    }

    public void insertSala(Sala sala){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("descricao", sala.getDescricao());
        cv.put("id_bloco", sala.getBloco().getId());

        db.insert("salas", null, cv);

        db.close();
    }

    public void insertServico(Servico servico){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("descricao", servico.getDescricao());
        cv.put("latitude", servico.getLatitude());
        cv.put("longitude", servico.getLongitude());
        cv.put("id_campus", servico.getCampus().getId());

        db.insert("servicos", null, cv);

        db.close();
    }

    public ArrayList selectTodosCampi(){
        ArrayList<Campus> campi = new ArrayList<Campus>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosBlocos =
                "SELECT * FROM campi "
                        +"ORDER BY descricao";

        Cursor c = db.rawQuery(sqlSelectTodosBlocos, null);
        if (c.moveToFirst()){
            do{
                Campus campus = new Campus();
                campus.setId(c.getInt(0));
                campus.setDescricao(c.getString(1));

                campi.add(campus);
            }while(c.moveToNext());
        }

        return campi;
    }

    public ArrayList selectTodosBlocos(){
        ArrayList<Bloco> blocos = new ArrayList<Bloco>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosBlocos =
                "SELECT blo.id AS idBloco, blo.descricao as bloDesc, blo.latitude AS latBlo, blo.longitude AS lonBlo, " +
                        "cam.id AS idCam, cam.descricao AS descCampus " +
                        "FROM blocos blo " +
                        "INNER JOIN campi cam ON (blo.id_campus = cam.id) "
                        +"ORDER BY bloDesc";

        Cursor c = db.rawQuery(sqlSelectTodosBlocos, null);
        if (c.moveToFirst()){
            do{
                Bloco bloco = new Bloco();
                bloco.setId(c.getInt(c.getColumnIndex("idBloco")));
                bloco.setDescricao(c.getString(c.getColumnIndex("bloDesc")));
                bloco.setLatitude(c.getDouble(c.getColumnIndex("latBlo")));
                bloco.setLongitude(c.getDouble(c.getColumnIndex("lonBlo")));

                Campus campus = new Campus();
                campus.setDescricao(c.getString(c.getColumnIndex("descCampus")));
                campus.setId(c.getInt(c.getColumnIndex("idCam")));
                bloco.setCampus(campus);

                blocos.add(bloco);
            }while(c.moveToNext());
        }

        return blocos;
    }

    public ArrayList selectTodasSalas(){
        ArrayList<Sala> salas = new ArrayList<Sala>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosBlocos =
                "SELECT sal.id AS idSala, sal.descricao AS descSala, " +
                        "blo.id AS idBloco, blo.descricao AS descBloco, " +
                        "blo.latitude AS latBloco, blo.longitude AS lonBloco, " +
                        "cam.id AS idCampus, cam.descricao AS descCampus " +
                        "FROM salas sal " +
                        "INNER JOIN blocos blo ON (blo.id = sal.id_bloco) " +
                        "INNER JOIN campi cam ON (blo.id_campus = cam.id) " +
                        "ORDER BY descSala";

        Cursor c = db.rawQuery(sqlSelectTodosBlocos, null);
        if (c.moveToFirst()){
            do{
                Bloco bloco = new Bloco();
                bloco.setId(c.getInt(c.getColumnIndex("idBloco")));
                bloco.setDescricao(c.getString(c.getColumnIndex("descBloco")));
                bloco.setLatitude(c.getDouble(c.getColumnIndex("latBloco")));
                bloco.setLongitude(c.getDouble(c.getColumnIndex("lonBloco")));

                Campus campus = new Campus();
                campus.setDescricao(c.getString(c.getColumnIndex("descCampus")));
                campus.setId(c.getInt(c.getColumnIndex("idCampus")));
                bloco.setCampus(campus);

                Sala sala = new Sala();
                sala.setId(c.getInt(c.getColumnIndex("idSala")));
                sala.setDescricao(c.getString(c.getColumnIndex("descSala")));
                sala.setBloco(bloco);

                salas.add(sala);
            }while(c.moveToNext());
        }

        return salas;
    }

    public ArrayList selectTodosServicos(){
        ArrayList<Servico> servicos = new ArrayList<Servico>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosServicos =
                "SELECT ser.id AS idSvc, ser.descricao AS descSvc, ser.latitude AS latSvc, ser.longitude AS lonSvc, " +
                        "cam.descricao AS descCampus, cam.id AS idCampus " +
                        "FROM servicos ser " +
                        "INNER JOIN campi cam ON (ser.id_campus = cam.id) "+
                        "ORDER BY descSvc";

        Cursor c = db.rawQuery(sqlSelectTodosServicos, null);
        if (c.moveToFirst()){
            do{

                Campus campus = new Campus();
                campus.setDescricao(c.getString(c.getColumnIndex("descCampus")));
                campus.setId(c.getInt(c.getColumnIndex("idCampus")));

                Servico servico = new Servico();
                servico.setId(c.getInt(c.getColumnIndex("idSvc")));
                servico.setDescricao(c.getString(c.getColumnIndex("descSvc")));
                servico.setLatitude(c.getDouble(c.getColumnIndex("latSvc")));
                servico.setLongitude(c.getDouble(c.getColumnIndex("lonSvc")));
                servico.setCampus(campus);

                servicos.add(servico);
            }while(c.moveToNext());
        }

        return servicos;
    }

    public ArrayList selectBlocos(String filtro, int idCampus){
        ArrayList<Bloco> blocos = new ArrayList<Bloco>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect =
                "SELECT blo.id AS idBloco,  blo.descricao AS descBloco, blo.latitude AS latBloco, " +
                        "blo.longitude AS lonBloco, " +
                        "cam.id AS idCampus, cam.descricao AS descCampus " +
                        "FROM blocos blo " +
                        "INNER JOIN campi cam ON (blo.id_campus = cam.id) "+
                        "WHERE blo.descricao LIKE '%"+filtro+"%' " +
                        "AND cam.id = "+idCampus+" "+
                        "ORDER BY descBloco";

        Cursor c = db.rawQuery(sqlSelect, null);
        if (c.moveToFirst()){
            do {
                Bloco bloco = new Bloco();
                bloco.setId(c.getInt(c.getColumnIndex("idBloco")));
                bloco.setDescricao(c.getString(c.getColumnIndex("descBloco")));
                bloco.setLatitude(c.getDouble(c.getColumnIndex("latBloco")));
                bloco.setLongitude(c.getDouble(c.getColumnIndex("lonBloco")));

                Campus campus = new Campus();
                campus.setDescricao(c.getString(c.getColumnIndex("descCampus")));
                campus.setId(c.getInt(c.getColumnIndex("idCampus")));
                bloco.setCampus(campus);

                blocos.add(bloco);
            }while(c.moveToNext());
        }

        return blocos;
    }

    public ArrayList selectSalas(String filtro, int idBloco){
        ArrayList<Sala> salas = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect =
                "SELECT sal.id AS idSala, sal.descricao AS descSala, sal.id_bloco AS idBlocoSala, " +
                        "blo.id AS idBloco, blo.descricao AS descBloco, blo.latitude AS latBloco, " +
                        "blo.longitude AS lonBloco, blo.id_campus, " +
                        "cam.id AS idCampus, cam.descricao AS descCampus " +
                        "FROM salas sal " +
                        "INNER JOIN blocos blo ON (blo.id = sal.id_bloco) " +
                        "INNER JOIN campi cam ON (blo.id_campus = cam.id) "+
                        "WHERE descSala LIKE '%"+filtro+"%' " +
                        "AND idBlocoSala = "+idBloco+" "+
                        "ORDER BY descSala";

        Cursor c = db.rawQuery(sqlSelect, null);
        if (c.moveToFirst()){
            do{
                Bloco bloco = new Bloco();
                bloco.setId(c.getInt(c.getColumnIndex("idBloco")));
                bloco.setDescricao(c.getString(c.getColumnIndex("descBloco")));
                bloco.setLatitude(c.getDouble(c.getColumnIndex("latBloco")));
                bloco.setLongitude(c.getDouble(c.getColumnIndex("lonBloco")));

                Campus campus = new Campus();
                campus.setDescricao(c.getString(c.getColumnIndex("descCampus")));
                campus.setId(c.getInt(c.getColumnIndex("idCampus")));
                bloco.setCampus(campus);

                Sala sala = new Sala();
                sala.setId(c.getInt(c.getColumnIndex("idSala")));
                sala.setDescricao(c.getString(c.getColumnIndex("descSala")));
                sala.setBloco(bloco);

                salas.add(sala);
            }while(c.moveToNext());
        }

        return salas;
    }

    public ArrayList selectServicos(String filtro, int idCampus){
        ArrayList<Servico> servicos = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosServicos =
                "SELECT ser.id AS idSvc, ser.descricao AS descSvc, ser.latitude AS latSvc, ser.longitude AS lonSvc, " +
                        "cam.descricao AS descCampus, cam.id AS idCampus " +
                        "FROM servicos ser " +
                        "INNER JOIN campi cam ON (ser.id_campus = cam.id) "+
                        "WHERE ser.descricao LIKE '%" + filtro + "%' " +
                        "AND cam.id = "+idCampus+" "+
                        "ORDER BY descSvc";

        Cursor c = db.rawQuery(sqlSelectTodosServicos, null);
        if (c.moveToFirst()){
            do{
                Campus campus = new Campus();
                campus.setDescricao(c.getString(c.getColumnIndex("descCampus")));
                campus.setId(c.getInt(c.getColumnIndex("idCampus")));

                Servico servico = new Servico();
                servico.setId(c.getInt(c.getColumnIndex("idSvc")));
                servico.setDescricao(c.getString(c.getColumnIndex("descSvc")));
                servico.setLatitude(c.getDouble(c.getColumnIndex("latSvc")));
                servico.setLongitude(c.getDouble(c.getColumnIndex("lonSvc")));
                servico.setCampus(campus);

                servicos.add(servico);
            }while(c.moveToNext());
        }

        return servicos;
    }

    public void updateVersao(SQLiteDatabase db, int versao){
        String sqlUpdateVersao = "UPDATE tb_versao SET versao = "+versao;
        db.execSQL(sqlUpdateVersao);
    }


    public Integer selectVersao(){
        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "SELECT versao FROM tb_versao";

        Cursor c = db.rawQuery(sqlSelect, null);

        c.moveToFirst();
        int result = c.getInt(0);

        Log.d("VERSAO BD",""+result);
        return result;
    }


    public void populaBase(SQLiteDatabase db){
        ContentValues cv = new ContentValues();
        ArrayList<ContentValues> dados = new ArrayList<>();

        //Campus
        cv = new ContentValues(); cv.put("descricao", "Tubarão"); dados.add(cv); //1
        //cv = new ContentValues(); cv.put("descricao", "Pedra Branca"); dados.add(cv); //2

        for(ContentValues contv : dados){
            db.insert("campi", null, contv);
        }
        dados.clear();


        //Serviços
        cv = new ContentValues(); cv.put("descricao", "GTI");  cv.put("latitude", -28.483492);  cv.put("longitude", -49.013177);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Recursos Humanos");  cv.put("latitude", -28.483501);  cv.put("longitude", -49.013336);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Centro de Convivência (CC)");  cv.put("latitude", -28.480716);  cv.put("longitude", -49.021318);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "SAIAC (CC)");  cv.put("latitude", -28.480716);  cv.put("longitude", -49.021130);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Ginásio");  cv.put("latitude", -28.480781);  cv.put("longitude", -49.020236);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Biblioteca");  cv.put("latitude", -28.481257);  cv.put("longitude", -49.019748);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Unisul Virtual");  cv.put("latitude", -28.481384);  cv.put("longitude", -49.019169);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Parque Tecnológico");  cv.put("latitude", -28.480849);  cv.put("longitude", -49.021453);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "DCE");  cv.put("latitude", -28.479884);  cv.put("longitude", -49.021971);  cv.put("id_campus", 1); dados.add(cv);

        for(ContentValues contv : dados){
            db.insert("servicos", null, contv);
        }
        dados.clear();


        //Blocos do Campus Tubarão
        cv = new ContentValues(); cv.put("descricao", "BLOCO A - Sede");  cv.put("latitude", -28.482450);  cv.put("longitude", -49.019388);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO B - Dehon");  cv.put("latitude", -28.482021);  cv.put("longitude", -49.020016);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO C - Saude");  cv.put("latitude", -28.480118);  cv.put("longitude", -49.021700);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO D - Pedagógico");  cv.put("latitude", -28.479698);  cv.put("longitude", -49.021282);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO E");  cv.put("latitude", -28.477983);  cv.put("longitude", -49.022915);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO F - Centec");  cv.put("latitude", -28.476301);  cv.put("longitude", -49.025338);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO G - Cettal");  cv.put("latitude", -28.475761);  cv.put("longitude", -49.026212); cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO H - Núcleo de Informatica");  cv.put("latitude", -28.475249);  cv.put("longitude", -49.025571);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Hospital Veterinário");  cv.put("latitude", -28.478138);  cv.put("longitude", -49.022747);  cv.put("id_campus", 1); dados.add(cv);

//        cv = new ContentValues(); cv.put("descricao", "Bloco de A.R. de Judô");  cv.put("latitude", 	-28.48079800);  cv.put("longitude", -49.02010100);  cv.put("id_campus", 1); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Unidade Hospitalar de Ensino");  cv.put("latitude", -28.48079800);  cv.put("longitude", -49.02010100);  cv.put("id_campus", 1); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Prédio da Fundação");  cv.put("latitude", -28.483659);  cv.put("longitude", -49.013435);  cv.put("id_campus", 1); dados.add(cv);

        for(ContentValues contv : dados){
            db.insert("blocos", null, contv);
        }
        dados.clear();


        //Salas do Campus Tubarão
        cv = new ContentValues(); cv.put("descricao", "Sala de Projeção I (TBPRJC1A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 204 (TB204A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 205 (TB205A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 206 (TB206A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 207 (TB207A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 208 (TB208A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 209 (TB209A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 105 (TB105A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 106 (TB106A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 107 (TB107A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 108 (TB108A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 109 (TB109A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 110 (TB110A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 111 (TB111A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 113 (TB113A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 101 (TB101A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 (TB102A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 103 (TB103A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 104 (TB104A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 116 (TB116A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 119 (TB119A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 120 (TB120A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 121 (TB121A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 210 (TB210A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 211 (TB211A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 214 (TB214A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Moda - Ateliê (ATELIE)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 02  (TB02A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 03  (TB03A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 22  (TB22A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 219 (TB219A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 01  (TB01A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 07  (TB07A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 08  (TB08A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 09  (TB09A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 11  (TB11A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 16  (TB16A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 17  (TB17A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 19  (TB19A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 21  (TB21A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 23  (TB23A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 13  (TB13A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 10  (TB10A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 18  (TB18A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 20  (TB20A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 217 (TB217A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 218 (TB218A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 222 (TB222A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório Contábil (LABCONTABI)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 223 (TB223A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 220 (TB220A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 221 (TB221A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 224 (TB224A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 04  (TB04A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 05  (TB05A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica I (TBLBINF1A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica II (TBLBINF2A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica POS (TBLBINFPOS)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica IV (TBLINF4A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat.Desenho 1 (TBDESENHOA)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Salão Nobre (TBSLNOBA)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 126 (TB126B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 127 (TB127B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 128 (TB128B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 129 (TB129B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 130 (TB130B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 131 (TB131B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 132 (TB132B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 133 (TB133B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 134 (TB134B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 135 (TB135B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 136 (TB136B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 137 (TB137B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 138 (TB138B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 139 (TB139B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 140 (TB140B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 141 (TB141B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 108 (TB108B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 109 (TB109B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 110 (TB110B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 111 (TB111B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 112 (TB112B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 113 (TB113B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 114 (TB114B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 115 (TB115B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 116 (TB116B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 117 (TB117B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 119 (TB119B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 120 (TB120B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 122 (TB122B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 123 (TB123B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 124 (TB124B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 125 (TB125B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 (TB102B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 103 (TB103B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 121 (TB121B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala I Infantil (TBIFANT1B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala II Infantil (TBIFANT2B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala III Infantil (TBIFANT3B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Maternal (TBMTERNB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Leitura (TBLTURAB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática (TBLBINFB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Ciências (TBLBCIÒB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Banda Marcial (TBBDMARCB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab de Enfermagem (TBLABENFC)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Auditório 201C (TB201C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala de Ginástica (GINASTICA)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 004 - Policlínica (TB004C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 001 - Policlínica (TB001C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 002 - Policlínica (TB002C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 003 - Policlínica (TB003C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Clínica Escola - Policlínica (CLIN. ESC.)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Eletroestética (TBLABESTET)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Cosmetologia e Estética (TBLABIP)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "TCC Curso de Odontologia (TCC ODONTO)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Ginástica (SALA GINAS)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Quimica Farm. (TB24C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório de Fisioterapia (TB01C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Clínica Odontológica II (TBC008)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Clínica Odontológica III (TBC009)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Interpretação Radiológica (TB13C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microspia B (TB14C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório (TB09C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório de Comestologia (LABTCO)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Pedagogia (TBL10C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Pneumocardiologia (TBL12C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Neurologia (TBL11C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "LABTCO2 (LABTCO2)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab.Fisio (LAB.FISIO)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Radio.Imagem (RADIO.IMAG)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Neurologia (LAB.ANEX)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Bioqu¡mica (TB25C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Zoologia (TBLB03C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microscopia A (TB16C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microbiologia (TB17C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microbiologia (MICRO B)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Parasitologia (TB19C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Auditório 211D (TB211D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Auditório 311D (TB311D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 201 (TB201D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 202 (TB202D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 203 (TB203D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 204 (TB204D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 205 (TB205D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 206 (TB206D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 207 (TB207D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 208 (TB208D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 209 (TB209D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 210 (TB210D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 213 (TB213D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 214 (TB214D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 215 (TB215D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 216 (TB216D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 105 (TB105D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 106 (TB106D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 107 (TB107D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 113 (TB113D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 114 (TB114D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 115 (TB115D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 116 (TB116D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 117 (TB117D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 118 (TB118D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 119 (TB119D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 120 (TB120D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 121 (TB121D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 122 (TB122D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 123 (TB123D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 124 (TB124D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 (TB102D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 103 (TB103D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 104 (TB104D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 217 (TB217D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 218 (TB218D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 220 (TB220D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 221 (TB221D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 222 (TB222D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 223 (TB223D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 224 (TB224D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 301 (TB301D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 302 (TB302D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 303 (TB303D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 304 (TB304D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 305 (TB305D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 306 (TB306D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 307 (TB307D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 308 (TB308D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 309 (TB309D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 310 (TB310D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 313 (TB313D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 314 (TB314D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 315 (TB315D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 316 (TB316D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 317 (TB317D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 318 (TB318D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 319 (TB319D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 320 (TB320D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 321 (TB321D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 322 (TB322D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 323 (TB323D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 324 (TB324D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 101 (TB101D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 108 (TB108D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 4 (TBLBINF4D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 3 (TBLBINF3D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 101 (TB101E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 104 (TB104E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Anatomico (TB1E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Anatomico (TB2E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Enfermagem (TB3E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Tec. Cirurgicas (TB102E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Tec. Cirurgicas (TB103E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 117 (TB117F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 08A (TB08AF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 09A (TB09AF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 07A (TB07AF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Atelie de Arquitetura (TBATEARQF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Bromatologia (TB1F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inorga. e Analítica (TB21F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inorgânica B (TB22F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Alimentos (TB26F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Cromatologia e Infrav. (TB28F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de abs. At. e Espec. (TB29F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Engenharia Quimica (TB2F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Quimica Organica (TB5F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Redes e Software (TB7F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Física (TB8F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Herb rio (TBLABHERF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Conforto T‚rmico (LBCONFTTB)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Informática (LBINFTB_)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Computação (TB6F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Computação (TBLBCOMPF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB15F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB17F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB32F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB33F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Físic./Quím. A (TB23F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Físci./Quím. B (TB24F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Jornalismo (TB13F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microscopia (TB20F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Solos (TB16F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Maquetaria (TB12F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 201 (TB201G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 202 (TB202G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 203 (TB203G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 204 (TB204G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 205 (TB205G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 206 (TB206G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 207 (TB207G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 208 (TB208G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 209 (TB209G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 210 (TB210G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 211 (TB211G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 212 (TB212G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 213 (TB213G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 214 (TB214G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 215 (TB215G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 105 (TB105G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 106 (TB106G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 107 (TB107G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 108 (TB108G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 109 (TB109G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 110A (TB110AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 110 (TB110G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 111 (TB111G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 112 (TB112G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 113 (TB113G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 114 (TB114G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 115 (TB115G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 116 (TB116G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 101 (TB101G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102A (TB102AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 (TB102G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 103 (TB103G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 104 (TB104G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 216 (TB216G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala L09 (TB9G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 217 (TB217G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 210A (TB210AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 202A (TB202AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 211A (TB211AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 206A (TB206AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório de Projetos (TBLABPROJ)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório de Jornalismo 01 (TBLABJNL01)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório de Jornalismo 02 (TBLABJNL02)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala de Projeção 1 (TBPRJ€1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 208A (TB208AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 109A (TB109AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Desenho 4 (DES4G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Desenho 5 (DES5G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Desenho 6 (DES6G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Desenho 2 (DES2G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Desenho 3 (DES12G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Desenho 1 (DES1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Desenho 3 (DES3G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 12 (TB12G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laboratório de Conforto (LBCONFORTO)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 10 (TB10G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala de aula - Edição Radio TV (TBEDICAO)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 11 (TB11G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Grafico I (TBLBGRF1G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 01 (TBLBINF1G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 02 (TBLBINF2G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 03 (TBLBINF3G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 04 (TBLBINF4G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 05 (TBLBINF5G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 06 (TBLBINF6G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 07 (TBLBINF7G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 08 (TBLINF8G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 3 (TBCETTAL3G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 5 (TBCETTAL5G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 11 (TBCETTAL11)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 4 (TBCETTAL4G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 10 (TBCETTAL10)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 2 (TBCETTAL2G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 1 (TBCETTAL1G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 8 (TBCETTAL8G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 6 (TBCETTAL6G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 9 (TBCETTAL9G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Lab. Informática 7 (TBCETTAL7G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Projeto (TBLBPROJG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Radio 1 (TBLBRAD1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Radio 2 (TBLBRAD2G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Redes (TBLBREDESG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. TV (TBLBTVG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Projeção I (TBPRJC1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Projeção II (TBPRJC2G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "TRC 1º Sem (TBTRC1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "TRC 3º Sem (TBTRC3G)");  cv.put("id_bloco", 7); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 105 (TB105H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 106 (TB106H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 107 (TB107H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 108 (TB108H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 109 (TB109H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 110 (TB110H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 111 (TB111H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 112 (TB112H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 113 (TB113H)");  cv.put("id_bloco", 8); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 114 (TB114H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 07 (LAB_INF-07)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 06 (LAB_INF-06)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 01 (LAB_INF-01)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 02 (LAB_INF-02)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 03 (LAB_INF-03)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 04 (LAB_INF-04)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 05 (LAB_INF-05)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 08 (LAB_INF-08)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informática 09 (LAB_INF-09)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 1 (TBHV1E)");  cv.put("id_bloco", 9); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 2 (TBHV2E)");  cv.put("id_bloco", 9); dados.add(cv);

//        nao mapeado ainda:

//        cv = new ContentValues(); cv.put("descricao", "Sala 1 (TBGARJ001)");  cv.put("id_bloco", 10); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Laboratório de Gin stica (TBGARJL01)");  cv.put("id_bloco", 10); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Laboratório de Lutas Esportiva (TBGARJL02)");  cv.put("id_bloco", 10); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Laboratório de Musculação (TBGARJL03)");  cv.put("id_bloco", 10); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 001 (TBUHEN001)");  cv.put("id_bloco", 11); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 002 (TBUHEN002)");  cv.put("id_bloco", 11); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 003 (TBUHEN003)");  cv.put("id_bloco", 11); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 004 (TBUHEN004)");  cv.put("id_bloco", 11); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Sala 005 (TBUHEN005)");  cv.put("id_bloco", 11); dados.add(cv);
//        cv = new ContentValues(); cv.put("descricao", "Auditório (TBUHENAUD)");  cv.put("id_bloco", 11); dados.add(cv);
        for(ContentValues contv : dados){
            db.insert("salas", null, contv);
        }
        dados.clear();


    }
}
