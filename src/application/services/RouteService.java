package application.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


import application.DataSet;
import application.MapApp;
import application.MarkerManager;
import application.RouteVisualization;
import application.controllers.RouteController;

import geography.GeographicPoint;
import geography.RoadSegment;
import gmapsfx.GoogleMapView;
import gmapsfx.javascript.object.GoogleMap;
import gmapsfx.javascript.object.LatLong;
import gmapsfx.javascript.object.LatLongBounds;
import gmapsfx.javascript.object.MVCArray;
import gmapsfx.shapes.Polyline;
import javafx.scene.control.Button;

public class RouteService {
	private GoogleMap map;

    // static variable
    private MarkerManager markerManager;
    private Polyline routeLine;
    private RouteVisualization rv;

	public RouteService(GoogleMapView mapComponent, MarkerManager manager) {
		this.map = mapComponent.getMap();
        this.markerManager = manager;

	}

    // COULD SEPARATE INTO ROUTE SERVICES IF CONTROLLER
	// GETS BIG
	// initialize??

	// add route polyline to map
	//DISPLAY ROUTE METHODS
	/**
	 * Displays route on Google Map
	 * @return returns false if route fails to display
	 */
	private boolean displayRoute(List<LatLong> route) {

        if(routeLine != null) {
        	removeRouteLine();
        }

		routeLine = new Polyline();
		MVCArray path = new MVCArray();
		LatLongBounds bounds = new LatLongBounds();

		for(LatLong point : route)  {
			path.push(point);
            bounds = bounds.extend(point);
		}

		routeLine.setPath(path);
		map.addMapShape(routeLine);
		markerManager.hideIntermediateMarkers();
		map.fitBounds(bounds);
    	markerManager.disableVisButton(false);

		return true;
	}

    public void hideRoute() {
    	if(routeLine != null) {
        	map.removeMapShape(routeLine);
        	if(markerManager.getVisualization() != null) {
        		markerManager.clearVisualization();
        	}
            markerManager.restoreMarkers();
        	markerManager.disableVisButton(true);
            routeLine = null;
    	}
    }

    public void reset() {
        removeRouteLine();
    }

    public boolean isRouteDisplayed() {
    	return routeLine != null;
    }

    public boolean displayRoute(geography.GeographicPoint start, geography.GeographicPoint end, int toggle) {
        if(routeLine == null) {
        	if(markerManager.getVisualization() != null) {
        		markerManager.clearVisualization();
        	}

        	if(toggle == RouteController.DIJ || toggle == RouteController.A_STAR ||
        			toggle == RouteController.BFS || toggle == RouteController.TSP) {
        		markerManager.initVisualization();
            	Consumer<geography.GeographicPoint> nodeAccepter = markerManager.getVisualization()::acceptPoint;
            	List<geography.GeographicPoint> path = null;

				final long startTime = System.nanoTime();

            	switch (toggle) {
					case RouteController.BFS:
						path = markerManager.getDataSet().getGraph().bfs(start, end, nodeAccepter);
						break;
					case RouteController.DIJ:
						path = markerManager.getDataSet().getGraph().dijkstra(start, end, nodeAccepter);
						break;
					case RouteController.A_STAR:
						path = markerManager.getDataSet().getGraph().aStarSearch(start, end, nodeAccepter);
						break;
					case RouteController.TSP:
						path = markerManager.getDataSet().getGraph().tsp(start, end, nodeAccepter);
						break;
				}

				final long duration = System.nanoTime() - startTime;
				long durationInMillis = TimeUnit.NANOSECONDS.toSeconds(duration);

				MapApp.showInfoAlert("Time", "Execution time is " + durationInMillis + "(sec)");

            	if(path == null) {
                    MapApp.showInfoAlert("Routing Error : ", "No path found");
                	return false;
                }

                markerManager.setSelectMode(false);
                return displayRoute(constructMapPath(path));
    		}

    		return false;
        }

        return false;
    }

    /**
     * Construct path including road regments
     * @param path - path with only intersections
     * @return list of LatLongs corresponding the path of route
     */
    private List<LatLong> constructMapPath(List<geography.GeographicPoint> path) {
    	List<LatLong> retVal = new ArrayList<LatLong>();
        List<geography.GeographicPoint> segmentList = null;
    	geography.GeographicPoint curr;
    	geography.GeographicPoint next;

    	geography.RoadSegment chosenSegment = null;;

        for(int i = 0; i < path.size() - 1; i++) {
            double minLength = Double.MAX_VALUE;
        	curr = path.get(i);
        	next = path.get(i+1);

        	if(markerManager.getDataSet().getRoads().containsKey(curr)) {
        		HashSet<geography.RoadSegment> segments = markerManager.getDataSet().getRoads().get(curr);
        		Iterator<geography.RoadSegment> it = segments.iterator();

        		// get segments which are
            	geography.RoadSegment currSegment;
                while(it.hasNext()) {
                	currSegment = it.next();
                	if(currSegment.getOtherPoint(curr).equals(next)) {
                		if(currSegment.getLength() < minLength) {
                			chosenSegment = currSegment;
                		}
                	}
                }

                if(chosenSegment != null) {
                    segmentList = chosenSegment.getPoints(curr, next);
                    for(geography.GeographicPoint point : segmentList) {
                        retVal.add(new LatLong(point.getX(), point.getY()));
                    }
                }
                else {
                	System.err.println("ERROR in constructMapPath : chosenSegment was null");
                }
        	}
        }

    	return retVal;
    }

	private void removeRouteLine() {
        if(routeLine != null) {
    		map.removeMapShape(routeLine);
        }
	}
}
