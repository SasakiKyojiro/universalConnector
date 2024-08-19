package config.inspector;

import config.json.Authorization;
import config.json.Config;
import config.json.SystemConfig;
import config.types.AuthorizationType;
import config.types.ParameterType;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static log.LevelLog.*;

public class AvailabilityInspector {
    public static boolean availabilityInspector(@NotNull Config config) {
        boolean availability = true;
        if (!checkPathFormat(config.getLogPath())) {
            availability = false;
            System.err.println("Log path is not valid");
        }
        if (!checkLogLevel(config.getLogLevel())) {
            availability = false;
            System.err.println("Log level is not valid");
        }
        if (!checkSystemTypeA(config.getSystemTypeA())) {
            availability = false;
            System.err.println("System type A is not valid");
        }
        if (!checkSystemTypeB(config.getSystemTypeB())) {
            availability = false;
            System.err.println("System type B is not valid");
        }
        if(!(config.getSystemTypeA().getPackages().size() == config.getSystemTypeB().getPackages().size())){
            availability = false;
            System.err.println("System type A and B is not valid. The number of \"packages\" must be the same.");
        }
        return availability;
    }

    private static boolean checkSystemTypeB(SystemConfig systemTypeB) {
        boolean flag = checkAuthorizationsSystems(systemTypeB);
        if (systemTypeB.getPackagesDelay() != 0) {
            flag = false;
            System.err.println("Packages delay != 0");
        }
        return flag;
    }

    private static boolean checkSystemTypeA(@NotNull SystemConfig systemTypeA) {
        boolean flag = checkAuthorizationsSystems(systemTypeA);
        if (systemTypeA.getPackagesDelay() <= 0) {
            flag = false;
            System.err.println("Packages delay <= 0");
        }
        return flag;
    }

    private static boolean checkAuthorizationsSystems(@NotNull SystemConfig systemTypeB) {
        boolean flag = true;
        if (systemTypeB.getTimeout() < 100) {
            flag = false;
            System.err.println("Timeout < 100");
        }
        if (!checkURLFormat(systemTypeB.getDomain())) {
            flag = false;
            System.err.println("Domain is not valid");
        }
        if (!checkAuthorizations(systemTypeB.isUseAuth(), systemTypeB.getAuthorization())) {
            flag = false;
            System.err.println("Authorization is not valid");
        }
        return flag;
    }

    private static boolean checkAuthorizations(boolean useAuth, Authorization authorization) {
        boolean flag = true;
        if (useAuth) {
            if (!(authorization.getType().equals(AuthorizationType.AUTH_TOKEN)
                    || authorization.getType().equals(AuthorizationType.PERMANENT_TOKEN))) {
                flag = false;
                System.err.println("Authorization type is not valid");
            } else {
                if (authorization.getType().equals(AuthorizationType.PERMANENT_TOKEN)) {
                    if (authorization.getParams().size() != 1) {
                        flag = false;
                        System.err.println("Authorization params is not valid for auth token. There should be 1 field.");
                    } else {
                        flag = checkParamsAuthorizations(authorization, flag, 0);
                        if (authorization.isNeedUpdate()) {
                            flag = false;
                            System.err.println("Authorization needs update != false");
                        } else if (authorization.getTimeoutUpdate() != 0) {
                            flag = false;
                            System.err.println("Authorization timeout update != 0");
                        }
                    }
                }
                if (authorization.getType().equals(AuthorizationType.AUTH_TOKEN)) {
                    if (authorization.getParams().size() != 2) {
                        flag = false;
                        System.err.println("Authorization params is not valid for auth token. There should be 2 fields.");
                    } else {
                        flag = checkParamsAuthorizations(authorization, flag, 0);
                        flag = checkParamsAuthorizations(authorization, flag, 1);
                        if (!authorization.isNeedUpdate()) {
                            flag = false;
                            System.err.println("Authorization needs update == false");
                        } else {
                            if (authorization.getTimeoutUpdate() <= 0) {
                                flag = false;
                                System.err.println("Authorization timeout update <= 0");
                            }
                        }
                    }
                }
            }

        }
        return flag;
    }

    private static boolean checkParamsAuthorizations(@NotNull Authorization authorization, boolean flag, int getInt) {
        if (!(authorization.getParams().get(getInt).getTypeParam().equals(ParameterType.STRING))) {
            flag = false;
            System.err.println("Authorization params \"type_param\" is not valid");
        }
        if ((authorization.getParams().get(getInt).getValue().isEmpty())) {
            flag = false;
            System.err.println("Authorization params \"value\" is empty");
        }
        if (authorization.getParams().get(getInt).getName().isEmpty()) {
            flag = false;
            System.err.println("Authorization params \"name\" is empty");
        }
        return flag;
    }

    private static boolean checkURLFormat(String domain) {
        String pattern = "^http://[a-zA-Z0-9_.~+-/&?=%:#;]*$+(:\\d+)?";
        return Pattern.matches(pattern, domain);
    }

    private static boolean checkPathFormat(@NotNull String inputString) {
        return !inputString.isEmpty();
    }

    private static boolean checkLogLevel(@NotNull String inputString) {
        return (inputString.equals(Debug.toString()) || inputString.equals(Warning.toString()) ||
                inputString.equals(Error.toString()) || inputString.equals(Fatal.toString()));
    }
}
