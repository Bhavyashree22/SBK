/**
 * Copyright (c) KMG. All Rights Reserved..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package io.sbk.Jeromq;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import io.sbk.api.DataReader;
import io.sbk.api.DataWriter;
import io.sbk.api.ParameterOptions;
import io.sbk.api.Storage;
import io.sbk.data.DataType;
import io.sbk.data.impl.ByteArray;

import java.io.IOException;
import java.util.Objects;

/**
 * Class for Jeromq storage driver.
 *
 * Incase if your data type in other than byte[] (Byte Array)
 * then change the datatype and getDataType.
 */
public class Jeromq implements Storage<byte[]> {
    private final static String CONFIGFILE = "jeromq.properties";
    private JeromqConfig config;

    @Override
    public void addArgs(final ParameterOptions params) throws IllegalArgumentException {
        final ObjectMapper mapper = new ObjectMapper(new JavaPropsFactory())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            config = mapper.readValue(
                    Objects.requireNonNull(Jeromq.class.getClassLoader().getResourceAsStream(CONFIGFILE)),
                    JeromqConfig.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException(ex);
        }
        params.addOption("host", true, "host to connect, default host : " + config.host);
    }


    @Override
    public void parseArgs(final ParameterOptions params) throws IllegalArgumentException {
        config.host = params.getOptionValue("host", config.host);
        if (params.getReadersCount() > 1) {
            throw new IllegalArgumentException("Readers should be only 1 for JeroMQ");
        }
    }

    @Override
    public void openStorage(final ParameterOptions params) throws IOException {

    }

    @Override
    public void closeStorage(final ParameterOptions params) throws IOException {

    }

    @Override
    public DataWriter<byte[]> createWriter(final int id, final ParameterOptions params) {
        return new JeromqWriter(id, params, config);
    }

    @Override
    public DataReader<byte[]> createReader(final int id, final ParameterOptions params) {
        return new JeromqReader(id, params, config);
    }

    @Override
    public DataType<byte[]> getDataType() {
        return new ByteArray();
    }
}
