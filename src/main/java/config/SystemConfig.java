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
public class SystemConfig {
    private int timeout;
    private String domain;
    private boolean use_auth;
    private Authorization authorization;
    private int packages_delay;
    private List<Package> packages;
}
