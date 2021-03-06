/**
 * @author UCSD MOOC development team and YOU
 * <p>
 * A class which reprsents a graph of geographic locations
 * Nodes in the graph are intersections between
 */
package roadgraph;


import java.util.*;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

/**
 * @author UCSD MOOC development team and YOU
 * Class MapGraph
 * A class which represents a graph of geographic locations
 * Nodes in the graph are intersections between
 */
public class MapGraph {
    private HashMap<GeographicPoint, MapNode> nodes;
    private HashSet<MapNode> visited;
    private CacheResult cached;

    /**
     * Create a new empty MapGraph
     */
    public MapGraph() {
        this.cached = new CacheResult();
        this.nodes = new HashMap<GeographicPoint, MapNode>();
    }

    /**
     * Get the number of vertices (road intersections) in the graph
     *
     * @return The number of vertices in the graph.
     */
    public int getNumVertices() {
        return this.nodes.size();
    }

    /**
     * Return the intersections, which are the vertices in this graph.
     *
     * @return The vertices in this graph as GeographicPoints
     */
    public Set<GeographicPoint> getVertices() {
        return nodes.keySet();
    }

    /**
     * Get the number of road segments in the graph
     *
     * @return The number of edges in the graph.
     */
    public int getNumEdges() {
        int edges = 0;

        for (GeographicPoint key : nodes.keySet()) {
            MapNode mapNode = nodes.get(key);
            edges += mapNode.getEdges().size();
        }

        return edges;
    }

    /**
     * Add a node corresponding to an intersection at a Geographic Point
     * If the location is already in the graph or null, this method does
     * not change the graph.
     *
     * @param location The location of the intersection
     * @return true if a node was added, false if it was not (the node
     * was already in the graph, or the parameter is null).
     */
    public boolean addVertex(GeographicPoint location) {
        if (nodes.containsKey(location)) {
            return true;
        }

        nodes.put(location, new MapNode(location));

        return false;
    }

    /**
     * Adds a directed edge to the graph from pt1 to pt2.
     * Precondition: Both GeographicPoints have already been added to the graph
     *
     * @param from     The starting point of the edge
     * @param to       The ending point of the edge
     * @param roadName The name of the road
     * @param roadType The type of the road
     * @param length   The length of the road, in km
     * @throws IllegalArgumentException If the points have not already been
     *                                  added as nodes to the graph, if any of the arguments is null,
     *                                  or if the length is less than 0.
     */
    public void addEdge(
            GeographicPoint from,
            GeographicPoint to,
            String roadName,
            String roadType,
            double length
    ) throws IllegalArgumentException {
        if (!nodes.containsKey(from) || !nodes.containsKey(to)) {
            throw new IllegalArgumentException("Point(s) is not present in vertice");
        }

        MapNode mapNode = this.nodes.get(from);
        mapNode.addEdge(new MapNodeEdge(from, to, roadName, roadType, length));
    }

    /**
     * Find the path from start to goal using breadth first search
     *
     * @param start The starting location
     * @param goal  The goal location
     * @return The list of intersections that form the shortest (unweighted)
     * path from start to goal (including both start and goal).
     */
    public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
        // Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {
        };
        return bfs(start, goal, temp);
    }

    /**
     * Find the path from start to goal using breadth first search
     *
     * @param start        The starting location
     * @param goal         The goal location
     * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
     * @return The list of intersections that form the shortest (unweighted)
     * path from start to goal (including both start and goal).
     */
    public List<GeographicPoint> bfs(
            GeographicPoint start,
            GeographicPoint goal,
            Consumer<GeographicPoint> nodeSearched
    ) {
        String key = this.cached.generateKey("bfs", start, goal);
        List<GeographicPoint> list = this.cached.getCache(key);

        if (!list.isEmpty()) {
            return list;
        }

        LinkedList<GeographicPoint> queue = new LinkedList<>();
        HashSet<GeographicPoint> visited = new HashSet<>();
        HashMap<GeographicPoint, GeographicPoint> path = new HashMap<>();
        queue.add(start);

        while (queue.size() != 0) {
            GeographicPoint queueElement = queue.poll();
            nodeSearched.accept(queueElement);
            if (queueElement.equals(goal)) {
                list = getShortPath(path, start, goal);
                this.cached.setCache(key, list);
                return list;
            }

            visited.add(queueElement);

            MapNode mapNode = this.nodes.get(queueElement);
            List<MapNodeEdge> edges = mapNode.getEdges();

            for (MapNodeEdge edge : edges) {
                if (!visited.contains(edge.getEnd())) {
                    queue.add(edge.getEnd());
                    path.put(edge.getEnd(), queueElement);
                }
            }
        }

        return null;
    }

    /**
     * Additional method which create short path base on the visited points
     *
     * @param elements HashMap
     * @param goal     GeographicPoint
     * @return List
     */
    private List<GeographicPoint> getShortPath(HashMap elements, GeographicPoint start, GeographicPoint goal) {
        ArrayList<GeographicPoint> result = new ArrayList<GeographicPoint>();

        if (elements.size() > 0) {
            result.add(goal);
            while (goal != null) {
                if (elements.containsKey(goal)) {
                    GeographicPoint parent = (GeographicPoint) elements.get(goal);
                    result.add(parent);

                    goal = parent;
                } else {
                    break;
                }
            }
            Collections.reverse(result); // in this case after collect list of points it will be in reverted mode
        }

        return result;
    }


    /**
     * Find the path from start to goal using Dijkstra's algorithm
     *
     * @param start The starting location
     * @param goal  The goal location
     * @return The list of intersections that form the shortest path from
     * start to goal (including both start and goal).
     */
    public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
        // Dummy variable for calling the search algorithms
        // You do not need to change this method.
        Consumer<GeographicPoint> temp = (x) -> {
        };
        return dijkstra(start, goal, temp);
    }

    /**
     * Find the path from start to goal using Dijkstra's algorithm
     *
     * @param start        The starting location
     * @param goal         The goal location
     * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
     * @return The list of intersections that form the shortest path from
     * start to goal (including both start and goal).
     */
    public List<GeographicPoint> dijkstra(
            GeographicPoint start,
            GeographicPoint goal,
            Consumer<GeographicPoint> nodeSearched
    ) {
        String key = this.cached.generateKey("dj", start, goal);
        List<GeographicPoint> list = this.cached.getCache(key);

        if (!list.isEmpty()) {
            return list;
        }

        // Hook for visualization.  See writeup.
        PriorityQueue<MapNode> queue = new PriorityQueue<MapNode>();
        this.visited = new HashSet<>();
        HashMap<GeographicPoint, GeographicPoint> path = new HashMap<>();

        setBaseInfiniteValue();
        this.nodes.get(start).setDistance(0);
        queue.add(this.nodes.get(start));

        while (queue.size() > 0) {
            MapNode queueElement = queue.remove();
            System.out.println("DIJKSTRA visiting[NODE at location(" + queueElement.getPoint().toString() + ")");
            if (!this.visited.contains(queueElement)) {
                this.visited.add(queueElement);
                nodeSearched.accept(queueElement.getPoint());

                if (queueElement.getPoint().equals(goal)) {
                    list = getShortPath(path, start, goal);
                    this.cached.setCache(key, list);
                    return list;
                }

                List<MapNodeEdge> edges = queueElement.getEdges();
                for (MapNodeEdge edge : edges) {
                    MapNode temp = this.nodes.get(edge.getEnd());
                    if (!visited.contains(temp)) {
                        if (temp.getDistance() > (edge.getTime() + queueElement.getDistance())) {
                            temp.setDistance(edge.getTime() + queueElement.getDistance());
                            queue.add(temp);
                            path.put(edge.getEnd(), queueElement.getPoint());
                        }
                    }
                }
            }
        }

        return null;
    }

    private void setBaseInfiniteValue() {
        for (GeographicPoint n : this.nodes.keySet()) {
            this.nodes.get(n).setDistance(Integer.MAX_VALUE);
        }
    }

    /**
     * Find the path from start to goal using A-Star search
     *
     * @param start The starting location
     * @param goal  The goal location
     * @return The list of intersections that form the shortest path from
     * start to goal (including both start and goal).
     */
    public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
        // Dummy variable for calling the search algorithms
        Consumer<GeographicPoint> temp = (x) -> {
        };
        return aStarSearch(start, goal, temp);
    }

    /**
     * Find the path from start to goal using A-Star search
     *
     * @param start        The starting location
     * @param goal         The goal location
     * @param nodeSearched A hook for visualization.  See assignment instructions for how to use it.
     * @return The list of intersections that form the shortest path from
     * start to goal (including both start and goal).
     */
    public List<GeographicPoint> aStarSearch(
            GeographicPoint start,
            GeographicPoint goal,
            Consumer<GeographicPoint> nodeSearched
    ) {
        String key = this.cached.generateKey("aStar", start, goal);
        List<GeographicPoint> list = this.cached.getCache(key);

        if (!list.isEmpty()) {
            return list;
        }

        // Hook for visualization.  See writeup.
        PriorityQueue<MapNode> queue = new PriorityQueue<MapNode>();
        this.visited = new HashSet<>();
        HashMap<GeographicPoint, GeographicPoint> path = new HashMap<>();
        double distance = 0;
        double pred = 0;
        setBaseInfiniteValue();
        this.nodes.get(start).setDistance(0);
        queue.add(this.nodes.get(start));

        while (queue.size() > 0) {
            MapNode queueElement = queue.remove();
            System.out.println("A* visiting[NODE at location(" + queueElement.getPoint().toString() + ")");
            if (!this.visited.contains(queueElement)) {
                this.visited.add(queueElement);
                nodeSearched.accept(queueElement.getPoint());

                if (queueElement.getPoint().equals(goal)) {
                    list = getShortPath(path, start, goal);
                    this.cached.setCache(key, list);
                    return list;
                }

                List<MapNodeEdge> edges = queueElement.getEdges();
                System.out.print(" intersect street: ");
                for (MapNodeEdge edge : edges) {
                    MapNode temp = this.nodes.get(edge.getEnd());
                    System.out.print(edge.getStreetName() + ", ");
                    if (!this.visited.contains(temp)) {
                        distance = edge.getTime() + queueElement.getDistance();
                        if (temp.getDistance() > distance) {
                            pred = predictLength(temp, this.nodes.get(goal));
                            temp.setDistance(distance);
                            temp.setPred(pred);
                            queue.add(temp);
                            path.put(edge.getEnd(), queueElement.getPoint());
                            System.out.println("Actual = " + distance + ", Pred: " + (pred + distance));
                        }
                    }
                }
            }
        }

        return null;
    }

    private double predictLength(MapNode start, MapNode goal) {
        return Math.abs(goal.getPoint().distance(start.getPoint()));
    }

    public List<GeographicPoint> tsp(
            GeographicPoint start,
            GeographicPoint goal,
            Consumer<GeographicPoint> nodeSearched
    ) {
        List edges = this.nodes.get(start).getEdges();

        if (edges.isEmpty()) {
            throw new RuntimeException("Empty edge for the vertex");
        }

        boolean startIterate = true;

        while (startIterate) {
            this.nodes.get(start);
        }

        return new LinkedList<>();
    }

    public static void main(String[] args) {
        MapGraph simpleTestMap = new MapGraph();
        GraphLoader.loadRoadMap("data/testdata/simpletest.map", simpleTestMap);

        GeographicPoint testStart = new GeographicPoint(1.0, 1.0);
        GeographicPoint testEnd = new GeographicPoint(8.0, -1.0);

        System.out.println("Test 1 using simpletest: Dijkstra should be 9 and AStar should be 5");
        List<GeographicPoint> testroute = simpleTestMap.dijkstra(testStart, testEnd);
        System.out.println("Real " + simpleTestMap.visited.size());
        List<GeographicPoint> testroute2 = simpleTestMap.aStarSearch(testStart, testEnd);
        System.out.println("Real " + simpleTestMap.visited.size());

        MapGraph testMap = new MapGraph();
        GraphLoader.loadRoadMap("data/maps/utc.map", testMap);

        // A very simple test using real data
        testStart = new GeographicPoint(32.869423, -117.220917);
        testEnd = new GeographicPoint(32.869255, -117.216927);
        System.out.println("Test 2 using utc: Dijkstra should be 13 and AStar should be 5");
        testroute = testMap.dijkstra(testStart, testEnd);
        System.out.println("Real " + testMap.visited.size());
        testroute2 = testMap.aStarSearch(testStart, testEnd);
        System.out.println("Real " + testMap.visited.size());


        // A slightly more complex test using real data
        testStart = new GeographicPoint(32.8674388, -117.2190213);
        testEnd = new GeographicPoint(32.8697828, -117.2244506);
        System.out.println("Test 3 using utc: Dijkstra should be 37 and AStar should be 10");
        testroute = testMap.dijkstra(testStart, testEnd);
        System.out.println("Real " + testMap.visited.size());
        testroute2 = testMap.aStarSearch(testStart, testEnd);
        System.out.println("Real " + testMap.visited.size());


        MapGraph theMap = new MapGraph();
        System.out.print("DONE. \nLoading the map...");
        GraphLoader.loadRoadMap("data/maps/utc.map", theMap);
        System.out.println("DONE.");

        GeographicPoint start = new GeographicPoint(32.8648772, -117.2254046);
        GeographicPoint end = new GeographicPoint(32.8660691, -117.217393);

        List<GeographicPoint> route = theMap.dijkstra(start, end);
        System.out.println("Real " + theMap.visited.size());
        List<GeographicPoint> route2 = theMap.aStarSearch(start, end);
        System.out.println("Real " + theMap.visited.size());
    }
}
