package com.wgd.registry.zk.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wgd
 * @create 2021/08/01  16:03
 */
public class CuratorUtils {

    //构造zkClient用到的参数
    private static final int BASE_SLEEP_TIME_MS = 1000;
    private static final int MAX_RETRIES = 3;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    private static final String ROOT_PATH = "wgdRPC";

    //对于服务端：已经注册服务节点的本地缓存
    private static final Set<String> REGISTERED_PATH_CACHE = ConcurrentHashMap.newKeySet();
    //对于客户端：已经使用过的服务节点的本地缓存
    private static final Map<String, List<String>> SERVICE_ADDRESS_CACHE = new ConcurrentHashMap<>();

    private static CuratorFramework zkClient;

    //因为是工具类，构造器私有化
    private CuratorUtils(){

    }

    /**
     * 获取单例的zkClient
     * @return
     */
    public static CuratorFramework getZKClient(){

        //如果已经有zk的客户端存在了，即之前已经使用过了，就不需要重新创建
        if(zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        //如果客户端不存在或者客户端已经关闭了，那就重新创建
        //指定重试策略：重试之间的休眠时间为1秒，最多重试3次
        RetryPolicy policy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES);
        //构造客户端，使用链式编程
        zkClient = CuratorFrameworkFactory.builder()
                    .connectString(DEFAULT_ZOOKEEPER_ADDRESS)
                    .retryPolicy(policy)
                    .namespace(ROOT_PATH)
                    .build();
        zkClient.start();

        //给客户端30秒的连接时间，如果连接超时则抛出异常
//        try {
//            if(zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
//                throw new RuntimeException("连接ZooKeeper超时！");
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        return zkClient;
    }

    /**
     * 把serviceName创建成永久节点，把serviceAddress创建成临时节点
     * 通过本地的一个Set缓存已经注册好的服务，避免了重复注册，也避免了从zookeeper中查询花费的时间
     * @param client
     * @param serviceName
     * @param serviceAddress
     */
    public static void createServiceNode(CuratorFramework client, String serviceName, InetSocketAddress serviceAddress) {

        try {
            //1、把serviceName创建成永久节点，使得服务器下线时，服务名字仍然在，只把服务器地址删除
            if(REGISTERED_PATH_CACHE.contains(serviceName)
                || client.checkExists().forPath("/" + serviceName) != null) {
                System.out.println("该服务名的永久节点已经创建好了！");
            } else {
                //创建成永久节点
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
                //加入本地缓存中
                REGISTERED_PATH_CACHE.add(serviceName);
            }

            //2、把serviceAddress创建成临时节点，使得服务器下线时，把服务器地址删除
            String path = "/" + serviceName + serviceAddress.toString();
            if(REGISTERED_PATH_CACHE.contains(path)
                    || client.checkExists().forPath(path) != null) {
                System.out.println("该服务地址的临时节点已经创建好了！");
            } else {
                //创建成临时节点
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
                //加入本地缓存中
                REGISTERED_PATH_CACHE.add(path);
            }
        } catch (Exception e) {
            System.out.println("创建节点出错了！");
            e.printStackTrace();
        }

    }

    /**
     * 根据服务名来获取孩子节点，即获取提供此服务的服务器集合
     * @param client
     * @param serviceName
     * @return
     */
    public static List<String> getChildrenNodesByServiceName(CuratorFramework client, String serviceName) {
        //如果本地缓存能够找到则直接返回
        if(SERVICE_ADDRESS_CACHE.containsKey(serviceName)) {
            return SERVICE_ADDRESS_CACHE.get(serviceName);
        }

        List<String> result = null;
        try {
            result = zkClient.getChildren().forPath("/" + serviceName);
            SERVICE_ADDRESS_CACHE.put(serviceName, result);
            //开启监听这个节点
            registerWatcher(client, serviceName);
        } catch (Exception e) {
            System.out.println("根据服务名获取孩子节点失败了！");
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 监听对指定节点的更改
     * @param client
     * @param serviceName
     */
    private static void registerWatcher(CuratorFramework client, String serviceName) throws Exception {

        String path = "/" + serviceName;
        //使用PathChildrenCache监听服务名下面的子节点变化
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        //设置Listener，如果有更改则更新本地缓存
        PathChildrenCacheListener listener =  new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                List<String> list = curatorFramework.getChildren().forPath(path);
                SERVICE_ADDRESS_CACHE.put(serviceName, list);
            }
        };
        //加入listener
        pathChildrenCache.getListenable().addListener(listener);
        //开启监听
        pathChildrenCache.start();
    }

}
