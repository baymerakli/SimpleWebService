package com.ozg.ws;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import javax.jws.WebService;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ozg.client.dao.UserDAO;
import com.ozg.client.dao.UserDAOImpl;
import com.ozg.client.model.User;
import com.ozg.ws.util.PropertyReader;

@WebService(endpointInterface = "com.ozg.ws.UserService")
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyReader.class);
    private static int fileIncrementor = 1;
    private ObjectMapper objectMapper = new ObjectMapper();
    private UserDAO userDAO = new UserDAOImpl();
    private Properties properties;
    private String batchFileName;
    private String batchFilePath;
    private String batchFileType;

    @Override
    public String getUsers() {
	try {
	    List<User> users = userDAO.getUsers();
	    return populateJsonArray(users);
	} catch (IOException e) {
	    LOGGER.error("User listing failed.",e);
	}
	return "";
    }

    public String populateJsonArray(List<User> list) throws IOException {

	OutputStream out = new ByteArrayOutputStream();

	JsonFactory jfactory = new JsonFactory();
	JsonGenerator jGenerator = jfactory.createJsonGenerator(out, JsonEncoding.UTF8);
	ObjectMapper mapper = new ObjectMapper();
	jGenerator.writeStartArray();
	int i = 0;
	for (User event : list) {
	    i++;
	    String usage = mapper.writeValueAsString(event);
	    jGenerator.writeRaw(usage);
	    if (list.size() > i) {
		jGenerator.writeRaw(",");
	    }
	}

	jGenerator.writeEndArray();

	jGenerator.close();
	return out.toString();
    }

    @Override
    public boolean addUser(String userJSON) {
	if (this.properties == null) {
	    try {
		loadConfiguration();
	    } catch (Exception e) {
		LOGGER.error("Add User failed.",e);
	    }
	}
	try {
	    User user = null;
	    user = objectMapper.readValue(userJSON, User.class);
	    File batchFile = getBatchFile();
	    objectMapper.writeValue(batchFile, user);
	} catch (JsonGenerationException e) {
	    LOGGER.error("Batch file generation failed.",e);
	    return false;
	} catch (JsonMappingException e) {
	    LOGGER.error("Batch file generation failed.",e);
	    return false;
	} catch (IOException e) {
	    LOGGER.error("Batch file generation failed.",e);
	    return false;
	}
	return true;
    }

    private void loadConfiguration() throws Exception {
	batchFileName = PropertyReader.getProperty("batch.filename", "user");
	batchFileType = PropertyReader.getProperty("batch.filetype", "json");
	batchFilePath = PropertyReader.getProperty("batch.filepath", "c:/mobidev/");
    }
    
    private File getBatchFile() throws IOException{
	    String filePath = batchFilePath + batchFileName + fileIncrementor + "." + batchFileType;
	    fileIncrementor++;
	    File dir = new File(batchFilePath);
	    if (!dir.exists()) {
		try {
		    dir.mkdir();
		} catch (SecurityException e) {
		    LOGGER.error("Batch folder generation failed.",e);
		}
	    }
	    File file = new File(filePath);
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    return file;
    }

}