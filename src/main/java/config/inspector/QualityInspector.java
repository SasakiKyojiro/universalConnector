package config.inspector;

import config.json.Config;
import config.packages.LinkedPackage;
import config.packages.PackageConnector;

import java.util.List;

public class QualityInspector {
    public static boolean qualityInspector(Config config) {
        boolean result = true;
        List<LinkedPackage> linkedPackages = PackageConnector.connectPackages(config);
        for (LinkedPackage linkedPackage : linkedPackages) {
            AssistantQualityInspector assistantQualityInspector = new AssistantQualityInspector();
            assistantQualityInspector.recursiveCollectionOfPackageFieldsA(linkedPackage.getAPackage().getResponseParams());
            assistantQualityInspector.recursiveCollectionOfPackageFieldsB(linkedPackage.getBPackage().getRequestBody());
            if (!assistantQualityInspector.comparison())
                result = false;
        }

        return result;
    }
}
