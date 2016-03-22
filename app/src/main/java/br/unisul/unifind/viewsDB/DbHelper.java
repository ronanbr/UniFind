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

        onCreateAddBlocos("blocos", db, "Bloco G", -28.475490, -49.026258, 1);
        onCreateAddBlocos("blocos", db, "Saúde", -28.480209, -49.021578, 1);
        onCreateAddBlocos("servicos", db, "Centro de Convivência", -28.480684, -49.021079, 1);
        onCreateAddBlocos("servicos", db, "Ginásio", -28.480798, -49.020101, 1);
        onCreateAddBlocos("blocos", db, "Sede", -28.482543, -49.019273, 1);
        onCreateAddCampus("campi", db, "Tubarão");
        onCreateAddBlocos("servicos", db, "GTI", -28.483659, -49.013435, 1);

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
}
