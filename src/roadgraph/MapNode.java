package roadgraph;

import geography.GeographicPoint;
import java.util.LinkedList;
import java.util.List;

/**
 * Class MapNode
 * Purpose and description of class:
 * The main idea of the class is incapsulate vertex and related to vertex particulate edges
 */
public class MapNode implements Comparable<MapNode>
{
    private GeographicPoint point;
    private LinkedList edge;
    private double distance;
    private double pred;

    /**
     * Constructor
     *
     * @param geographicPoint
     */
    MapNode(GeographicPoint geographicPoint) {
        point = geographicPoint;
        edge = new LinkedList();
    }

    public GeographicPoint getPoint() {
        return point;
    }

    public void setPoint(GeographicPoint point) {
        this.point = point;
    }

    /**
     * Return edges of the vertex
     *
     * @return
     */
    public List<MapNodeEdge> getEdges() {
        return edge;
    }

    /**
     * Add edge to the list of edges
     *
     * @param mapNodeEdge
     */
    void addEdge(MapNodeEdge mapNodeEdge) {
        edge.addLast(mapNodeEdge);
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int compareTo(MapNode o) {
        return Double.compare(this.pred + this.distance, o.getDistance() + o.getPred());
    }

    public double getPred() {
        return pred;
    }

    public void setPred(double prev) {
        this.pred = prev;
    }
}
