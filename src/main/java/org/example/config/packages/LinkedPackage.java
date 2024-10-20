package org.example.config.packages;

import org.example.config.json.Package;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class LinkedPackage {
    Package aPackage;
    Package bPackage;
}
