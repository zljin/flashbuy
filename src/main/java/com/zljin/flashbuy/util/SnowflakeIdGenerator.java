package com.zljin.flashbuy.util;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * 雪花ID生成器
 */
public class SnowflakeIdGenerator implements IdentifierGenerator {
    
    private static final long START_TIMESTAMP = 1609459200000L; // 2021-01-01 00:00:00
    private static final long SEQUENCE_BIT = 12;
    private static final long MACHINE_BIT = 5;
    private static final long DATACENTER_BIT = 5;
    
    private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);
    
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private static final long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;
    
    private long datacenterId = 1;
    private long machineId = 1;
    private long sequence = 0L;
    private long lastTimeStamp = -1L;
    
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return nextId();
    }
    
    private synchronized long nextId() {
        long currTimeStamp = getNewTimeStamp();
        if (currTimeStamp < lastTimeStamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        
        if (currTimeStamp == lastTimeStamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currTimeStamp = getNextMill();
            }
        } else {
            sequence = 0L;
        }
        
        lastTimeStamp = currTimeStamp;
        
        return (currTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | datacenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }
    
    private long getNextMill() {
        long mill = getNewTimeStamp();
        while (mill <= lastTimeStamp) {
            mill = getNewTimeStamp();
        }
        return mill;
    }
    
    private long getNewTimeStamp() {
        return System.currentTimeMillis();
    }
}
