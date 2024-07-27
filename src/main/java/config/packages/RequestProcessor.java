package config.packages;

import config.Config;
import config.Method;
import config.Package;
import config.Parameter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestProcessor {
    public List<String> processConfig(@NotNull Config config) {
        List<String> requests = new ArrayList<>();

        for (Package pack : config.getSystem_type_a().getPackages()) {
            if (pack.getMethod().equals(Method.POST)) {
                // Собираем JSON запрос из requestBody
                JSONObject jsonRequest = new JSONObject();
                for (Parameter param : pack.getRequest_body()) {
                    // object = вложенный json
                    jsonRequest.append(param.getName(), param.getType_param());
                }
                requests.add(jsonRequest.toString());
            } else if (pack.getMethod().equals(Method.GET)) {
                StringBuilder urlBuilder = new StringBuilder(pack.getUrl());
                if (pack.getRequest_params().isEmpty()) {
                    // Собираем URL из pathVariable
                    // переделать (он должен брать только value из path_variable, а не все поля)
                    for (Map.Entry<String, Object> entry : pack.getPath_variable().entrySet()) {
                        urlBuilder.append("/").append(entry.getValue());
                    }
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
                requests.add(urlBuilder.toString());
            }
        }
        return requests;
    }
}
