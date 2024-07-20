package client;

import config.Authorization;
import config.Config;
import config.SystemConfig;
import org.json.JSONObject;

public class JsonRequestBuilder {
    public String buildMultiLevelJsonRequest(Config config) {
        JSONObject jsonRequest = new JSONObject();

        jsonRequest.put("logging", config.isLogging());
        jsonRequest.put("log_path", config.getLog_path());

        JSONObject systemTypeA = buildSystemJson(config.getSystem_type_a());
        jsonRequest.put("system_type_a", systemTypeA);

        JSONObject systemTypeB = buildSystemJson(config.getSystem_type_b());
        jsonRequest.put("system_type_b", systemTypeB);

        return jsonRequest.toString();
    }

    private JSONObject buildSystemJson(SystemConfig system) {
        JSONObject systemJson = new JSONObject();
        systemJson.put("timeout", system.getTimeout());
        systemJson.put("domain", system.getDomain());
        systemJson.put("use_auth", system.isUse_auth());

        JSONObject authorizationJson = buildAuthorizationJson(system.getAuthorization());
        systemJson.put("authorization", authorizationJson);

        // Добавление других полей для System

        return systemJson;
    }

    private JSONObject buildAuthorizationJson(Authorization authorization) {
        JSONObject authorizationJson = new JSONObject();
        authorizationJson.put("type", authorization.getType());
        authorizationJson.put("need_loging", authorization.isNeed_loging());
        authorizationJson.put("method", authorization.getMethod());

        // Добавление данных для params
        // Рекурсивный вызов для вложенных параметров, если необходимо

        // Добавление других полей для Authorization

        return authorizationJson;
    }
}
