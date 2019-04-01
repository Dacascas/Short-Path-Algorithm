package roadgraph;

import geography.GeographicPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CacheResult
{
    private HashMap<String, List> cachedResult;

    public CacheResult() {
        this.cachedResult = new HashMap<>();
    }

    public void setCache(String key, List result)
    {
        this.cachedResult.put(key, result);
    }

    public List<GeographicPoint> getCache(String key)
    {
        if (this.cachedResult.containsKey(key)) {
            return (List<GeographicPoint>) this.cachedResult.get(key);
        }
        return new ArrayList<GeographicPoint>();
    }

    protected String generateKey(String $type, GeographicPoint start, GeographicPoint end)
    {
        return $type.concat(start.toString()).concat(end.toString());
    }
}
