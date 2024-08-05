package config.packages;

import config.json.Package;
import config.json.Config;
import config.json.Parameter;
import config.json.SystemConfig;
import config.types.Method;
import config.types.ParameterType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestProcessor {
    public List<String> processConfig(@NotNull Config config) {
        List<String> list = new ArrayList<>();
        list.addAll(createUrl(config.getSystemTypeA()));
//        list.addAll(createUrl(config.getSystemTypeB()));
        return list;
    }

    public static @NotNull List<String> createUrl(@NotNull SystemConfig systemConfig) {
        List<String> requests = new ArrayList<>();

        for (Package pack : systemConfig.getPackages()) {
            StringBuilder urlBuilder = new StringBuilder(pack.getUrl());
            if (pack.getMethod().equals(Method.POST)) {
                // Собираем JSON запрос из requestBody
                JSONObject jsonRequest = createJson(pack.getRequestBody());
                urlBuilder.insert(0, "POST:");
                urlBuilder.append(": " +jsonRequest.toString());
                requests.add(urlBuilder.toString());
            } else if (pack.getMethod().equals(Method.GET)) {
                urlBuilder.insert(0, "GET:");

                if (pack.getRequestParams().isEmpty()) {
                    // Собираем URL из pathVariable
                    urlBuilder.append("/").append(pack.getPathVariable().getValue());

                } else {
                    urlBuilder.append("?");
                    // Собираем URL из requestParams
                    for (Parameter param : pack.getRequestParams()) {
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

    public static @NotNull JSONObject createJson(@NotNull List<Parameter> params) {
        JSONObject jsonObject = new JSONObject();

        for (Parameter param : params) {
            if (param.getTypeParam().equals(ParameterType.OBJECT)) {
                JSONObject nestedObject = createJson(param.getParams());
                jsonObject.put(param.getName(), nestedObject);
            } else {
                if (param.getValue() != null)
                    jsonObject.put(param.getName(), param.getValue());
                else
                    jsonObject.put(param.getName(), "{value} Type: " + param.getTypeParam());
            }
        }
        return jsonObject;
    }
}
