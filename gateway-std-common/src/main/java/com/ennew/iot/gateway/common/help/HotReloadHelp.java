/**
 * llkang.com Inc.
 * Copyright (c) 2010-2023 All Rights Reserved.
 */
package com.ennew.iot.gateway.common.help;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

/**
 * @author kanglele
 * @version $Id: HotReloadHelp, v 0.1 2023/5/18 19:10 kanglele Exp $
 */
public class HotReloadHelp {

    private final TransmittableThreadLocal<URLClassLoader> classLoader = new TransmittableThreadLocal<>();

    public HotReloadHelp(String jarPath) throws Exception {
        classLoader.set(classLoader(jarPath));
        //this.classLoader = new URLClassLoader(new URL[]{new File(jarPath).toURI().toURL()});
    }

    @SneakyThrows
    private URLClassLoader classLoader(String location) {
        URL url;
        if (!location.contains("://")) {
            url = new File(location).toURI().toURL();
        } else {
            // 直接使用远程jar文件的URL创建了一个URLClassLoader。不需要将jar文件下载到本地。
            // 但是，请注意，这种方法可能会导致性能问题，因为类加载器需要从远程URL加载类。如果网络连接较慢，这可能会影响到应用程序的性能。
            url = new URL("jar:" + location + "!/");
        }
        return new URLClassLoader(new URL[]{url}, getClassLoader());
    }

    private ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    /**
     * invoke方法
     *
     * @param className
     * @param methodName
     * @param args
     * @return
     * @throws Exception
     */
    public Object invokeMethod(String className, String methodName, Object... args) throws Exception {
        Class<?> loadedClass = loadClass(className);
        Method method = loadedClass.getMethod(methodName, getParameterTypes(args));
        if (Modifier.isStatic(method.getModifiers())) {
            //静态方法obj可以是null
            return method.invoke(null, args);
        } else {
            Object obj = loadObj(loadedClass);
            return method.invoke(obj, args);
        }
    }

    private Class<?>[] getParameterTypes(Object[] args) {
        if (Objects.nonNull(args) && args.length > 0) {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            return parameterTypes;
        }
        return null;
    }

    /**
     * 加载class
     *
     * @param className
     * @return
     * @throws Exception
     */
    public Class<?> loadClass(String className) throws Exception {
        return classLoader.get().loadClass(className);
    }

    /**
     * 实例化对象
     *
     * @param className
     * @return
     * @throws Exception
     */
    public Object loadObj(String className) throws Exception {
        return loadClass(className).getDeclaredConstructor().newInstance();
    }

    public Object loadObj(Class<?> clazz) throws Exception {
        return clazz.getDeclaredConstructor().newInstance();
    }


    /**
     * 重新加载一定要用新的加载器
     */
//    private static Class<?> reloadClass(String jarPath, String className, Class<?> clazz) throws Exception {
//        URL[] urls = new URL[]{new File(jarPath).toURI().toURL()};
//        URLClassLoader classLoader = URLClassLoader.newInstance(urls);
//        Class<?> newClass = classLoader.loadClass(className);
//        if (newClass != clazz) {
//            return newClass;
//        } else {
//            return clazz;
//        }
//    }
}
