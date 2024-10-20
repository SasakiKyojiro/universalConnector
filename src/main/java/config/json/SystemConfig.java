package config.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SystemConfig {
    @JsonProperty("timeout")
    private int timeout;
    @JsonProperty("domain")
    private String domain;
    @JsonProperty("use_auth")
    private boolean useAuth;
    @JsonProperty("use_certificate")
    private boolean useCertificate;
    @JsonProperty("authorization")
    private Authorization authorization;
    @JsonProperty("packages_delay")
    private int packagesDelay;
    @JsonProperty("packages")
    private List<Package> packages;
}
