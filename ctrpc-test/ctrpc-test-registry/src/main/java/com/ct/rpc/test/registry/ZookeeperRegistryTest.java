package com.ct.rpc.test.registry;

import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.registry.zookeeper.ZookeeperRegistryService;
import com.ct.rpc.spi.loader.ExtensionLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class ZookeeperRegistryTest {
    private RegistryService registryService;
    private ServiceMeta serviceMeta;

    @Before
    public void init() throws Exception{
        RegistryConfig registryConfig = new RegistryConfig("127.0.0.1:2181", "zookeeper", "random");
        this.registryService = ExtensionLoader.getExtension(RegistryService.class, "zookeeper");
        this.registryService.init(registryConfig);
        this.serviceMeta = new ServiceMeta(ZookeeperRegistryTest.class.getName(), "1.0.0","ct","127.0.0.1",8080,1);
    }

    @Test
    public void testRegister() throws Exception{
        this.registryService.register(serviceMeta);
    }

    @Test
    public void testUnregister() throws Exception{
        this.registryService.unregister(serviceMeta);
    }

    @Test
    public void testDiscovery() throws Exception{
        this.registryService.discovery(RegistryService.class.getName(), "ct".hashCode(),"127.0.0.1");
    }

    @Test
    public void testDestroy() throws Exception {
        this.registryService.destroy();
    }
}
