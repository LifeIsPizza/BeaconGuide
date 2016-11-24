package bfs_app;

import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
/*import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;*/

/**
 * Created by Enrico on 19/10/2016.
 */

public class Beacon implements java.io.Serializable{
    public Beacon(int idb, String zona, String map_id, ArrayList<Nearby> vicini, ArrayList<Nearby> vic_dis) {
        this.idb = idb;
        this.zona = zona;
        this.map_id = map_id;
        this.vicini = vicini;
        this.vic_dis = vic_dis;
    }

    public Beacon(int idb, String zona, String map_id, JSONArray vicini, JSONArray vic_dis, Graph graphNor, Graph graphDis) {
        this.idb = idb;
        this.zona = zona;
        this.map_id = map_id;
        this.vicini = new ArrayList<>();
        if(vicini != null) {
            try {
                Log.d("VicLeng", String.valueOf(vicini.length()));
                for (int k = 0; k < vicini.length(); k++) {
                    Log.d("VicLeng", String.valueOf(vicini.getJSONObject(k).getInt("gradi")));
                    this.vicini.add(new Nearby(
                            vicini.getJSONObject(k).getInt("beacon_arrivo"),
                            vicini.getJSONObject(k).getInt("gradi"),
                            vicini.getJSONObject(k).getInt("costo")
                    ));
                    graphNor.addLink(idb,vicini.getJSONObject(k).getInt("beacon_arrivo"),
                            vicini.getJSONObject(k).getInt("gradi"),
                            vicini.getJSONObject(k).getInt("costo"));
                }
            } catch (Exception E) {

            }
        }
        this.vic_dis = new ArrayList<>();
        if (vic_dis != null) {
            try {
                Log.d("DisLeng", String.valueOf(vic_dis.length()));
                for (int k = 0; k < vic_dis.length(); k++) {
                    Log.d("DisLeng", String.valueOf(vic_dis.getJSONObject(k).getInt("gradi")));
                    this.vic_dis.add(new Nearby(
                            vic_dis.getJSONObject(k).getInt("beacon_arrivo"),
                            vic_dis.getJSONObject(k).getInt("gradi"),
                            vic_dis.getJSONObject(k).getInt("costo")
                    ));
                    graphDis.addLink(idb,vicini.getJSONObject(k).getInt("beacon_arrivo"),
                            vicini.getJSONObject(k).getInt("gradi"),
                            vicini.getJSONObject(k).getInt("costo"));
                }
            } catch (Exception E) {

            }
        }
    }

    private int idb;
    private String zona;
    private String map_id;
    private ArrayList<Nearby> vicini;
    private ArrayList<Nearby> vic_dis;

    public int getIdb() {
        return idb;
    }

    public void setIdb(int idb) {
        this.idb = idb;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getMap_id() {
        return map_id;
    }

    public void setMap_id(String map_id) {
        this.map_id = map_id;
    }

    public ArrayList<Nearby> getVicini() {
        return vicini;
    }

    public void setVicini(ArrayList<Nearby> vicini) {
        this.vicini = vicini;
    }

    public ArrayList<Nearby> getVic_dis() {
        return vic_dis;
    }

    public void setVic_dis(ArrayList<Nearby> vic_dis) {
        this.vic_dis = vic_dis;
    }



}





