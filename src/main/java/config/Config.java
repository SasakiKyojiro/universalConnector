package config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    private boolean logging;
    private String log_path;
    private SystemConfig system_type_a;
    private SystemConfig system_type_b;
}
