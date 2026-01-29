package ru.tdd.geo.application.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Tribushk Danil
 * @since 20.12.2025
 * Утилиты для работы с url
 */
public class URLUtils {

    private URLUtils() {
    }

    public static URLBuilder builder(String url) {
        return new URLBuilder(url);
    }

    /**
     * Класс для построки строки url адреса
     */
    public static class URLBuilder {

        private final StringBuilder url;

        public URLBuilder(String url) {
            this.url = new StringBuilder(url);
        }

        public URLBuilder addPathPart(Object pathPart) {
            url.append("/").append(pathPart);
            return this;
        }

        public URLBuilder addQueryParameter(String key, Object value, boolean enableEncode) {
            if (!url.toString().contains("?")) {
                url.append("?");
            } else {
                url.append("&");
            }

            if (enableEncode)
                url.append(key).append("=").append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
            else
                url.append(key).append("=").append(value.toString());

            return this;
        }

        public URLBuilder addQueryParameter(String key, Object value) {
            if (!url.toString().contains("?")) {
                url.append("?");
            } else {
                url.append("&");
            }

            url.append(key).append("=").append(value.toString());

            return this;
        }

        public String build() {
            return url.toString();
        }
    }
}
