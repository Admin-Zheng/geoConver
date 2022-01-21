package org.lovey.core;

import org.locationtech.jts.geom.Geometry;
import org.lovey.geojson.GeojsonTools;
import org.lovey.shp.ShpTools;
import org.lovey.utils.FileUtils;
import org.lovey.wkt.WktTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author l_y
 * @version 1.0.0
 * @ClassName ShpService.java
 * @createTime 2022年01月07日 17:58:00
 */
public class ShpService {


    /**
     * 比较geojson 和 shp文件
     *
     * @param geoJson       geoJson
     * @param shpFolder     shpFolder
     * @param geoJsonEcCode geoJsonEcCode
     * @param shpEnCode     shpEnCode
     * @return boolean
     * @author l_y
     * @date 2022/1/12
     */
    public boolean compareGeoJsonOrShp(File geoJson, String shpFolder, String geoJsonEcCode, String shpEnCode) {
        if (null == geoJson || shpFolder.length() <= 0 || geoJsonEcCode.length() <= 0 || shpEnCode.length() <= 0) {
            return false;
        }
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        List<String> filePath = new ArrayList<>();
        List<String> shpFile = FileUtils.getDirInFile(filePath, shpFolder, ".shp");
        List<String> geoJsonStr = GeojsonTools.checkGeoJsonPoint(geoJson, geoJsonEcCode);
        shpFile.forEach(shp -> {
            List<Geometry> geometries = ShpTools.reaferShp(shp, shpEnCode);
            List<Geometry> point = WktTools.makePointByJson(geoJsonStr);
            geometries.forEach(geo ->
                    point.forEach(pointInput -> {
                        try {
                            boolean contains = geo.intersects(pointInput);
                            if (contains) {
                                atomicBoolean.set(contains);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    })
            );
        });
        return atomicBoolean.get();
    }

    /**
     * 将shp转化为 geojson文件
     *
     *
     * @param source
     * @param tag
     * @param shpFile         shpFile
     * @param geoJsonSavePath geoJsonSavePath
     * @return String
     * @author l_y
     * @date 2022/1/12
     */
    public String converShpFileToGeoJson(String source, String tag, File shpFile, String geoJsonSavePath) {
        GeojsonTools tools = new GeojsonTools();
        boolean bool = tools.reaferShp(source,tag,shpFile, geoJsonSavePath);
        if (bool) {
            return geoJsonSavePath;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        List<String> fileList = new ArrayList<>();
        List<String> dirInFile = FileUtils.getDirInFile(fileList, "C:\\Users\\SupperZheng\\Documents\\WXWorkLocal\\1688849887737674_1970325135052173\\Cache\\File\\2022-01\\楚雄-大姚-叭腊么风电场\\楚雄-大姚-叭腊么风电场", ".shp");
        dirInFile.forEach(file -> {
            try {
                    new ShpService().converShpFileToGeoJson("3857","4326", new File(file), file.substring(0, file.lastIndexOf(".shp")) + ".geojson");
            } catch (Exception x) {
                x.printStackTrace();
            }
        });
    }
}
