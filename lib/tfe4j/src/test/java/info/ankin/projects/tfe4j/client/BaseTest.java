package info.ankin.projects.tfe4j.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

class BaseTest {

    protected static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().findAndAddModules().build();
    protected static final String ORG_TOKEN = "";
    protected static final String USER_TOKEN = "";

}
