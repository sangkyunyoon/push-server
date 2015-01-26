/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package com.cosmicpush.common.requests;

import com.cosmicpush.common.clients.ApiClient;
import com.cosmicpush.pub.common.*;
import com.cosmicpush.pub.internal.CpIdGenerator;
import com.cosmicpush.pub.push.*;
import com.couchace.annotations.*;
import com.fasterxml.jackson.annotation.*;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.crazyyak.dev.common.*;
import org.crazyyak.dev.common.exceptions.ExceptionUtils;

@CouchEntity(ApiRequestStore.API_REQUEST_DESIGN_NAME)
public class ApiRequest implements Comparable<ApiRequest> {

  private final String apiRequestId;
  private final String revision;

  private final String apiClientId;

  private final LocalDateTime createdAt;
  private RequestStatus requestStatus;

  private final String ipAddress;
  private final String remoteHost;

  private final PushType pushType;

  private final List<String> notes = new ArrayList<>();

  private final Push push;

  @JsonCreator
  private ApiRequest(
      @JacksonInject("apiRequestId") String apiRequestId,
      @JacksonInject("revision") String revision,

      @JsonProperty("apiClientId") String apiClientId,

      @JsonProperty("createdAt") LocalDateTime createdAt,
      @JsonProperty("requestStatus") RequestStatus requestStatus,

      @JsonProperty("ipAddress") String ipAddress,
      @JsonProperty("remoteHost") String remoteHost,

      @JsonProperty("pushType") PushType pushType,
      @JsonProperty("notes") List<String> notes,
      @JsonProperty("push") Push push) {

    this.apiRequestId = apiRequestId;
    this.revision = revision;

    this.apiClientId = apiClientId;

    this.createdAt = createdAt;
    this.requestStatus = requestStatus;

    this.ipAddress = ipAddress;
    this.remoteHost = remoteHost;

    this.pushType = pushType;

    this.notes.addAll(notes);

    this.push = push;
  }

  public ApiRequest(ApiClient apiClient, Push push, InetAddress inetAddress) {
    this.apiRequestId = CpIdGenerator.newId();
    this.revision = null;
    this.apiClientId = apiClient.getApiClientId();

    this.createdAt = DateUtils.currentDateTime();
    this.requestStatus = RequestStatus.pending;

    this.ipAddress = inetAddress.getHostAddress();
    this.remoteHost = inetAddress.getCanonicalHostName();
    this.pushType = push.getPushType();

    this.push = push;
  }

  @CouchId
  public String getApiRequestId() {
    return apiRequestId;
  }

  @CouchRevision
  public String getRevision() {
    return revision;
  }

  public String getApiClientId() {
    return apiClientId;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getRemoteHost() {
    return remoteHost;
  }

  public PushType getPushType() {
    return pushType;
  }

  public String getCreatedAt(String format) {
    return (createdAt == null) ? null : createdAt.format(DateTimeFormatter.ofPattern(format));
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * Provided for Backwards compatibility with Java 7 libraries.
   * @return The value of createdAt as a java.util.Date
   */
  @JsonIgnore
  public Date getCreatedAtUtilDate() {
    return DateUtils.toUtilDate(createdAt);
  }

  public RequestStatus getRequestStatus() {
    return requestStatus;
  }

  public List<String> getNotes() {
    return Collections.unmodifiableList(notes);
  }
  public void addNote(String note) {
    notes.add(note);
  }

  public RequestStatus processed() {
    return processed(null);
  }

  public RequestStatus processed(String note) {
    this.requestStatus = RequestStatus.processed;
    this.notes.add("Request has been processed.");

    if (StringUtils.isNotBlank(note)) {
      this.notes.add(note);
    }

    return this.requestStatus;
  }

  public RequestStatus denyRequest(String reasonNotPermitted) {
    this.requestStatus = RequestStatus.denied;
    this.notes.add("Request denied: " + reasonNotPermitted);

    return this.requestStatus;
  }

  public RequestStatus failed(String message) {
    this.requestStatus = RequestStatus.failed;

    if (StringUtils.isBlank(message)) {
      this.notes.add("*** FAILURE - Reason unspecified **");
    } else {
      this.notes.add("*** FAILURE ***");
      this.notes.add(message);
    }

    return this.requestStatus;
  }

  public RequestStatus failed(Exception ex) {
    this.requestStatus = RequestStatus.failed;
    this.notes.add("*** FAILURE ***");

    for (Throwable throwable : ExceptionUtils.getRootCauses(ex)) {
      this.notes.add(ExceptionUtils.getMessage(throwable));
    }

    return this.requestStatus;
  }

  public RequestStatus warn(String message) {
    this.requestStatus = RequestStatus.warning;

    if (StringUtils.isBlank(message)) {
      this.notes.add("*** WARNING - Reason unspecified **");
    } else {
      this.notes.add("*** WARNING ***");
      this.notes.add(message);
    }

    return this.requestStatus;
  }

  public RequestStatus warn(Throwable e) {
    this.requestStatus = RequestStatus.warning;
    this.notes.add("*** WARNING ***");

    for (Throwable throwable : ExceptionUtils.getRootCauses(e)) {
      this.notes.add(ExceptionUtils.getMessage(throwable));
    }

    return this.requestStatus;
  }

  public Push getPush() {
    return push;
  }

  @JsonIgnore
  public EmailPush getEmailPush() {
    return (push instanceof EmailPush) ? (EmailPush)push : null;
  }

  @JsonIgnore
  public NotificationPush getNotificationPush() {
    return (push instanceof NotificationPush) ? (NotificationPush)push : null;
  }

  @JsonIgnore
  public UserEventPush getUserEventPush() {
    return (push instanceof UserEventPush) ? (UserEventPush)push : null;
  }

  public boolean equals(Object object) {
    if (object instanceof ApiRequest) {
      ApiRequest that = (ApiRequest)object;
      return this.apiRequestId.equals(that.apiRequestId);
    }
    return false;
  }

  @Override
  public int compareTo(ApiRequest that) {
    return this.createdAt.compareTo(that.createdAt);
  }

  public String toString() {
    return push.getPushType().getLabel() + ": " + push.toString();
  }
}
