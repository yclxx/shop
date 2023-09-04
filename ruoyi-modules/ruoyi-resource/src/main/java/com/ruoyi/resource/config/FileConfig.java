package com.ruoyi.resource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传
 */
@Component
@ConfigurationProperties(value = "file")
public class FileConfig {

    public static String domain;

    public static String path;

    public static String prefix;

    public static String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        FileConfig.domain = domain;
    }

    public static String getPath() {
        return path;
    }

    public void setPath(String path) {
        FileConfig.path = path;
    }

    public static String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        FileConfig.prefix = prefix;
    }
}
