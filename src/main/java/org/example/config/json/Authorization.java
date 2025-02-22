package org.example.config.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.config.types.AuthorizationType;

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
    @JsonProperty("url")
    private String url;
    @JsonProperty("name")
    private String name;
    @JsonProperty("params")
    private List<Parameter> params;
    @JsonProperty("need_update")
    private boolean needUpdate;
    @JsonProperty("timeout_update")
    private int timeoutUpdate;
}
