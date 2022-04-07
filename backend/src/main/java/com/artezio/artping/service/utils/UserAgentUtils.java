package com.artezio.artping.service.utils;

import com.artezio.artping.entity.DeviceTypeEnum;
import com.blueconic.browscap.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

import static java.util.Objects.nonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UserAgentUtils {

    private static final UserAgentParser PARSER = getUserAgentParser();

    public static DeviceTypeEnum detectDeviceType(String userAgentString) {
        if (nonNull(PARSER)) {
            Capabilities capabilities = PARSER.parse(userAgentString);
            return DeviceTypeEnum.fromName(capabilities.getDeviceType());
        }

        return DeviceTypeEnum.UNDEFINED;
    }

    private static UserAgentParser getUserAgentParser() {
        try {
            return new UserAgentService().loadParser(Arrays.asList(BrowsCapField.BROWSER, BrowsCapField.DEVICE_TYPE));
        } catch (IOException | ParseException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

}
