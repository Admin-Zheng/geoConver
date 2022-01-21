package org.lovey.conver;


import org.apache.commons.lang3.StringUtils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author l_y
 * @version 1.0.0
 * @ClassName ConverUtils.java
 * @createTime 2022年01月17日 15:57:00
 */

public class ConverUtils {

    /**
     * @param source
     * @param tag
     * @param geometry
     * @param wktConver
     * @return org.locationtech.jts.geom.Geometry
     * @author l_y
     * @date 2022/1/17
     */
    public static Geometry conver(String source, String tag, Geometry geometry, String wktConver) throws FactoryException, TransformException {
        CoordinateReferenceSystem mercatroCRS = null;
        CoordinateReferenceSystem crsTarget = null;
        if (StringUtils.isEmpty(source) && StringUtils.isEmpty(tag)) {
            if (wktConver.contains("CGCS2000")) {
                mercatroCRS = CRS.parseWKT(wktConver);
                crsTarget = CRS.decode("EPSG:4490");
            } else if (wktConver.contains("WGS84") || wktConver.contains("WGS_1984")) {
                mercatroCRS = CRS.parseWKT(wktConver);
                crsTarget = CRS.decode("EPSG:4326");
            } else {
                mercatroCRS = CRS.parseWKT(wktConver);
                crsTarget = CRS.decode("EPSG:4326");
            }
        } else if (StringUtils.isEmpty(source) && !StringUtils.isEmpty(tag)) {
            mercatroCRS = CRS.parseWKT(wktConver);
            crsTarget = CRS.decode("EPSG:" + tag);
        } else {
            mercatroCRS = CRS.decode("EPSG:" + source);
            crsTarget = CRS.decode("EPSG:" + tag);
        }
        // 投影转换
        MathTransform transform = CRS.findMathTransform(mercatroCRS, crsTarget);
        return JTS.transform(geometry, transform);
    }

    /**
     * @param geometry
     * @param hand
     * @param foot
     * @return java.lang.String
     * @author l_y
     * @date 2022/1/17
     */
    public static String buildGeoStr(Geometry geometry, String hand, String foot) {
        String geometryStr = geometry.toString();
        String[] split = geometryStr.substring(geometryStr.lastIndexOf("(") + 1, geometryStr.length() - foot.length()).trim().split(",");
        int len = 0;
        StringBuilder builder = new StringBuilder(hand);
        for (String polygon : split) {
            len++;
            if (polygon.indexOf(" ") == 0) {
                polygon = polygon.substring(1);
            }
            String[] point = polygon.split(" ");
            if (point.length <= 0) {
                continue;
            }
            if (Double.parseDouble(point[0].trim()) > Double.parseDouble(point[1].trim())) {
                builder.append(point[0].trim() + " " + point[1].trim());
            } else {
                builder.append(point[1].trim() + " " + point[0].trim());
            }
            if (len < split.length) {
                builder.append(",");
            } else {
                builder.append(foot);
            }
        }
        return builder.toString();
    }

    /**
     * @param geometry
     * @return java.util.List<java.lang.String>
     * @author l_y
     * @date 2022/1/17
     */
    public static List<String> judgeGeometry(String geometry) {
        if (geometry.contains("MULTIPOLYGON")) {
            return Arrays.asList("MULTIPOLYGON (((", ")))");
        }
        if (geometry.contains("POLYGON")) {
            return Arrays.asList("POLYGON (((", ")))");
        }
        if (geometry.contains("MULTILINESTRING")) {
            return Arrays.asList("MULTILINESTRING ((", "))");
        }
        if (geometry.contains("LINESTRING")) {
            return Arrays.asList("LINESTRING ((", "))");
        }
        if (geometry.contains("MULTIPOINT")) {
            return Arrays.asList("MULTIPOINT (", ")");
        }
        if (geometry.contains("POINT")) {
            return Arrays.asList("POINT (", ")");
        }
        return Collections.EMPTY_LIST;
    }


}
