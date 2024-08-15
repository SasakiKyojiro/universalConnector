package client;

import config.json.Parameter;
import config.types.ParameterType;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


// Функция рекурсивно проходится по ответу с сервера А и, удаляя лишние поля (в конфиге у них "flag": false), подставляет названия поля из "name_b"
// промежуточный этап из PackageProcessor в PackageCollectorB
public class PackageRecursiveHandler {
    public static @NotNull JSONObject recursivePackage(@NotNull List<Parameter> parameterPackage, @NotNull JSONObject response) {
        JSONObject packageJSON = new JSONObject();
        for (Parameter parameter : parameterPackage) {
            objectParse(parameter, response, packageJSON);
        }
        return packageJSON;
    }

    private static void objectParse(@NotNull Parameter parameter, @NotNull JSONObject response, JSONObject packageJSON) {
        if (parameter.getFlag())
            if (parameter.getTypeParam() == ParameterType.OBJECT) {
                JSONObject jsonObject = response.getJSONObject(parameter.getName());
                JSONObject json = recursivePackage(parameter.getParams(), jsonObject);
                packageJSON.put(parameter.getName(), json);
            } else if (parameter.getTypeParam() == ParameterType.LIST_OBJECT) {
                JSONArray jsonObject = response.getJSONArray(parameter.getName());
                JSONArray json = jsonArrayParse(parameter.getParams(), jsonObject);
                packageJSON.put(parameter.getName(), json);
            } else parsing(response, packageJSON, parameter);
    }

    private static @NotNull JSONArray jsonArrayParse(List<Parameter> params, @NotNull JSONArray jsonObject) {
        JSONArray answer = new JSONArray();
        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject jsonTMP1 = jsonObject.getJSONObject(i);
            JSONObject jsonTMP2 = new JSONObject();
            for (Parameter parameter : params)
                if (parameter.getFlag())
                    parsing(jsonTMP1, jsonTMP2, parameter);
            answer.put(jsonTMP2);
        }
        return answer;
    }

    private static void parsing(@NotNull JSONObject response, JSONObject packageJSON, @NotNull Parameter parameter) {
        if (parameter.getTypeParam().equals(ParameterType.STRING)) {
            packageJSON.put(parameter.getNameB(), response.getString(parameter.getNameB()));
        } else if (parameter.getTypeParam().equals(ParameterType.LIST_INT)) {
            packageJSON.put(parameter.getNameB(), response.getJSONArray(parameter.getNameB()));
        } else if (parameter.getTypeParam().equals(ParameterType.INT)) {
            packageJSON.put(parameter.getNameB(), response.getInt(parameter.getNameB()));
        } else if (parameter.getTypeParam().equals(ParameterType.BOOLEAN)) {
            packageJSON.put(parameter.getNameB(), response.getBoolean(parameter.getNameB()));
        } else if (parameter.getTypeParam().equals(ParameterType.DATETIME)) {
            packageJSON.put(parameter.getNameB(), response.getString(parameter.getNameB()));
        } else if (parameter.getTypeParam().equals(ParameterType.AUTH_TOKEN)) {
            packageJSON.put(parameter.getNameB(), response.getString(parameter.getNameB()));
        }
    }
}
