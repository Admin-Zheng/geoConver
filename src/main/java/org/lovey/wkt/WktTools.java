package org.lovey.wkt;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @author l_y
 * @version 1.0.0
 * @ClassName WktTools.java
 * @createTime 2022年01月07日 17:47:00
 */

public class WktTools {

    /**
     * @param pointList
     * @return java.util.List<com.vividsolutions.jts.geom.Geometry>
     * @author l_y
     * @date 2022/1/7
     */
    public static List<Geometry> makePointByJson(List<String> pointList) {
        List<Geometry> geometries = new ArrayList<>();
        WKTReader reader = new WKTReader();
        pointList.forEach(pointWkt -> {
            Geometry read = null;
            try {
                if (null == pointWkt || "".equals(pointWkt)) {
                    return;
                }
                read = reader.read(pointWkt);
                geometries.add(read);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return geometries;
    }


}
