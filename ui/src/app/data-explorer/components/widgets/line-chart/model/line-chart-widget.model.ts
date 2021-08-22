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

import { DataExplorerWidgetModel, EventPropertyUnion } from '../../../../../core-model/gen/streampipes-model';

export interface LineChartDataConfig {
  selectedLineChartProperties: EventPropertyUnion[];
  availableProperties: EventPropertyUnion[];
  dimensionProperties: EventPropertyUnion[];
  selectedBackgroundColorProperty: EventPropertyUnion;
  availableNonNumericColumns: EventPropertyUnion[];
  advancedSettingsActive: boolean;
  aggregationValue: number;
  aggregationTimeUnit: string;
  groupValue: string;
  showCountValue: boolean;
  showBackgroundColorProperty: boolean;
  yKeys: string[];
  xKey: string;
  backgroundColorPropertyKey: string;
  labelingModeOn: boolean;
  chartMode: string;
}

export interface LineChartWidgetModel extends DataExplorerWidgetModel {
  dataConfig: LineChartDataConfig;
}