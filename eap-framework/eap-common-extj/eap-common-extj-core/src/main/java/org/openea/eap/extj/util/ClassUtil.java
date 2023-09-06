package org.openea.eap.extj.util;


import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 *
 *
 */
@Component
public class ClassUtil {

    private static Environment environment;
    private static ResourcePatternResolver resourcePatternResolver;
    private static MetadataReaderFactory metadataReaderFactory;

    @Autowired(required = false)
    public void setEnvironment(Environment environment) {
        ClassUtil.environment = environment;
    }

    @Autowired(required = false)
    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        ClassUtil.metadataReaderFactory = metadataReaderFactory;
    }

    @Autowired(required = false)
    public void setResourcePatternResolver(ResourcePatternResolver resourcePatternResolver) {
        ClassUtil.resourcePatternResolver = resourcePatternResolver;
    }
    private static Environment getEnvironment() {
        if (environment == null) {
            environment = new StandardEnvironment();
        }
        return environment;
    }

    protected static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    private static ResourcePatternResolver getResourcePatternResolver() {
        if (resourcePatternResolver == null) {
            resourcePatternResolver = new PathMatchingResourcePatternResolver();
        }
        return resourcePatternResolver;
    }
    private static MetadataReaderFactory getMetadataReaderFactory() {
        if (metadataReaderFactory == null) {
            metadataReaderFactory = new CachingMetadataReaderFactory();
        }
        return metadataReaderFactory;
    }
    public static Set<Class<?>> scanCandidateComponents(String basePackage, Predicate<Class> predicate) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + '/' +  "**/*.class";
            Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
                Class cls = environment.getClass().getClassLoader().loadClass(metadataReader.getClassMetadata().getClassName());
                if(predicate.test(cls)){
                    classes.add(cls);
                }
            }
        }
        catch (IOException ex) {
            throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return classes;
    }
}
