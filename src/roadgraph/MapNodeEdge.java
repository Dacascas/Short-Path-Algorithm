package roadgraph;

import geography.GeographicPoint;

/**
 * Class MapNodeEdge
 * Purpose and description of class:
 * The main idea of the class is incapsulate related to edge data, like start/end location etc
 */
public class MapNodeEdge {
    private GeographicPoint start;
    private GeographicPoint end;
    private String streetName;
    private String roadType;
    private double length;
    private Integer speed;
    private double time;

    public MapNodeEdge(
            GeographicPoint startPoint,
            GeographicPoint endPoint,
            String pointStreetName,
            String pointRoadType,
            double lengthPoint
    ) {
        start = startPoint;
        end = endPoint;
        streetName = pointStreetName;
        roadType = pointRoadType;
        length = lengthPoint;
        speed = new SpeedRoadType().getSpeedValue(pointRoadType);
        time = this.length / this.speed;
    }

    /**
     * Get start location of the edge
     *
     * @return GeographicPoint
     */
    public GeographicPoint getStart() {
        return start;
    }

    /**
     * Get end location of the edge
     *
     * @return GeographicPoint
     */
    public GeographicPoint getEnd() {
        return end;
    }

    public String getStreetName() {
        return streetName;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Double getTime()
    {
        return this.time;
    }
}
