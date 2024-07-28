package config.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    @JsonProperty("logging")
    private boolean logging;
    @JsonProperty("log_path")
    private String logPath;
    @JsonProperty("system_type_a")
    private SystemConfig systemTypeA;
    @JsonProperty("system_type_b")
    private SystemConfig systemTypeB;
}
