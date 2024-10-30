package org.example.config.inspector;

import org.example.config.json.Parameter;
import org.example.config.types.ParameterType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistantQualityInspector {
    private final Map<String, ParameterType> packageA = new HashMap<String, ParameterType>();
    private final Map<String, String> packageAB = new HashMap<String, String>();
    private final Map<String, ParameterType> packageB = new HashMap<String, ParameterType>();

    public void recursiveCollectionOfPackageFieldsA(@NotNull List<Parameter> list) {
        for (Parameter parameter : list) {
            if (parameter.getFlag()) {
                if (parameter.getParams() != null)
                    recursiveCollectionOfPackageFieldsA(parameter.getParams());
                else {
                    packageA.put(parameter.getName(), parameter.getTypeParam());
                    packageAB.put(parameter.getName(), parameter.getNameB());
                }
            }
        }
    }

    public void recursiveCollectionOfPackageFieldsB(@NotNull List<Parameter> list) {
        for (Parameter parameter : list) {
            if (parameter.getParams() != null)
                recursiveCollectionOfPackageFieldsB(parameter.getParams());
            else
                packageB.put(parameter.getName(), parameter.getTypeParam());
        }
    }

    public boolean comparison() {
        boolean result = true;

        for (String key : packageA.keySet()) {
            String nameB = packageAB.get(key);
            if (!nameB.isEmpty()) {
                ParameterType valueA = packageA.get(key);
                ParameterType valueB = packageB.get(nameB);
                if (!valueA.equals(valueB)) {
                    result = false;
                    System.out.printf("The %s field and the %s field have different data types.\n",
                            key, nameB);
                }
            } else {
                result = false;
                System.out.println("Field " + nameB + " missing in packageB.");
            }
        }
        return result;
    }
}
