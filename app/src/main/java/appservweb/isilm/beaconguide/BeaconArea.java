package appservweb.isilm.beaconguide;

/**
 * Created by Ele on 14/11/2016.
 */

public class BeaconArea {

    private int beaconId;
    private int idMap;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public BeaconArea(int beaconId, int idMap, int startX, int startY, int endX, int endY) {
        this.beaconId = beaconId;
        this.idMap = idMap;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    public int getBeaconId() {
        return beaconId;
    }

    public int getIdMap() {
        return idMap;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }
}
