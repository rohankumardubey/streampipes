/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.wrapper.standalone.routing;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.routing.RawDataProcessor;
import org.apache.streampipes.wrapper.routing.SpInputCollector;

public class StandaloneReconfigurationSpInputCollector<T extends TransportProtocol> extends
        AbstractStandaloneSpInputCollector<T, RawDataProcessor>
        implements SpInputCollector {


    public StandaloneReconfigurationSpInputCollector(T protocol, TransportFormat format,
                                                     Boolean singletonEngine) throws SpRuntimeException {
        super(protocol, format, singletonEngine);
    }

    void send(RawDataProcessor rawDataProcessor, byte[] event) {
        try {
            rawDataProcessor.reconfigure(dataFormatDefinition.toMap(event));
        } catch (SpRuntimeException e) {
            e.printStackTrace();
        }
    }

}