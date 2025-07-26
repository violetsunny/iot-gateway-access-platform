//package com.ennew.iot.gateway.biz.jarload;
//
//import com.ennew.iot.gateway.client.protocol.Protocol;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.File;
//import java.net.URL;
//import java.util.Map;
//import java.util.ServiceLoader;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 动态加载Jar包来实现协议支持.
// * jar包不支持使用第三方依赖.
// *
// * @author hanyilong@enn.cn
// */
//@Slf4j
//public class JarProtocolSupportLoader  {
//    //ClassLoader缓存
//    private final Map<String, ProtocolClassLoader> protocolLoaders = new ConcurrentHashMap<>();
//
//    public String getProvider() {
//        return "jar";
//    }
//
//
//    @SneakyThrows
//    public <T> T load(String location, String protocolName,Class<T> tClass) {
//            try {
//                URL url;
//
//                if (!location.contains("://")) {
//                    //本地文件
//                    url = new File(location).toURI().toURL();
//                } else {
//                    //url
//                    url = new URL("jar:" + location + "!/");
//                }
//
//                ProtocolClassLoader loader;
//                URL fLocation = url;
//
//                loader = protocolLoaders.compute(protocolName, (key, old) -> {
//                    if (null != old) {
//                        try {
//                            //释放旧的ClassLoader
//                            closeLoader(old);
//                        } catch (Exception ignore) {
//                        }
//                    }
//                    return createClassLoader(fLocation);
//                });
//
//                T protocol;
//                log.debug("load protocol support from : {}", location);
//                if (protocolName != null) {
//                    //直接从classLoad获取,防止冲突
//                    @SuppressWarnings("all")
//                    Class<T> providerType = (Class) loader.loadSelfClass(protocolName);
//                    protocol = providerType.getDeclaredConstructor().newInstance();
//                } else {
//                    //没有指定provider，则通过SPI来获取
//                    protocol = ServiceLoader.load(tClass, loader).iterator().next();
//                }
//                return protocol;
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//            return null;
//    }
//
//    /**
//     * 根据URL创建ClassLoader
//     *
//     * @param location location
//     * @return ProtocolClassLoader
//     */
//    protected ProtocolClassLoader createClassLoader(URL location) {
//        return new ProtocolClassLoader(new URL[]{location}, this.getClass().getClassLoader());
//    }
//
//    /**
//     * 关闭(释放)classLoader
//     *
//     * @param loader classLoader
//     */
//    @SneakyThrows
//    protected void closeLoader(ProtocolClassLoader loader) {
//        loader.close();
//    }
//
//}
