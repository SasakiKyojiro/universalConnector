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
    private System system_type_a;
    private System system_type_b;
}
