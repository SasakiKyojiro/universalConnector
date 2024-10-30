package org.example.config.packages;

import org.example.config.json.Config;
import org.example.config.json.Package;
import org.example.config.json.SystemConfig;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageConnector {
    static public List<LinkedPackage> connectPackages(@NotNull Config config) {
        SystemConfig systemTypeA = config.getSystemTypeA();
        SystemConfig systemTypeB = config.getSystemTypeB();
        Map<Integer, Package> packageMap = new HashMap<>();
        List<LinkedPackage> linkedPackages = new ArrayList<>();

        // Соединяем пакеты из systemTypeA
        for (Package pkg : systemTypeA.getPackages()) {
            packageMap.put(pkg.getId(), pkg);
        }

        // Соединяем пакеты из systemTypeB
        for (Package pkg : systemTypeB.getPackages()) {
            Package existingPackage = packageMap.get(pkg.getId());
            if (existingPackage != null) {
                linkedPackages.add(new LinkedPackage(existingPackage, pkg));
            } else {
                try {
                    throw new Exception("The set of packages is not complete");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // Возвращаем связанные пакеты
        return linkedPackages;
    }
}
