package org.lovey.geojson;

import java.util.HashMap;
import java.util.Map;

/**
 * @author l_y
 * @version 1.0.0
 * @ClassName GeoJsonStringService.java
 * @createTime 2022年01月12日 10:59:00
 */

public class GeoJsonStringService {

    /**
     * @param polygon
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author l_y
     * @date 2022/1/12
     */
    Map<String, String> parseCoordinate(String polygon) {
        Map<String, String> param = new HashMap<>();
        String value = "";
        if (polygon.indexOf("(((") > 0 && polygon.lastIndexOf(")))") > 0) {//面
            int beginIndex = polygon.indexOf("(((") + 3;
            int endIndex = polygon.lastIndexOf(")))");
            String s = polygon.substring(beginIndex, endIndex).replace('(', ' ').replace(')', ' ');
            value = geojsonWrapper(s);
            param.put("type", "Polygon");
            param.put("value", value);
        } else if (polygon.indexOf("((") > 0 && polygon.lastIndexOf("))") > 0 && polygon.indexOf("(((") == -1) {//线
            int beginIndex = polygon.indexOf("((") + 2;
            int endIndex = polygon.lastIndexOf("))");
            String s = polygon.substring(beginIndex, endIndex).replace('(', ' ').replace(')', ' ');
            value = geojsonWrapperLine(s);
            param.put("type", "LineString");
            param.put("value", value);
        } else if (polygon.indexOf("(") > 0 && polygon.lastIndexOf(")") > 0 && polygon.indexOf("((") == -1) {//点
            int beginIndex = polygon.indexOf("(") + 1;
            int endIndex = polygon.lastIndexOf(")");
            String s = polygon.substring(beginIndex, endIndex).replace('(', ' ').replace(')', ' ');
            value = geojsonWrapperPoint(s);
            param.put("type", "Point");
            param.put("value", value);
        }
        return param;
    }

    //点
    private String geojsonWrapperPoint(String point) {
        StringBuilder sber = new StringBuilder(point.length() + 30);
        String tmp[] = point.split(",");
        sber.append("[");
        for (int i = 0; i < tmp.length; i++) {
            sber.append(tmp[i].trim().replace(" ", ","));
            if (i != tmp.length - 1)
                sber.append(",");
        }
        sber.append("]");
        return sber.toString();
    }

    //线
    private String geojsonWrapperLine(String polygon) {
        StringBuilder sber = new StringBuilder(polygon.length() + 30);
        String tmp[] = polygon.split(",");
        sber.append("[");
        for (int i = 0; i < tmp.length; i++) {
            sber.append("[");
            sber.append(tmp[i].trim().replace(" ", ","));
            sber.append("]");
            if (i != tmp.length - 1)
                sber.append(",");
        }
        sber.append("]");
        return sber.toString();
    }

    //面
    private String geojsonWrapper(String polygon) {
        StringBuilder sber = new StringBuilder(polygon.length() + 30);
        String tmp[] = polygon.split(",");
        sber.append("[[");
        for (int i = 0; i < tmp.length; i++) {
            sber.append("[");
            sber.append(tmp[i].trim().replace(" ", ","));
            sber.append("]");
            if (i != tmp.length - 1)
                sber.append(",");
        }
        sber.append("]]");
        return sber.toString();
    }


}
