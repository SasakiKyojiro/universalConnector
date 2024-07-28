package config.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import config.types.AuthorizationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Authorization {
    @JsonProperty("type")
    private AuthorizationType type;
    @JsonProperty("need_loging")
    private boolean needLogging;
    @JsonProperty("method")
    private String method;
    @JsonProperty("name")
    private String name;
    @JsonProperty("params")
    private List<Parameter> params;
    @JsonProperty("need_update")
    private boolean needUpdate;
    @JsonProperty("timeout_update")
    private int timeoutUpdate;
}
