package config.packages;

import config.Package;
import config.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestProcessor {
    public List<String> processConfig(Config config) {
        List<String> requests = new ArrayList<>();

        for (Package pack : config.getSystem_type_a().getPackages()) {
            if (pack.getMethod().equals(Method.POST)) {
                // Собираем JSON запрос из requestBody
                JSONObject jsonRequest = createJson(pack.getRequest_body());
                requests.add(jsonRequest.toString());
            } else if (pack.getMethod().equals(Method.GET)) {
                StringBuilder urlBuilder = new StringBuilder(pack.getUrl());
                if (pack.getRequest_params().isEmpty()) {
                    // Собираем URL из pathVariable
                    urlBuilder.append("/").append(pack.getPath_variable().getValue());

                } else {
                    urlBuilder.append("?");
                    // Собираем URL из requestParams
                    for (Parameter param : pack.getRequest_params()) {
                        if (!urlBuilder.isEmpty() && urlBuilder.charAt(urlBuilder.length() - 1) != '?') {
                            urlBuilder.append("&");
                        }
                        urlBuilder.append(param.getName()).append("=").append(param.getValue());

                    }
                }
                // пробел заменить на %20
                requests.add(urlBuilder.toString().replaceAll(" ", "%20"));
            }
        }
        return requests;
    }
    public static JSONObject createJson(List<Parameter> params) {
        JSONObject jsonObject = new JSONObject();

        for (Parameter param : params) {
            if (param.getType_param().equals(ParameterType.OBJECT)) {
                JSONObject nestedObject = createJson(param.getParams());
                System.out.println(nestedObject);
                jsonObject.put(param.getName(), nestedObject);
            } else {
                jsonObject.put(param.getName(), param.getValue());

            }
        }

        return jsonObject;
    }
}
