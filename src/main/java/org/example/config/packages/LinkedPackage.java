package org.example.config.packages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.config.json.Package;


@AllArgsConstructor
@Getter
public class LinkedPackage {
    Package aPackage;
    Package bPackage;
}
