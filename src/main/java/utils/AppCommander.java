package utils;

import com.beust.jcommander.Parameter;

public class AppCommander {
    @Parameter(
            names = {"--pathToConfig", "-ptc"},
            description = "Путь к конфигурационному файлу к программе"
    )
    public String pathToConfig = "";
}
