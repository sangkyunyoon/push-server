/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package com.cosmicpush.plugins.smtp;

import com.cosmicpush.common.AbstractDelegate;
import com.cosmicpush.common.accounts.Account;
import com.cosmicpush.common.clients.ApiClient;
import com.cosmicpush.common.plugins.PluginContext;
import com.cosmicpush.common.requests.ApiRequest;
import com.cosmicpush.pub.common.RequestStatus;
import com.cosmicpush.pub.push.*;
import org.crazyyak.dev.common.StringUtils;
import org.crazyyak.dev.common.exceptions.ExceptionUtils;
import org.crazyyak.dev.domain.comm.AuthenticationMethod;

public class SmtpEmailDelegate extends AbstractDelegate {

  private final Account account;
  private final ApiClient apiClient;

  private final SmtpEmailPush push;
  private final SmtpEmailConfig config;

  public SmtpEmailDelegate(PluginContext context, Account account, ApiClient apiClient, ApiRequest apiRequest, SmtpEmailPush push, SmtpEmailConfig config) {
    super(context.getObjectMapper(), apiRequest, context.getApiRequestStore());
    this.push = ExceptionUtils.assertNotNull(push, "push");
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.account = ExceptionUtils.assertNotNull(account, "account");
    this.apiClient = ExceptionUtils.assertNotNull(apiClient, "apiClient");
  }

  @Override
  public synchronized RequestStatus processRequest() throws Exception {
    String reasonNotPermitted = account.getReasonNotPermitted(push);
    if (StringUtils.isNotBlank(reasonNotPermitted)) {
      return apiRequest.denyRequest(reasonNotPermitted);
    }

    String apiMessage = sendEmail();

    return apiRequest.processed(apiMessage);
  }

  /**
   * Backdoor to the SesEmailDelegate to send an email message without any validation.
   * @return The API message to log into history.
   */
  public String sendEmail() {

    String apiMessage = null;

    EmailMessage message;

    if (StringUtils.isNotBlank(config.getRecipientOverride())) {
      // This is NOT a "production" request.
      message = new EmailMessage(config.getServerName(), config.getPortNumber(), config.getRecipientOverride());
      apiMessage = String.format("Request sent to recipient override, %s.", config.getRecipientOverride());
    } else {
      // This IS a "production" request.
      message = new EmailMessage(config.getServerName(), config.getPortNumber(), push.getToAddress());
    }

    if (config.getAuthType().isTls()) {
      message.setAuthentication(AuthenticationMethod.TLS, config.getUserName(), config.getPassword());
    } else if (config.getAuthType().isSsl()) {
      message.setAuthentication(AuthenticationMethod.SSL, config.getUserName(), config.getPassword());
    } else {
      message.setAuthentication(AuthenticationMethod.NONE, config.getUserName(), config.getPassword());
    }

    message.setFrom(push.getFromAddress());
    message.send(push.getEmailSubject(), null, push.getHtmlContent());

    return apiMessage;
  }
}
