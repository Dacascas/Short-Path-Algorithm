package roadgraph;

import java.util.HashMap;

public class SpeedRoadType {
    private HashMap<String, Integer> speedType;
    private Integer defaulSpeed = 20;

    public SpeedRoadType()
    {
        this.speedType = new HashMap<String, Integer>();
        this.speedType.put("unclassified", 50);
        this.speedType.put("residential", 20);
        this.speedType.put("tertiary", 50);
        this.speedType.put("living_street", 50);
        this.speedType.put("motorway_link", 130);
        this.speedType.put("motorway", 130);
        this.speedType.put("secondary", 50);
        this.speedType.put("secondary_link", 50);
        this.speedType.put("primary", 60);
        this.speedType.put("trunk_link", 60);
        this.speedType.put("trunk", 60);
    }

    /**
     * @param roadType
     * @return Integer
     */
    public Integer getSpeedValue(String roadType) {
        if (this.speedType.containsKey(roadType)) {
            return this.speedType.get(roadType);
        }

        return this.defaulSpeed;
    }
}
