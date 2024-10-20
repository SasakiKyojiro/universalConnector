package org.example.config.inspector;

import org.example.config.json.Config;
import org.example.config.packages.LinkedPackage;
import org.example.config.packages.PackageConnector;

import java.util.List;

public class QualityInspector {
    public static boolean qualityInspector(Config config) {
        boolean result = true;
        List<LinkedPackage> linkedPackages = PackageConnector.connectPackages(config);
        for (LinkedPackage linkedPackage : linkedPackages) {
            AssistantQualityInspector assistantQualityInspector = new AssistantQualityInspector();
            assistantQualityInspector.recursiveCollectionOfPackageFieldsA(linkedPackage.getAPackage().getResponseParams());
            assistantQualityInspector.recursiveCollectionOfPackageFieldsB(linkedPackage.getBPackage().getRequestBody());
            assistantQualityInspector.recursiveCollectionOfPackageFieldsB(linkedPackage.getBPackage().getRequestParams());
            if (!assistantQualityInspector.comparison())
                result = false;
        }

        return result;
    }
}
