package com.kapkiai.smpp.pool;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.EvictionConfig;
import org.apache.commons.pool2.impl.EvictionPolicy;

public class DisabledEvictionPolicy<T> implements EvictionPolicy<T> {

    @Override
    public boolean evict(EvictionConfig config, PooledObject<T> underTest, int idleCount) {
        return false;
    }
}