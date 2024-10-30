package org.example.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.config.types.ParameterType;

import java.util.List;

@Getter

public class Parameter {
    @JsonProperty("type_param")
    private ParameterType typeParam;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;
    @Setter
    private String valueTMP;
    @JsonProperty("name_b")
    private String nameB;
    @JsonProperty("flag")
    private Boolean flag;
    @JsonProperty("params")
    private List<Parameter> params; // для вложенных параметров
}
