package ru.tdd.telegram_bot.controller.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.util.Objects;
import java.util.Properties;

/**
 * @author Tribushko Danil
 * @since 20.12.2025
 * Фабрика для работы .yaml файлами
 */
public class YamlPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) {
        YamlPropertiesFactoryBean yamlFactoryBean = new YamlPropertiesFactoryBean();
        Resource propertyResource = resource.getResource();

        yamlFactoryBean.setResources(propertyResource);
        Properties properties = yamlFactoryBean.getObject();

        assert properties != null;
        return new PropertiesPropertySource(Objects.requireNonNull(propertyResource.getFilename()), properties);
    }
}
