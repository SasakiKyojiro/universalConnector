package config.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import config.types.ParameterType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Parameter {
    @JsonProperty("type_param")
    private ParameterType typeParam;
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;
    @JsonProperty("name_b")
    private String nameB;
    @JsonProperty("flag")
    private Boolean flag;
    @JsonProperty("params")
    private List<Parameter> params; // для вложенных параметров
}
