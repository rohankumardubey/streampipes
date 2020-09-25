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

package org.apache.streampipes.rest.impl.datalake;

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.message.Notification;
import org.apache.streampipes.rest.impl.AbstractRestInterface;
import org.apache.streampipes.rest.impl.datalake.model.DataResult;
import org.apache.streampipes.rest.impl.datalake.model.GroupedDataResult;
import org.apache.streampipes.rest.impl.datalake.model.PageResult;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Path("/v3/users/{username}/datalake")
public class DataLakeResourceV3 extends AbstractRestInterface {
  private DataLakeManagementV3 dataLakeManagement;

  public DataLakeResourceV3() {
    this.dataLakeManagement = new DataLakeManagementV3();
  }

  public DataLakeResourceV3(DataLakeManagementV3 dataLakeManagement) {
    this.dataLakeManagement = dataLakeManagement;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Path("/data/{index}/paging")
  public Response getPage(@PathParam("index") String index,
                          @Context UriInfo info,
                          @QueryParam("itemsPerPage") int itemsPerPage) {

    PageResult result;
    String page = info.getQueryParameters().getFirst("page");
    String pr = info.getQueryParameters().getFirst("retentionPolicy");

    try {

      if (page != null) {
        result = this.dataLakeManagement.getEvents(index, itemsPerPage, Integer.parseInt(page), pr);
      } else {
        result = this.dataLakeManagement.getEvents(index, itemsPerPage, pr);
      }
      return Response.ok(result).build();
    } catch (IOException e) {
      e.printStackTrace();

      return Response.serverError().build();
    }
  }

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/info")
  public Response getAllInfos() {
    List<DataLakeMeasure> result = this.dataLakeManagement.getInfos();

    return ok(result);
  }

  @Deprecated
  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Path("/data/{index}")
  public Response getAllData(@PathParam("index") String index,
                             @QueryParam("format") String format,
                             @Context UriInfo info) {

    String pr = info.getQueryParameters().getFirst("retentionPolicy");
    StreamingOutput streamingOutput = dataLakeManagement.getAllEvents(index, format, pr);

    return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).
            header("Content-Disposition", "attachment; filename=\"datalake." + format + "\"")
            .build();
  }

  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Path("/data/{index}/download")
  public Response downloadData(@PathParam("index") String index,
                               @QueryParam("format") String format,
                               @Context UriInfo info) {

    String pr = info.getQueryParameters().getFirst("retentionPolicy");
    StreamingOutput streamingOutput = dataLakeManagement.getAllEvents(index, format, pr);

    return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).
            header("Content-Disposition", "attachment; filename=\"datalake." + format + "\"")
            .build();
  }

  @GET
  @Path("/data/{index}/delete")
  public void deleteMeasurement(@PathParam("index") String index) {
    dataLakeManagement.deleteMeasurement(index);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/data/{index}/last/{value}/{unit}")
  public Response getAllData(@PathParam("index") String index,
                             @PathParam("value") int value,
                             @PathParam("unit") String unit,
                             @Context UriInfo info) {


    String aggregationUnit = info.getQueryParameters().getFirst("aggregationUnit");
    String aggregationValue = info.getQueryParameters().getFirst("aggregationValue");
    String pr = info.getQueryParameters().getFirst("retentionPolicy");

    DataResult result;
    try {
      if (aggregationUnit != null && aggregationValue != null) {
        result = dataLakeManagement.getEventsFromNow(index, unit, value, aggregationUnit,
                Integer.parseInt(aggregationValue), pr);
      } else {
        result = dataLakeManagement.getEventsFromNowAutoAggregation(index, unit, value, pr);
      }
      return Response.ok(result).build();
    } catch (IllegalArgumentException e) {
      return constructErrorMessage(new Notification(e.getMessage(), ""));
    } catch (ParseException e) {
      return constructErrorMessage(new Notification(e.getMessage(), ""));
    }

  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/data/{index}/{startdate}/{enddate}")
  public Response getAllData(@Context UriInfo info,
                             @PathParam("index") String index,
                             @PathParam("startdate") long startdate,
                             @PathParam("enddate") long enddate) {

    String aggregationUnit = info.getQueryParameters().getFirst("aggregationUnit");
    String aggregationValue = info.getQueryParameters().getFirst("aggregationValue");
    String pr = info.getQueryParameters().getFirst("retentionPolicy");

    DataResult result;

    try {
      if (aggregationUnit != null && aggregationValue != null) {
          result = dataLakeManagement.getEvents(index, startdate, enddate, aggregationUnit,
                  Integer.parseInt(aggregationValue), pr);

      } else {
          result = dataLakeManagement.getEventsAutoAggregation(index, startdate, enddate, pr);
      }
      return Response.ok(result).build();
    } catch (IllegalArgumentException | ParseException e) {
      return constructErrorMessage(new Notification(e.getMessage(), ""));
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/data/{index}/{startdate}/{enddate}/grouping/{groupingTag}")
  public Response getAllDataGrouping(@Context UriInfo info,
                             @PathParam("index") String index,
                             @PathParam("startdate") long startdate,
                             @PathParam("enddate") long enddate,
                             @PathParam("groupingTag") String groupingTag) {

    String aggregationUnit = info.getQueryParameters().getFirst("aggregationUnit");
    String aggregationValue = info.getQueryParameters().getFirst("aggregationValue");
    String pr = info.getQueryParameters().getFirst("retentionPolicy");

    GroupedDataResult result;
    try {
      if (aggregationUnit != null && aggregationValue != null) {
          result = dataLakeManagement.getEvents(index, startdate, enddate, aggregationUnit,
                  Integer.parseInt(aggregationValue), groupingTag, pr);
      } else {
          result = dataLakeManagement.getEventsAutoAggregation(index, startdate, enddate, groupingTag, pr);
      }
      return Response.ok(result).build();
    } catch (IllegalArgumentException | ParseException e) {
      return constructErrorMessage(new Notification(e.getMessage(), ""));
    }
  }

  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/data/delete/all")
  public Response removeAllData() {

    boolean result = dataLakeManagement.removeAllDataFromDataLake();

    return Response.ok(result).build();
  }

  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Path("/data/{index}/{startdate}/{enddate}/download")
  public Response downloadData(@PathParam("index") String index, @QueryParam("format") String format,
                               @PathParam("startdate") long start, @PathParam("enddate") long end,
                               @Context UriInfo info) {

    String pr = info.getQueryParameters().getFirst("retentionPolicy");
    StreamingOutput streamingOutput = dataLakeManagement.getAllEvents(index, format, start, end, pr);

    return Response.ok(streamingOutput, MediaType.APPLICATION_OCTET_STREAM).
            header("Content-Disposition", "attachment; filename=\"datalake." + format + "\"")
            .build();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/data/{index}/count")
  public Response getNumOfRecordsOfTable(@PathParam("index") String index,
                                         @Context UriInfo info){
      String pr = info.getQueryParameters().getFirst("retentionPolicy");
      Double numOfRecords = dataLakeManagement.getNumOfRecordsOfTable(index, pr);
      return Response.ok(numOfRecords, MediaType.TEXT_PLAIN).build();
  }

  @GET
  @Path("/data/image/{route}/file")
  @Produces("image/png")
  public Response getImage(@PathParam("route") String fileRoute) throws IOException {
    return ok(dataLakeManagement.getImage(fileRoute));
  }

  @POST
  @Path("/data/image/{route}/coco")
  public void saveImageCoco(@PathParam("route") String fileRoute, String data) throws IOException {
    dataLakeManagement.saveImageCoco(fileRoute, data);
  }

  @GET
  @Path("/data/image/{route}/coco")
  @Produces("application/json")
  public Response getImageCoco(@PathParam("route") String fileRoute) throws IOException {
    return ok(dataLakeManagement.getImageCoco(fileRoute));
  }

  @POST
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/data/{index}/{startdate}/{enddate}/labeling/{column}")
  public Response labelData(@Context UriInfo info,
                            @PathParam("index") String index,
                            @PathParam("startdate") long startdate,
                            @PathParam("enddate") long enddate,
                            @PathParam("column") String column) {

      String label = info.getQueryParameters().getFirst("label");
      String pr = info.getQueryParameters().getFirst("retentionPolicy");
      this.dataLakeManagement.updateLabels(index, column, startdate, enddate, label, pr);

      return Response.ok("Successfully updated database.", MediaType.TEXT_PLAIN).build();
  }

  @GET
  @Path("/data/{index}/default/{name}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response writeMeasurementFromDefaulToCustomRententionPolicy(@PathParam("index") String index,
          @PathParam("name") String policyRetentionName) {
      this.dataLakeManagement.writeMeasurementFromDefaulToCustomRententionPolicy(index, policyRetentionName);
      return Response.ok("Wrote measurement to other retention policy.", MediaType.TEXT_PLAIN).build();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/database/size")
  public Response getStorageSizeOfDatabase() {
    Long storageSize = dataLakeManagement.getStorageSizeOfDatabase();
    return Response.ok(storageSize.toString(), MediaType.TEXT_PLAIN).build();
  }

  @GET
  @Path("/policy/info")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getRetentionPolicies() {
    DataResult result = dataLakeManagement.getRetentionPoliciesOfDatabase();
    return Response.ok(result).build();
  }

  @GET
  @Path("/policy/{name}/delete")
  @Produces(MediaType.TEXT_PLAIN)
  public Response deleteRetentionPolicy(@PathParam("name") String policyName) {
    if (dataLakeManagement.deleteRetentionPolicy(policyName)) {
      return Response.ok("Successfully deleted the retention policy.", MediaType.TEXT_PLAIN).build();
    } else {
      return Response.serverError().build();
    }
  }

  @GET
  @Path("/policy/{name}/create")
  @Produces(MediaType.TEXT_PLAIN)
  public Response createRetentionPolicy(@PathParam("name") String policyName,
                                        @Context UriInfo info) {

    String duration = info.getQueryParameters().getFirst("duration");
    String replicationString  = info.getQueryParameters().getFirst("replication");
    String shardDuration = info.getQueryParameters().getFirst("shardDuration");

    Integer replication = 0;

    if (replicationString != null) { replication = Integer.parseInt(replicationString); }
    if (dataLakeManagement.createRetentionPolicy(policyName, duration, shardDuration, replication)) {
      return Response.ok("Successfully created retention policy.", MediaType.TEXT_PLAIN).build();
    } else {
      return Response.serverError().build();
    }
  }


  @GET
  @Path("/policy/{name}/alter")
  @Produces(MediaType.TEXT_PLAIN)
  public Response alterRetentionPolicy(@PathParam("name") String policyName,
                                        @Context UriInfo info) {

    String duration = info.getQueryParameters().getFirst("duration");
    String replicationString  = info.getQueryParameters().getFirst("replication");
    String shardDuration = info.getQueryParameters().getFirst("shardDuration");

    Integer replication = 0;

    if (replicationString != null) { replication = Integer.parseInt(replicationString); }

    if (dataLakeManagement.alterRetentionPolicy(policyName, duration, shardDuration, replication)) {
      return Response.ok("Successfully altered retention policy.", MediaType.TEXT_PLAIN).build();
    } else {
      return Response.serverError().build();
    }
  }
}
