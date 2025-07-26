package com.ennew.iot.gateway.biz.gateway;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

@Slf4j
public abstract class AbstractDeviceGateway implements DeviceGateway {

    private final static AtomicReferenceFieldUpdater<AbstractDeviceGateway, GatewayState>
            STATE = AtomicReferenceFieldUpdater.newUpdater(AbstractDeviceGateway.class, GatewayState.class, "state");

    private final String id;

    private volatile GatewayState state = GatewayState.shutdown;

    public AbstractDeviceGateway(String id) {
        this.id = id;
    }

    @Override
    public final String getId() {
        return id;
    }

    @Override
    public void startup() {
        if (state == GatewayState.paused) {
            changeState(GatewayState.started);
        }
        if (state == GatewayState.started || state == GatewayState.starting) {
        }
        changeState(GatewayState.starting);
        doStartup();
        changeState(GatewayState.started);
    }

    protected abstract void doStartup();

    protected synchronized final void changeState(GatewayState target) {
        STATE.getAndSet(this, target);
    }

    @Override
    public void shutdown() {
        GatewayState old = STATE.getAndSet(this, GatewayState.shutdown);

        if (old == GatewayState.shutdown) {
            return;
        }
        changeState(GatewayState.shutdown);
        doShutdown();
    }

    protected abstract void doShutdown();

    @Override
    public void pause() {
        changeState(GatewayState.paused);
    }

    @Override
    public final GatewayState getState() {
        return state;
    }
}
