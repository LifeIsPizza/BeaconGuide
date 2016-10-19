package appservweb.isilm.beaconguide;

import java.util.List;

/**
 * Created by Enrico on 19/10/2016.
 */

public class Beacon {
    public Beacon(int idb, String zona, List<Nearby> vicini, List<Nearby> vic_dis) {
        this.idb = idb;
        this.zona = zona;
        this.vicini = vicini;
        this.vic_dis = vic_dis;
    }

    private int idb;
    private String zona;
    private List<Nearby> vicini;
    private List<Nearby> vic_dis;

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

    public List<Nearby> getVicini() {
        return vicini;
    }

    public void setVicini(List<Nearby> vicini) {
        this.vicini = vicini;
    }

    public List<Nearby> getVic_dis() {
        return vic_dis;
    }

    public void setVic_dis(List<Nearby> vic_dis) {
        this.vic_dis = vic_dis;
    }



}





