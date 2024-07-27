package config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Package {
    private Integer id;
    private String key;
    private Method method;
    private String url;
    private Integer delay;
    private Parameter path_variable;
    private List<Parameter> request_params;
    private List<Parameter> request_body;
    private List<Parameter> response_params;
}
