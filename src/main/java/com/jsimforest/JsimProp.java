package com.jsimforest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class JsimProp {

    private final String resource;

    public JsimProp(String resource){
        this.resource = resource;
    }

    // This method is used to load the properties file
    public String loadPropertiesFile(String param){

        Properties properties = new Properties();

        //Properties properties = new Properties();
        InputStream iStream = null;
        try {
            // Loading properties file from the classpath
            iStream = this.getClass().getClassLoader()
                    .getResourceAsStream(this.resource);
            if(iStream == null){
                throw new IOException("File not found");
            }
            properties.load(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(iStream != null){
                    iStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties.getProperty(param);
    }
}