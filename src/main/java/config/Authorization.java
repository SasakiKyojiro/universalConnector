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
public class Authorization {
    private AuthorizationType type;
    private boolean need_loging;
    private String method;
    private String name;
    private List<Parameter> params;
    private boolean need_update;
    private int timeout_update;
}
