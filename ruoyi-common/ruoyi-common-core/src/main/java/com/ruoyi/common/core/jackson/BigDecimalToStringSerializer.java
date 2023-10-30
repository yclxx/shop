package com.ruoyi.common.core.jackson;

import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;

import java.math.BigDecimal;

@JacksonStdImpl
public class BigDecimalToStringSerializer extends ToStringSerializerBase {

    public final static BigDecimalToStringSerializer instance = new BigDecimalToStringSerializer();

    public BigDecimalToStringSerializer() {
        super(Object.class);
    }

    public BigDecimalToStringSerializer(Class<? extends BigDecimal> rawType) {
        super(rawType);
    }

    @Override
    public String valueToString(Object value) {
        return ((BigDecimal) value).stripTrailingZeros().toPlainString();
    }
}
