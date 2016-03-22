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
    private static final String NOME_BASE = "UniFindData";
    private static final int VERSAO_BASE = 43;

    public DbHelper(Context context) {
        super(context, NOME_BASE, null, VERSAO_BASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlCreateTabelaCampi = "CREATE TABLE campi("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"descricao TEXT "
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

        db.execSQL(sqlCreateTabelaCampi);
        db.execSQL(sqlCreateTabelaBlocos);
        db.execSQL(sqlCreateTabelaSalas);
        db.execSQL(sqlCreateTabelaServicos);

        onCreateAddCampus("campi", db, "Tubarão");
//        onCreateAddBlocos("blocos", db, "Bloco G", -28.475490, -49.026258, 1);
//        onCreateAddBlocos("blocos", db, "Saúde", -28.480209, -49.021578, 1);
//        onCreateAddBlocos("servicos", db, "Centro de Convivência", -28.480684, -49.021079, 1);
//        onCreateAddBlocos("servicos", db, "Ginásio", -28.480798, -49.020101, 1);
//        onCreateAddBlocos("blocos", db, "Sede", -28.482543, -49.019273, 1);
//        onCreateAddBlocos("servicos", db, "GTI", -28.483659, -49.013435, 1);

        populaBase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("banco", "UPGRADE banco versao anterior: " + VERSAO_BASE);

        //drop tables
        db.execSQL("DROP TABLE campi");
        db.execSQL("DROP TABLE blocos");
        db.execSQL("DROP TABLE salas");
        db.execSQL("DROP TABLE servicos");
        //cria novamente
        onCreate(db);


    }

    public void onCreateAddCampus(String table, SQLiteDatabase db, String descricao){
        ContentValues cv = new ContentValues();

        cv.put("descricao", descricao);

        db.insert(table, null, cv);
    }

    public void onCreateAddBlocos(String table, SQLiteDatabase db, String descricao, Double lat, Double lon, int idCampus){
        ContentValues cv = new ContentValues();

        cv.put("descricao", descricao);
        cv.put("latitude", lat);
        cv.put("longitude", lon);
        cv.put("id_campus", idCampus);

        db.insert(table, null, cv);
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
                "SELECT * FROM campi";

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
                "SELECT blo.id, blo.descricao as bloDesc, blo.latitude, blo.longitude, " +
                        "cam.id as idCam, cam.descricao " +
                        "FROM blocos blo " +
                        "INNER JOIN campi cam ON (blo.id_campus = cam.id)";

        Cursor c = db.rawQuery(sqlSelectTodosBlocos, null);
        if (c.moveToFirst()){
            do{
                Bloco bloco = new Bloco();
                bloco.setId(c.getInt(c.getColumnIndex("blo.id")));
                bloco.setDescricao(c.getString(c.getColumnIndex("bloDesc")));
                bloco.setLatitude(c.getDouble(c.getColumnIndex("blo.latitude")));
                bloco.setLongitude(c.getDouble(c.getColumnIndex("blo.longitude")));

                Campus campus = new Campus();
                campus.setDescricao(c.getString(c.getColumnIndex("cam.descricao")));
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
                        "INNER JOIN campi cam ON (blo.id_campus = cam.id) ";

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
                        "INNER JOIN campi cam ON (ser.id_campus = cam.id) ";

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
                        "AND cam.id = "+idCampus;

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
                        "AND idBlocoSala = "+idBloco;

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
                        "WHERE ser.descricao LIKE '%"+filtro+"%' " +
                        "AND cam.id = "+idCampus;

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

    public void populaBase(SQLiteDatabase db){
        ContentValues cv = new ContentValues();

        ArrayList<ContentValues> dados = new ArrayList<>();

        cv = new ContentValues(); cv.put("descricao", "BLOCO A - Sede");  cv.put("latitude", -28.48254300);  cv.put("longitude", -49.01927300);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO B - Dehon");  cv.put("latitude", -28.48254300);  cv.put("longitude", -49.01927300);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO C - Saude");  cv.put("latitude", -28.48018400);  cv.put("longitude", -49.02178800);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO D - Pedagógico");  cv.put("latitude", -28.47963800);  cv.put("longitude", -49.02135900);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO E");  cv.put("latitude", -28.47963800);  cv.put("longitude", -49.02135900);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO F");  cv.put("latitude", -28.47963800);  cv.put("longitude", -49.02135900);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO G - Cettal");  cv.put("latitude", -28.47549000);  cv.put("longitude", -49.02625800); cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO H - Informatica");  cv.put("latitude", -28.47521400);  cv.put("longitude", -49.02552400);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "BLOCO EHV");  cv.put("latitude", -28.47521400);  cv.put("longitude", -49.02552400);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "GARJ");  cv.put("latitude", 	-28.48079800);  cv.put("longitude", -49.02010100);  cv.put("id_campus", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Und Hosp de Ensino");  cv.put("latitude", -28.48079800);  cv.put("longitude", -49.02010100);  cv.put("id_campus", 1); dados.add(cv);

        for (ContentValues cvalue: dados){
            db.insert("blocos", null, cvalue);
        }

        dados.clear();

        cv = new ContentValues(); cv.put("descricao", "Sala de Proje‡Æo I (TBPRJC1A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 204 (TB204A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 205 (TB205A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 206 (TB206A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 207 (TB207A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 208 (TB208A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 209 (TB209A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 105 DESATIVADA (TB105A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 106 (TB106A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 107 DESATIVADA (TB107A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 108 DESATIVADA (TB108A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 109 DESATIVADA (TB109A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 110 DESATIVADA (TB110A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 111 DESATIVADA (TB111A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 113 DESATIVADA (TB113A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 101 DESATIVADA (TB101A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 DESATIVADA (TB102A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 103 DESATIVADA (TB103A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 104 DESATIVADA (TB104A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 116 DESATIVADA (TB116A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 119 (TB119A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 120 (TB120A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 121 (TB121A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 210 (TB210A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 211 (TB211A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 214 (TB214A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Moda - Ateliˆ (ATELIE)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 02  (TB02A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 03  (TB03A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 22   (TB22A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 219DESATIVADA (TB219A)");  cv.put("id_bloco", 1); dados.add(cv);
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
        cv = new ContentValues(); cv.put("descricao", "Sala 217 DESATIVADA (TB217A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 218 DESATIVADA (TB218A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 222 A DESATIVAD (TB222A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "LABORATàRIO CONTµBIL (LABCONTABI)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 223 DESATIVADA (TB223A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 220 DESATIVADA (TB220A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 221 DESATIVADA (TB221A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 224 (TB224A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 04  (TB04A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 05  (TB05A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica I (TBLBINF1A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica II (TBLBINF2A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica (TBLBINF4A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica P¢s (TBLBINFPOS)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica IV (TBLINF4A)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat.Desenho 1 TubarÆ (TBDESENHOA)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "SalÆo Nobre (TBSLNOBA)");  cv.put("id_bloco", 1); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 126  (TB126B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 127  (TB127B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 128  (TB128B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 129  (TB129B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 130  (TB130B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 131  (TB131B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 132  (TB132B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 133  (TB133B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 134  (TB134B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 135   (TB135B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 136  (TB136B)");  cv.put("id_bloco", 2); dados.add(cv);
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
        cv = new ContentValues(); cv.put("descricao", "Sala 123  (TB123B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 124  (TB124B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 125  (TB125B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 (TB102B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 103 (TB103B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 121 (TB121B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala I Infantil (TBIFANT1B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala II Infantil (TBIFANT2B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala III Infantil (TBIFANT3B)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Maternal (TBMTERNB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala Leitura (TBLTURAB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica (TBLBINFB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Ciˆncias (TBLBCIÒB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Banda Marcial (TBBDMARCB)");  cv.put("id_bloco", 2); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab de Enfermagem (TBLABENFC)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Audit¢rio 201C (TB201C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala de Gin stica (GINASTICA)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 004 - Policl¡nica (TB004C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 001 - Policl¡nica (TB001C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 002 - Policl¡nica (TB002C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 003 - Policl¡nica (TB003C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "CLINICA ESCOLA - POLICLÖNICA (CLIN. ESC.)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. El‚troest‚tica (TBLABESTET)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab Cosmetologia e Est‚tica (TBLABIP)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "TCC curso de Odontologia (TCC ODONTO)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "SALA GINµSTICA (SALA GINAS)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Qui. Farm. (TB24C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat¢rio de Fisioterapia (TB01C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Cl¡nica Odontol¢gica II (TBC008)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Cl¡nica Odontol¢gica III (TBC009)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Interpreta‡Æo Radiol¢gica (TB13C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microspia B (TB14C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat¢rio (TB09C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat¢rio de Comestologia (LABTCO)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Pedagogia (TBL10C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Pneumocardiologia (TBL12C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Neurologia (TBL11C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "LABTCO2 (LABTCO2)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab.Fisio (LAB.FISIO)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Radio.Imagem (RADIO.IMAG)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Neurologia (LAB.ANEX)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Bioqu¡mica (TB25C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Zoologia (TBLB03C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Micriscopia A (TB16C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microbiologia (TB17C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Microbiologia (MICRO B)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Parasitologia (TB19C)");  cv.put("id_bloco", 3); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Audit¢rio - 211D (TB211D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Audit¢rio - 311D (TB311D)");  cv.put("id_bloco", 4); dados.add(cv);
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
        cv = new ContentValues(); cv.put("descricao", "Sala 101  (TB101D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 108 (TB108D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 4 (TBLBINF4D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Informatica 3 (TBLBINF3D)");  cv.put("id_bloco", 4); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 101 (TB101E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 104 - Biot‚rio (TB104E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Anat“mico (TB1E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Anat“mico (TB2E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Enfermagem (TB3E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Tec. Cir£rgicas (TB102E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Tec. Cir£rgicas (TB103E)");  cv.put("id_bloco", 5); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 117 (TB117F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala - 08A (TB08AF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala - 09A (TB09AF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala - 07A (TB07AF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Atelie de Arquitetura (TBATEARQF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Bromatologia (TB1F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inorga. e Anal¡tica (TB21F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inorgƒnica B (TB22F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Alimentos (TB26F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Cromato. e Infrav. (TB28F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de abs. At“m. e Espec. (TB29F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Eng Quimica (TB2F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Qui. Organica (TB5F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Redes e Software (TB7F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. F¡sica (TB8F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Herb rio (TBLABHERF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Conforto T‚rmico (LBCONFTTB)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica Default (LBINFTB_)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Computa‡Æo (TB6F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Computa‡Æo (TBLBCOMPF)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB15F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB17F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB32F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Desenho (TB33F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de F¡sic./Qu¡m. A (TB23F)");  cv.put("id_bloco", 6); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de F¡sci./Qu¡m. B (TB24F)");  cv.put("id_bloco", 6); dados.add(cv);
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
        cv = new ContentValues(); cv.put("descricao", "Sala 110 A (TB110AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 110 (TB110G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 111 (TB111G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 112 (TB112G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 113 (TB113G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 114 (TB114G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 115 (TB115G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 116 (TB116G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 101 (TB101G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 A (TB102AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 102 (TB102G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 103 (TB103G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 104 (TB104G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 216 (TB216G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala L09 (TB9G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 217 (TB217G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 210 A (TB210AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 202AG (TB202AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 211 AG (TB211AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 206 AG (TB206AG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "LABORATORIO DE PROJETO (TBLABPROJ)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "LABORATORIO DE JORNALISMO 01 (TBLABJNL01)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "LABORATORIO DE JORNALISMO 02 (TBLABJNL02)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala de Proje‡Æo 1 - 7 (TBPRJ€1G)");  cv.put("id_bloco", 7); dados.add(cv);
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
        cv = new ContentValues(); cv.put("descricao", "Laborat¢rio de Conforto (LBCONFORTO)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 10 (TB10G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala de aula - Edi‡Æo Radio TV (TBEDI€ÇO)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 11 (TB11G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Grafico I (TBLBGRF1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 01 (TBLBINF1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 02 (TBLBINF2G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 03 (TBLBINF3G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 04 (TBLBINF4G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 05 (TBLBINF5G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 06 (TBLBINF6G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 07 (TBLBINF7G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 08 (TBLINF8G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 3 (TBCETTAL3G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 5 (TBCETTAL5G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 11 (TBCETTAL11)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 4 (TBCETTAL4G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 10 (TBCETTAL10)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 2 (TBCETTAL2G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 1 (TBCETTAL1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 8 (TBCETTAL8G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 6 (TBCETTAL6G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 9 (TBCETTAL9G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Inform tica 7 (TBCETTAL7G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Projeto (TBLBPROJG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Radio 1 (TBLBRAD1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Radio 2 (TBLBRAD2G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Redes (TBLBREDESG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab.  TV (TBLBTVG)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Proje‡Æo I (TBPRJC1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. Proje‡Æo II (TBPRJC2G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "TRC 1§ Sem (TBTRC1G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "TRC 3§ Sem (TBTRC3G)");  cv.put("id_bloco", 7); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 105 (TB105H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 106 (TB106H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 107 (TB107H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 108 (TB108H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 109 (TB109H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 110 (TB110H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 111 (TB111H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 112 (TB112H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 113 (TB113H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 114 (TB114H)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 07 (LAB_INF-07)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 06 (LAB_INF-06)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 01 (LAB_INF-01)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 02 (LAB_INF-02)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 03 (LAB_INF-03)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 04 (LAB_INF-04)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 05 (LAB_INF-05)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 08 (LAB_INF-08)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Lab. de Inform tica 09 (LAB_INF-09)");  cv.put("id_bloco", 8); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 1 (TBHV1E)");  cv.put("id_bloco", 9); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 2 (TBHV2E)");  cv.put("id_bloco", 9); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 001 - GARJ (TBGARJ001)");  cv.put("id_bloco", 10); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat¢rio de Gin stica (TBGARJL01)");  cv.put("id_bloco", 10); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat¢rio de Lutas Esportiva (TBGARJL02)");  cv.put("id_bloco", 10); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Laborat¢rio de Muscula‡Æo (TBGARJL03)");  cv.put("id_bloco", 10); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 001 - Und Hosp de Ensino (TBUHEN001)");  cv.put("id_bloco", 11); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 002 - Und Hosp de Ensino (TBUHEN002)");  cv.put("id_bloco", 11); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 003 - Und Hosp de Ensino (TBUHEN003)");  cv.put("id_bloco", 11); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 004 - Und Hosp de Ensino (TBUHEN004)");  cv.put("id_bloco", 11); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Sala 005 - Und Hosp de Ensino (TBUHEN005)");  cv.put("id_bloco", 11); dados.add(cv);
        cv = new ContentValues(); cv.put("descricao", "Audit¢rio - Und Hosp de Ensino (TBUHENAUD)");  cv.put("id_bloco", 11); dados.add(cv);

        for (ContentValues cvalue: dados){
            db.insert("salas", null, cvalue);
        }


    }
}
