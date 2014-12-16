package com.ozg.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReader {

    private static PropertyReader instance;
    private static Properties properties;
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReader.class);

    public static String getProperty(String property) throws Exception {
	String result = getProperties().getProperty(property);
	if (result == null) {
	    throw new Exception("Property not found: " + property);
	}
	result = result.trim();
	if (result.length() == 0) {
	    throw new Exception("Invalid value for property: " + property);
	}
	LOGGER.info("Property: " + property + ". Value: " + result);
	return result;
    }

    public static String getProperty(String property, String defaultValue) throws Exception {
	String result = getProperties().getProperty(property);
	if (result == null) {
	    LOGGER.warn("Property not found: " + property + ". Using default value: " + defaultValue);
	    return defaultValue;
	}
	result = result.trim();
	if (result.length() == 0) {
	    throw new Exception("Invalid value for property: " + property);
	}
	LOGGER.info("Property: " + property + ". Value: " + result);
	return result;
    }

    private static PropertyReader getInstance() {
	if (instance == null) {
	    instance = new PropertyReader();
	}
	return instance;
    }

    private static Properties getProperties() {
	if (properties == null) {
	    InputStream inputStream = PropertyReader.getInstance().getClass().getClassLoader().getResourceAsStream("service.properties");
	    properties = new Properties();
	    try {
		properties.load(inputStream);
	    } catch (IOException e) {
		LOGGER.error("Properties load failed.", e);
	    }
	}
	return properties;
    }

}
