package com.talleres.ovycar.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Aiven entrega {@code mysql://user:pass@host:port/db?ssl-mode=REQUIRED}.
 * Spring necesita {@code jdbc:mysql://...} y Connector/J usa {@code sslMode}.
 */
public class DatasourceUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String raw = firstNonBlank(
                environment.getProperty("DATABASE_URL"),
                environment.getProperty("MYSQL_URL"),
                environment.getProperty("spring.datasource.url")
        );
        if (raw == null || raw.isBlank() || raw.startsWith("${")) {
            return;
        }

        Map<String, Object> props = new LinkedHashMap<>();
        String url = raw.trim();

        if (url.startsWith("mysql://") || url.startsWith("mysqls://")) {
            applyMysqlUri(url, environment, props);
        } else if (url.startsWith("jdbc:mysql://") && url.toLowerCase(Locale.ROOT).contains("ssl-mode")) {
            props.put("spring.datasource.url", url.replace("ssl-mode", "sslMode").replace("ssl-Mode", "sslMode"));
        } else {
            return;
        }

        if (!props.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource("ovycarDatasourceUrl", props));
        }
    }

    private static void applyMysqlUri(String mysqlUrl, ConfigurableEnvironment environment, Map<String, Object> props) {
        String normalized = mysqlUrl.replaceFirst("^mysqls://", "mysql://");
        URI uri = URI.create(normalized);
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 3306;
        String path = uri.getPath() == null || uri.getPath().isBlank() ? "/defaultdb" : uri.getPath();
        String jdbc = "jdbc:mysql://" + host + ":" + port + path;
        jdbc = appendQuery(jdbc, convertAivenQuery(uri.getQuery()));
        props.put("spring.datasource.url", jdbc);

        if (uri.getUserInfo() != null) {
            String[] parts = uri.getUserInfo().split(":", 2);
            String envUser = environment.getProperty("DATABASE_USER");
            String envPass = environment.getProperty("DATABASE_PASS");
            if (!isBlank(envUser)) {
                props.put("spring.datasource.username", envUser);
            } else {
                props.put("spring.datasource.username", decode(parts[0]));
            }
            if (!isBlank(envPass)) {
                props.put("spring.datasource.password", envPass);
            } else if (parts.length > 1) {
                props.put("spring.datasource.password", decode(parts[1]));
            }
        }
    }

    private static String convertAivenQuery(String query) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("serverTimezone", "UTC");
        params.put("allowPublicKeyRetrieval", "true");
        params.put("characterEncoding", "utf8");
        params.put("zeroDateTimeBehavior", "convertToNull");

        if (query != null && !query.isBlank()) {
            for (String pair : query.split("&")) {
                if (pair.isBlank()) {
                    continue;
                }
                String[] kv = pair.split("=", 2);
                String key = kv[0].trim();
                String value = kv.length > 1 ? kv[1] : "";
                if ("ssl-mode".equalsIgnoreCase(key) || "sslMode".equalsIgnoreCase(key)) {
                    params.put("sslMode", toConnectorSslMode(value));
                } else if ("useSSL".equalsIgnoreCase(key) || "requireSSL".equalsIgnoreCase(key)) {
                    params.put(key, value);
                } else if (!"createDatabaseIfNotExist".equalsIgnoreCase(key)) {
                    params.put(key, value);
                }
            }
        }
        params.putIfAbsent("sslMode", "REQUIRED");

        StringBuilder sb = new StringBuilder();
        params.forEach((k, v) -> {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(k).append('=').append(v);
        });
        return sb.toString();
    }

    private static String toConnectorSslMode(String value) {
        String v = value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
        return switch (v) {
            case "DISABLED" -> "DISABLED";
            case "PREFERRED" -> "PREFERRED";
            case "REQUIRED" -> "REQUIRED";
            case "VERIFY_CA" -> "VERIFY_CA";
            case "VERIFY_IDENTITY" -> "VERIFY_IDENTITY";
            default -> "REQUIRED";
        };
    }

    private static String appendQuery(String jdbcBase, String query) {
        if (query == null || query.isBlank()) {
            return jdbcBase;
        }
        return jdbcBase + "?" + query;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String v : values) {
            if (!isBlank(v)) {
                return v;
            }
        }
        return null;
    }
}
