package com.autodesk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dt on 9/6/15.
 */
public class PropertyReader {

    /**
     *
     * @param propFileName
     * @return
     */
    public static Map<Object, Object> readAll(String propFileName) {
        Map<Object, Object> propMap = new HashMap<Object, Object>();
        InputStream inputStream = PropertyReader.class.getClassLoader().getResourceAsStream(propFileName);
        Properties prop = new Properties();
        if (inputStream != null) {
            try {
                prop.load(inputStream);
                propMap.putAll(prop);

            } catch (IOException e) {
                throw new UtilException("unable to load " + propFileName + " reason " + e.getMessage());
            }
        } else {
            throw new UtilException("property file '" + propFileName + "' not found in the classpath");
        }

        return propMap;

    }
}
