package com.ennew.iot.gateway.biz.concurrent;

/**
 * @author hanyilong@enn.cn
 * @since 2021-02-14 18:51:47
 */
public interface CallbackTask<R> {
    R execute() throws Exception;

    void onBack(R r);

    void onException(Throwable t);
}
