package org.lovey.utils;

import java.io.File;
import java.util.List;

/**
 * @author l_y
 * @version 1.0.0
 * @ClassName FileUtils.java
 * @createTime 2022年01月07日 17:42:00
 */

public class FileUtils {

    /**
     * @param pathFile   文件路径 null
     * @param folder     文件最上层目录
     * @param lastSuffix 最后后缀
     * @return java.util.List<java.lang.String>
     * @author l_y
     * @date 2022/1/7
     */
    public static List<String> getDirInFile(List<String> pathFile, String folder, String lastSuffix) {
        File file = new File(folder);
        if (!file.isDirectory() && file.getName().length() - 4 == file.getName().lastIndexOf(lastSuffix)) {
            pathFile.add(file.getAbsolutePath());
        }
        if (file.isDirectory()) {
            if (file.listFiles().length > 0) {
                for (File fileList : file.listFiles()) {
                    getDirInFile(pathFile, fileList.getPath(), lastSuffix);
                }
            }
        }
        return pathFile;
    }
}
