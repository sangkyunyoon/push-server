/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package com.cosmicpush.pub.push;

import com.cosmicpush.pub.common.Push;
import com.cosmicpush.pub.common.PushType;
import com.cosmicpush.pub.internal.PushUtils;
import com.cosmicpush.pub.internal.RequestErrors;
import com.cosmicpush.pub.internal.ValidationUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.crazyyak.dev.common.BeanUtils;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

public class TwilioPush implements Push, Serializable {

  public static final PushType PUSH_TYPE = new PushType(TwilioPush.class, "twilio", "Twilio");

  private final String recipient;
  private final String message;

  private String remoteHost;
  private String remoteAddress;

  private Map<String,String> traits = new LinkedHashMap<>();

  private final String callbackUrl;

  private TwilioPush(@JsonProperty("recipient") String recipient,
                     @JsonProperty("message") String message,
                     @JsonProperty("callbackUrl") String callbackUrl,
                     @JsonProperty("remoteHost") String remoteHost,
                     @JsonProperty("remoteAddress") String remoteAddress,
                     @JsonProperty("traits") Map<String,String> traits) {
    this.recipient = recipient;
    this.message = message;
    this.callbackUrl = callbackUrl;

    this.remoteHost = remoteHost;
    this.remoteAddress = remoteAddress;

    if (traits != null) {
      this.traits.putAll(traits);
    }
  }

  @Override
  public String getCallbackUrl() {
    return callbackUrl;
  }

  @Override
  public PushType getPushType() {
    return PUSH_TYPE;
  }

  @Override
  public RequestErrors validate(RequestErrors errors) {
    ValidationUtils.requireValue(errors, recipient, "The recipient must be specified.");
    ValidationUtils.requireValue(errors, message, "The message must be specified.");
    return errors;
  }

  @Override
  public Map<String, String> getTraits() {
    return traits;
  }

  @Override
  public String getRemoteHost() {
    return remoteHost;
  }

  @Override
  public String getRemoteAddress() {
    return remoteAddress;
  }

  public static TwilioPush newPush(String recipient,String message, String callbackUrl, String...traits) {
    InetAddress remoteAddress = PushUtils.getLocalHost();
    return new TwilioPush(recipient, message, callbackUrl, remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(), BeanUtils.toMap(traits));
  }

  public static TwilioPush newPush(String recipient,String message, String callbackUrl, Map<String,String> traits) {
    InetAddress remoteAddress = PushUtils.getLocalHost();
    return new TwilioPush(recipient, message, callbackUrl, remoteAddress.getCanonicalHostName(), remoteAddress.getHostAddress(), traits);
  }
}
