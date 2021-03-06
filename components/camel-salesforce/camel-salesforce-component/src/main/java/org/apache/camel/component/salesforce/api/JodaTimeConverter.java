/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.salesforce.api;

import java.lang.reflect.Constructor;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * XStream converter for handling JodaTime fields.
 */
public class JodaTimeConverter implements Converter {

    private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
            .appendOffset("+HH:mm", "Z")
            .toFormatter();

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext context) {
        ZonedDateTime dateTime = (ZonedDateTime) o;
        writer.setValue(formatter.format(dateTime));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String dateTimeStr = reader.getValue();
        Class<?> requiredType = context.getRequiredType();
        try {
            return formatter.parse(dateTimeStr, ZonedDateTime::from);
        } catch (Exception e) {
            throw new ConversionException(
                    String.format("Error reading ZonedDateTime from value %s: %s",
                            dateTimeStr, e.getMessage()),
                    e);
        }
    }

    @Override
    public boolean canConvert(Class aClass) {
        return ZonedDateTime.class.isAssignableFrom(aClass);
    }

}
