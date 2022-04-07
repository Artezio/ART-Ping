package com.artezio.artping.validator;

public final class SystemSettingsRestrictions {
    
    private SystemSettingsRestrictions() {
    }
    
    public static final int MIN_TEST_SUCCESSFUL_MINUTES = 1;
    public static final int MAX_TEST_SUCCESSFUL_MINUTES = 30;
    public static final int MIN_TEST_NO_RESPONSE_MINUTES = 5;
    public static final int MAX_TEST_NO_RESPONSE_MINUTES = 120;
    public static final int MIN_RECOMMENDED_DAILY_CHECKS = 1;
    public static final int MAX_RECOMMENDED_DAILY_CHECKS = 50;
    
}
