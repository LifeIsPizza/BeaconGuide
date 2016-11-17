package appservweb.isilm.beaconguide;

/**
 * Created by Enrico on 19/10/2016.
 */

public class Nearby implements java.io.Serializable{
    private int ide;
    private int gradi;
    private int peso;


    public Nearby(int ide, int gradi, int peso){
        this.ide = ide;
        this.gradi = gradi;
        this.peso = peso;
    }

    public int getGradi() {
        return gradi;
    }

    public int getIde() {
        return ide;
    }

    public int getPeso() {
        return peso;
    }

    public void setGradi(int gradi) {
        this.gradi = gradi;
    }

    public void setIde(int ide) {
        this.ide = ide;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }
}
