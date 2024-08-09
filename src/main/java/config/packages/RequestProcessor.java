package config.packages;

import config.json.Package;
import config.json.Parameter;
import config.types.Method;
import config.types.ParameterType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestProcessor {

    public static @NotNull String createUrl(@NotNull Package pack) {
        String requests = "";
        StringBuilder urlBuilder = new StringBuilder(pack.getUrl());
        if (pack.getMethod().equals(Method.POST)) {
            // Собираем JSON запрос из requestBody
            JSONObject jsonRequest = createJson(pack.getRequestBody());
            urlBuilder.append(": ").append(jsonRequest.toString());
            requests = (urlBuilder.toString());
        } else if (pack.getMethod().equals(Method.GET)) {
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
            requests = (urlBuilder.toString().replaceAll(" ", "%20").replaceAll(":", "%3A"));
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
