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

import { Component, Input } from '@angular/core';
import { EventSchema } from '@streampipes/platform-services';
import { SemanticTypeUtilsService } from '../../../core-services/semantic-type/semantic-type-utils.service';

@Component({
    selector: 'sp-live-preview-table',
    templateUrl: './live-preview-table.component.html',
    styleUrls: ['./live-preview-table.component.scss'],
})
export class LivePreviewTableComponent {
    @Input()
    eventSchema: EventSchema;

    @Input()
    runtimeData: { runtimeName: string; value: any }[];

    displayedColumns: string[] = ['runtimeName', 'value'];

    constructor(private semanticTypeUtilsService: SemanticTypeUtilsService) {}

    isImage(runtimeName: string) {
        const property = this.getProperty(runtimeName);
        return this.semanticTypeUtilsService.isImage(property);
    }

    isTimestamp(runtimeName: string) {
        const property = this.getProperty(runtimeName);
        return this.semanticTypeUtilsService.isTimestamp(property);
    }

    hasNoDomainProperty(runtimeName: string) {
        return !(this.isTimestamp(runtimeName) || this.isImage(runtimeName));
    }

    getProperty(runtimeName: string) {
        return this.eventSchema.eventProperties.find(
            property => property.runtimeName === runtimeName,
        );
    }
}
