package ru.tdd.core.application.utils;

import java.util.Optional;

/**
 * @author Tribushko Danil
 * @since 28.05.2026
 * Методы для работы с url адресами
 */
public class UrlUtils {

    private UrlUtils() {
    }

    public static Builder builder(String url) {
        return new Builder(url);
    }

    public static class Builder {

        private final StringBuilder urlBuilder;

        public Builder(String url) {
            this.urlBuilder = new StringBuilder(url);
        }

        public Builder add(Object value) {
            urlBuilder.append("/")
                    .append(value);

            return this;
        }

        public Builder add(String key, Object value) {
            var urlString = urlBuilder.toString();

            urlBuilder.append(urlString.contains("?") ? "&" : "?").append(key)
                    .append("=");

            Optional.ofNullable(value).ifPresent(urlBuilder::append);

            return this;
        }

        public String build() {
            return urlBuilder.toString();
        }
    }
}
