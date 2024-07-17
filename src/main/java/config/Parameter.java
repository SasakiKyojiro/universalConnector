package config;

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
    private String type_param;
    private String name;
    private String value;
    private List<Parameter> params; // для вложенных параметров
}
