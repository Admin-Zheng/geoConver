package org.lovey.geojson;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.lovey.conver.ConverUtils;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author l_y
 * @version 1.0.0
 * @ClassName GeojsonTools.java
 * @createTime 2022年01月07日 17:47:00
 */
public class GeojsonTools {


    /**
     * @param jsonFile jsonfile
     * @param enCode   utf-8 / gbk
     * @return java.util.List<java.lang.String>
     * @author l_y
     * @date 2022/1/7
     */
    public static List<String> checkGeoJsonPoint(File jsonFile, String enCode) {
        String jsonStr = "";
        ArrayList<String> point = new ArrayList<>();
        try (Reader reader = new InputStreamReader(new FileInputStream(jsonFile), enCode)) {
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            jsonStr = sb.toString();
            Map<String, Object> outMap = (Map<String, Object>) JSONObject.parse(jsonStr);
            List<Map<String, Object>> inMap = (List<Map<String, Object>>) outMap.get("features");
            inMap.forEach(in -> {
                Map<String, Object> inTwoMap = (Map<String, Object>) in.get("geometry");
                List<List<List<BigDecimal>>> geoList = (List<List<List<BigDecimal>>>) inTwoMap.get("coordinates");
                geoList.forEach(geo ->
                        geo.parallelStream().forEach(b ->
                                point.add("POINT (" + b.get(0) + " " + b.get(1) + ")")
                        )
                );
            });
            reader.close();
            return point;
        } catch (Exception e) {
            return point;
        }
    }


    /**
     * @param source
     * @param tag
     * @param shpPath
     * @param downFile
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     * @author l_y
     * @date 2022/1/12
     */
    public boolean reaferShp(String source, String tag, File shpPath, String downFile) {
        List<Map<String, String>> list = new ArrayList<>();
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(shpPath.toURI().toURL());
            sds.setCharset(Charset.forName("UTF-8"));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();
            while (itertor.hasNext()) {
                Map<String, String> map = new HashMap<>();
                SimpleFeature feature = itertor.next();
                Iterator<Property> iterator = feature.getProperties().iterator();
                //读取属性
                GeometryAttribute geometryProperty = feature.getDefaultGeometryProperty();
                String wkt = geometryProperty.getDescriptor().getCoordinateReferenceSystem().toWKT();

                while (iterator.hasNext()) {
                    Property pro = iterator.next();
                    if (!"the_geom".equals(pro.getName().toString())) {
                        if (!pro.getName().toString().contains("desc")
                                && !pro.getName().toString().contains("O_Comment")
                                && !pro.getName().toString().contains("remarks")
                                && !pro.getName().toString().contains("PopupInfo")) {
                            map.put(pro.getName().toString(), Optional.ofNullable(pro.getValue()).orElse("").toString());
                        }
                    } else {
                        WKTReader wktReader = new WKTReader();
                        Geometry conver = ConverUtils.conver(source, tag, wktReader.read(pro.getValue().toString()), wkt);
                        List<String> geometryType = ConverUtils.judgeGeometry(conver.toString());
                        String geoStr = ConverUtils.buildGeoStr(conver, geometryType.get(0), geometryType.get(1));
//                        map.put(pro.getName().toString(), geoStr);
                        System.out.println(geoStr);
                    }
                }
                list.add(map);
            }
            itertor.close();
            sds.dispose();
//            writeToGeojson(list, downFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * @param list
     * @param geoJsonPath
     * @return void
     * @author l_y
     * @date 2022/1/12
     */
    private void writeToGeojson(List<Map<String, String>> list, String geoJsonPath) throws IOException {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        FileWriter fileWriter = new FileWriter(geoJsonPath, true);//".json"
        fileWriter.append("{\"type\":\"FeatureCollection\",\"features\":[");
        for (int i = 0; i < list.size(); i++) {
            Map<String, String> item = list.get(i);
            fileWriter.append("{\"type\":\"Feature\",\"properties\":{");
            String properties = "";
            for (String key : item.keySet()) {
                if (!"the_geom".equals(key)) {
                    properties += "\"" + key + "\":\"" + item.get(key) + "\",";
                }
            }
            fileWriter.append(properties.subSequence(0, properties.length() - 1));
            fileWriter.append("},");
            Map<String, String> map = new GeoJsonStringService().parseCoordinate(list.get(i).get("the_geom"));
            String geometryType = map.get("type");
            fileWriter.append("\"geometry\":{\"type\":\"" + geometryType + "\",\"coordinates\":");
            String coordinate = map.get("value");
            fileWriter.append(coordinate);
            if (i == list.size() - 1) {
                fileWriter.append("}}");
            } else {
                fileWriter.append("}},");
            }
            fileWriter.flush();
        }
        fileWriter.append("]}");
        fileWriter.flush();
        fileWriter.close();
    }


}
