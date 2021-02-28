package util;

public class Config {
    public static final String DEVICE_ARCHITECTURE_NOT_SUPPORTED_ERROR = "Device architecture NOT Supported.";
    public static final String COPY_ERROR = "Poc copy ERROR!";
    public static final String RECOVER_FAILED = "Recover FAILED";

    public static final String TEMP_POC_LOCATION = "/sdcard/SSAVD/pocs/";
    public static final String FINAL_POC_LOCATION = "/data/local/tmp/SSAVD/pocs/";

    public static final int PARTICULAR_OUTCOME_NONE = 0;
    public static final int PARTICULAR_OUTCOME_REBOOT = 1;
    public static final int PARTICULAR_OUTCOME_SHUTDOWN = 2;
    public static final int PARTICULAR_OUTCOME_SYSTEM_HALTED = 3;

    public static final int SHOW_DEVICE_INFO_TASK = 0;
    public static final int DO_TEST_TASK = 1;
}
