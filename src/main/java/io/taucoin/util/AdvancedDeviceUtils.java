package io.taucoin.util;

import io.taucoin.Start;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Roman Mandeleil
 * @since 25.07.2014
 */
public class AdvancedDeviceUtils {

    public static void configureDetailedTracing() {
        URL configFile = ClassLoader.getSystemResource("log4j.properties");
        PropertyConfigurator.configure(configFile);
//        BasicConfigurator.configure();

//        Properties props = new Properties();
//        try {
//            props.load(AdvancedDeviceUtils.class.getClassLoader().
//                    getResourceAsStream("log4j.properties"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        PropertyConfigurator.configure(props);
    }
}
