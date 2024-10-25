package org.example.config.packages;

import org.example.config.json.Package;
import org.example.config.json.Parameter;
import org.example.config.types.ParameterType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.List;


public class RequestProcessor {

    public static @NotNull String createUrl(@NotNull Package pack) {
        String requests = "";
        StringBuilder urlBuilder = new StringBuilder(pack.getUrl());
        if (pack.getPathVariable().getTypeParam()!=null) {
            // Собираем URL из pathVariable
            if (pack.getPathVariable().getValueTMP() == null)
                urlBuilder.append("/").append(pack.getPathVariable().getValue());
            else
                urlBuilder.append("/").append(pack.getPathVariable().getValueTMP());
        }
        if (!pack.getRequestParams().isEmpty()) {
            urlBuilder.append("?");
            // Собираем URL из requestParams
            for (Parameter param : pack.getRequestParams()) {
                if (!urlBuilder.isEmpty() && urlBuilder.charAt(urlBuilder.length() - 1) != '?')
                    urlBuilder.append("&");
                if (param.getValueTMP() == null)
                    urlBuilder.append(param.getName()).append("=").append(param.getValue());
                else
                    urlBuilder.append(param.getName()).append("=").append(param.getValueTMP());
            }
        }

        requests = formatting(urlBuilder.toString());

        return requests;
    }

    public static @NotNull String assemblyUrl(@NotNull Package pack, JSONObject jsonObject) {
        StringBuilder urlBuilder = new StringBuilder(pack.getUrl());
        if (!pack.getRequestParams().isEmpty()) {
            urlBuilder.append("?");
            for (Parameter param : pack.getRequestParams()) {
                if (!urlBuilder.isEmpty() && urlBuilder.charAt(urlBuilder.length() - 1) != '?') urlBuilder.append("&");
                urlBuilder.append(param.getName()).append("=").append(jsonObject.get(param.getName()));
            }
        }
        return formatting(urlBuilder.toString());
    }

    private static String formatting(String input) {
        return input.replaceAll(" ", "%20").replaceAll(":", "%3A");
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
