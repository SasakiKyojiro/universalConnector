package config.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import config.types.Method;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Package {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("key")
    private String key;
    @JsonProperty("method")
    private Method method;
    @JsonProperty("url")
    private String url;
    @JsonProperty("delay")
    private Integer delay;
    @JsonProperty("path_variable")
    private Parameter pathVariable;
    @JsonProperty("request_params")
    private List<Parameter> requestParams;
    @JsonProperty("request_body")
    private List<Parameter> requestBody;
    @JsonProperty("response_params")
    private List<Parameter> responseParams;
}
