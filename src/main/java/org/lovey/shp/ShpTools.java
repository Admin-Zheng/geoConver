package org.lovey.shp;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author l_y
 * @version 1.0.0
 * @ClassName ShpTools.java
 * @createTime 2022年01月07日 17:39:00
 */

public class ShpTools {

    /**
     * 读SHP文件，
     *
     * @param shpPath shp文件路径
     * @param enCode  UTF-8 / GBK
     * @return List<Map < String, String>>（feature集合）
     */
    public static List<Geometry> reaferShp(String shpPath, String enCode) {
        ArrayList<Geometry> geometries = new ArrayList<>();
        File shpFile = new File(shpPath);
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        try {
            ShapefileDataStore sds = (ShapefileDataStore) dataStoreFactory.createDataStore(shpFile.toURI().toURL());
            sds.setCharset(Charset.forName(enCode));
            SimpleFeatureSource featureSource = sds.getFeatureSource();
            SimpleFeatureIterator itertor = featureSource.getFeatures().features();
            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                geometries.add(geometry);
            }
            itertor.close();
            return geometries;
        } catch (Exception ex) {
            return geometries;
        }
    }
}
