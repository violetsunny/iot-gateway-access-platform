package com.enn.iot.dtu.common.context;

import com.enn.iot.dtu.protocol.api.codec.dto.AbstractIotCmdReq;
import com.enn.iot.dtu.protocol.api.maindata.dto.DtuDeviceDTO;
import com.enn.iot.dtu.protocol.api.maindata.dto.MainDataDTO;
import io.netty.channel.Channel;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class IotGlobalContextUtil {

    private static final int MAP_INITIAL_CAPACITY = 2000;

    private IotGlobalContextUtil() {}

    /**
     * 断开连接后，调用此方法
     *
     * @param gatewaySn 网关标识
     */
    public static void deleteContext(String gatewaySn) {
        MainData.deleteContext(gatewaySn);
        Channels.deleteContext(gatewaySn);
        WriteGatewayChannels.deleteContext(gatewaySn);
        ReadCmd.deleteContext(gatewaySn);
        WriteCmd.deleteContext(gatewaySn);
    }

    public static class Channels {
        private static final ConcurrentHashMap<String, Channel> GLOBAL_CACHE =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);

        /**
         * 认证成功后需要调用此方法
         *
         * @param gatewaySn 网关标识
         * @param channel 通道
         */
        public static void addChannel(String gatewaySn, Channel channel) {
            GLOBAL_CACHE.put(gatewaySn, channel);
        }

        public static Channel getChannel(String gatewaySn) {
            return GLOBAL_CACHE.get(gatewaySn);
        }

        /**
         * @param gatewaySn 网关标识
         */
        static void deleteContext(String gatewaySn) {
            GLOBAL_CACHE.remove(gatewaySn);
        }

        public static Map<String, Channel> getAllChannels() {
            return Collections.unmodifiableMap(GLOBAL_CACHE);
        }
    }

    /**
     * 保存具有下发控制指令的网关到缓存
     *
     * @author Mr.Jia
     * @date 2022/8/2 4:21 PM
     */
    public static class WriteGatewayChannels {
        private static final ConcurrentHashMap<String, Channel> GLOBAL_WRITE_GATEWAY_CACHE =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);

        /**
         * 下发指令后需要调用此方法
         *
         * @param gatewaySn 网关标识
         * @param channel 通道
         */
        public static void addChannel(String gatewaySn, Channel channel) {
            GLOBAL_WRITE_GATEWAY_CACHE.put(gatewaySn, channel);
        }

        public static Channel getChannel(String gatewaySn) {
            return GLOBAL_WRITE_GATEWAY_CACHE.get(gatewaySn);
        }

        /**
         * @param gatewaySn 网关标识
         */
        static void deleteContext(String gatewaySn) {
            GLOBAL_WRITE_GATEWAY_CACHE.remove(gatewaySn);
        }

        public static Map<String, Channel> getAllChannels() {
            return Collections.unmodifiableMap(GLOBAL_WRITE_GATEWAY_CACHE);
        }
    }

    public static class MainData {
        private static final ConcurrentHashMap<String, MainDataDTO> GLOBAL_CACHE =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);
        private static final ConcurrentHashMap<String, Boolean> GLOBAL_STATUS =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);
        private static final ConcurrentHashMap<String, List<DtuDeviceDTO>> GLOBAL_DEVICE_CACHE =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY*10);

        private MainData() {}

        public static void setDeviceDataMap(Map<String, List<DtuDeviceDTO>> deviceDataMap) {
            GLOBAL_DEVICE_CACHE.putAll(deviceDataMap);
        }

        public static DtuDeviceDTO getDeviceData(String key) {
            List<DtuDeviceDTO> dtuDeviceDTOs = GLOBAL_DEVICE_CACHE.get(key);
            if (CollectionUtils.isEmpty(dtuDeviceDTOs)) {
                return null;
            }
            return dtuDeviceDTOs.get(0);
        }

        public static void setMainData(String gatewaySn, MainDataDTO mainData) {
            GLOBAL_CACHE.put(gatewaySn, mainData);
        }

        public static MainDataDTO getMainData(String gatewaySn) {
            return GLOBAL_CACHE.get(gatewaySn);
        }

        public static Long getUpdateTime(String gatewaySn) {
            MainDataDTO mainData = GLOBAL_CACHE.get(gatewaySn);
            if (null == mainData) {
                return null;
            }
            return mainData.getUpdateTime();
        }

        public static void setRefreshing(String gatewaySn, boolean isRefreshing) {
            GLOBAL_STATUS.put(gatewaySn, isRefreshing);
        }

        public static Boolean getRefreshing(String gatewaySn) {
            Boolean result = GLOBAL_STATUS.get(gatewaySn);
            return result != null && result;
        }

        public static Map<String, MainDataDTO> getAllMainData() {
            return Collections.unmodifiableMap(GLOBAL_CACHE);
        }

        static void deleteContext(String gatewaySn) {
            GLOBAL_CACHE.remove(gatewaySn);
            GLOBAL_STATUS.remove(gatewaySn);
        }
    }

    public static class ReadCmd {
        private static final ConcurrentHashMap<String, List<AbstractIotCmdReq>> GLOBAL_LIST =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);
        private static final ConcurrentHashMap<String, Queue<AbstractIotCmdReq>> GLOBAL_QUEUE =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);

        private ReadCmd() {}

        static void deleteContext(String gatewaySn) {
            GLOBAL_LIST.remove(gatewaySn);
            GLOBAL_QUEUE.remove(gatewaySn);
        }

        public static List<AbstractIotCmdReq> getList(String gatewaySn) {
            List<AbstractIotCmdReq> readCmdList = GLOBAL_LIST.get(gatewaySn);
            return readCmdList == null ? Collections.emptyList() : readCmdList;
        }

        public static int getListSize(String gatewaySn) {
            return getList(gatewaySn).size();
        }

        /**
         * 更新读指令队列<br/>
         * 覆盖List，并触发rebuildQueue
         *
         * @param gatewaySn 网关标识
         * @param readCmdList 读指令列表
         */
        public static void setList(String gatewaySn, List<AbstractIotCmdReq> readCmdList) {
            GLOBAL_LIST.put(gatewaySn, Collections.unmodifiableList(readCmdList));
            rebuildQueue(gatewaySn);
        }

        public static Queue<AbstractIotCmdReq> getQueue(String gatewaySn) {
            Queue<AbstractIotCmdReq> cmdQueue = GLOBAL_QUEUE.get(gatewaySn);
            if (cmdQueue == null) {
                cmdQueue = new ConcurrentLinkedQueue<>();
                GLOBAL_QUEUE.put(gatewaySn, cmdQueue);
                rebuildQueue(gatewaySn);
            }
            return cmdQueue;
        }

        public static int rebuildQueue(String gatewaySn) {
            Queue<AbstractIotCmdReq> queue = getQueue(gatewaySn);
            queue.clear();
            List<AbstractIotCmdReq> list = getList(gatewaySn);
            if (list.isEmpty()) {
                return 0;
            }
            for (AbstractIotCmdReq cmdReq : list) {
                queue.add(cmdReq.clone());
            }
            return queue.size();
        }

        /**
         * Retrieves, but does not remove, the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq peekQueue(String gatewaySn) {
            return getQueue(gatewaySn).peek();
        }

        /**
         * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq pollQueue(String gatewaySn) {
            return getQueue(gatewaySn).poll();
        }

        public static int getQueueSize(String gatewaySn) {
            return getQueue(gatewaySn).size();
        }

        public static void addOneToFront(String gatewaySn, AbstractIotCmdReq cmdReq) {
            Queue<AbstractIotCmdReq> queue = getQueue(gatewaySn);
            List<AbstractIotCmdReq> newList = new LinkedList<>();
            newList.add(cmdReq);
            for (AbstractIotCmdReq req : queue) {
                newList.add(req.clone());
            }
            queue.clear();
            queue.addAll(newList);
        }
    }

    public static class WriteCmd {
        private static final ConcurrentHashMap<String, Queue<AbstractIotCmdReq>> GLOBAL_QUEUE =
                new ConcurrentHashMap<>(MAP_INITIAL_CAPACITY);

        static void deleteContext(String gatewaySn) {
            GLOBAL_QUEUE.remove(gatewaySn);
        }

        public static Queue<AbstractIotCmdReq> getQueue(String gatewaySn) {
            Queue<AbstractIotCmdReq> cmdQueue = GLOBAL_QUEUE.get(gatewaySn);
            if (cmdQueue == null) {
                cmdQueue = new ConcurrentLinkedQueue<>();
                GLOBAL_QUEUE.put(gatewaySn, cmdQueue);
            }
            return cmdQueue;
        }

        public static void addOne(String gatewaySn, AbstractIotCmdReq cmdReq) {
            getQueue(gatewaySn).add(cmdReq);
        }

        public static void addAll(String gatewaySn, List<AbstractIotCmdReq> cmdReqList) {
            getQueue(gatewaySn).addAll(cmdReqList);
        }

        /**
         * Retrieves, but does not remove, the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq peekQueue(String gatewaySn) {
            return getQueue(gatewaySn).peek();
        }

        /**
         * Retrieves and removes the head of this queue, or returns {@code null} if this queue is empty.
         *
         * @return the head of this queue, or {@code null} if this queue is empty
         */
        public static AbstractIotCmdReq pollQueue(String gatewaySn) {
            return getQueue(gatewaySn).poll();
        }

        public static int getQueueSize(String gatewaySn) {
            return getQueue(gatewaySn).size();
        }

        public static void addOneToFront(String gatewaySn, AbstractIotCmdReq cmdReq) {
            Queue<AbstractIotCmdReq> queue = getQueue(gatewaySn);
            List<AbstractIotCmdReq> newList = new LinkedList<>();
            newList.add(cmdReq);
            for (AbstractIotCmdReq req : queue) {
                newList.add(req.clone());
            }
            queue.clear();
            queue.addAll(newList);
        }
    }
}
