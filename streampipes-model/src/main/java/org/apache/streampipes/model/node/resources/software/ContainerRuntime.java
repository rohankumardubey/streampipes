package org.apache.streampipes.model.node.resources.software;/*
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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.fogsy.empire.annotations.RdfProperty;
import io.fogsy.empire.annotations.RdfsClass;
import org.apache.streampipes.model.base.UnnamedStreamPipesEntity;
import org.apache.streampipes.model.shared.annotation.TsModel;
import org.apache.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.CONTAINER_RUNTIME)
@Entity
@JsonSubTypes({
        @JsonSubTypes.Type(DockerContainerRuntime.class),
        @JsonSubTypes.Type(NvidiaContainerRuntime.class)
})
@TsModel
public abstract class ContainerRuntime extends UnnamedStreamPipesEntity {

    @RdfProperty(StreamPipes.HAS_CONTAINER_RUNTIME_SERVER_VERSION)
    public String serverVersion;

    @RdfProperty(StreamPipes.HAS_CONTAINER_RUNTIME_API_VERSION)
    public String apiVersion;

    public ContainerRuntime() {
        super();
    }

    public ContainerRuntime(ContainerRuntime other) {
        super(other);
    }

    public ContainerRuntime(String serverVersion, String apiVersion) {
        super();
        this.serverVersion = serverVersion;
        this.apiVersion = apiVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
}