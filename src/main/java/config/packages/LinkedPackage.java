package config.packages;

import config.json.Package;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class LinkedPackage {
    Package aPackage;
    Package bPackage;
}
