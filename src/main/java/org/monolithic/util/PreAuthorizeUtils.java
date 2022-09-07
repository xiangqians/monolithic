package org.monolithic.util;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link PreAuthorize} 工具类
 *
 * @author xiangqian
 * @date 22:10 2022/09/07
 */
public class PreAuthorizeUtils {

    // \(\'([^\)\']*)\'\)
    // \(([ ]*)\'([^\)\']*)\'([ ]*)\)
    private static final String REGEX0 = "\\(([ ]*)\\'([^\\)\\']*)\\'([ ]*)\\)";
    private static final Pattern PATTERN0 = Pattern.compile(REGEX0);

    // ,([ ]*)\'([^\)\']*)\'([ ]*)\)
    private static final String REGEX1 = ",([ ]*)\\'([^\\)\\']*)\\'([ ]*)\\)";
    private static final Pattern PATTERN1 = Pattern.compile(REGEX1);

    public static String getValue(PreAuthorize preAuthorize) {
        String originalValue = preAuthorize.value();
        String value = null;
        if (originalValue.contains("hasPermission")) {
            Matcher matcher = PATTERN0.matcher(originalValue);
            if (matcher.find()) {
                value = matcher.group();
            }
        } else if (originalValue.contains("hasAnyAuthorityAndPermission")) {
            Matcher matcher = PATTERN1.matcher(originalValue);
            if (matcher.find()) {
                value = matcher.group();
            }
        }

        if (Objects.nonNull(value)) {
            int index = value.indexOf("'");
            return value.substring(index + 1, value.indexOf("'", index + 1));
        }

        return null;
    }

}
