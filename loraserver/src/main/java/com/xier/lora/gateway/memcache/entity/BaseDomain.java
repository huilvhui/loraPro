package com.xier.lora.gateway.memcache.entity;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDomain implements Serializable {

    private static final long serialVersionUID = -6506377566494049719L;

    protected Logger             log              = LoggerFactory.getLogger(this.getClass());

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    
}
