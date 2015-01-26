/*
 * Copyright (c) 2014 Jacob D. Parr
 *
 * This software may not be used without permission.
 */

package com.cosmicpush.app.resources.manage.client;

import com.cosmicpush.app.resources.manage.UserRequestConfig;
import com.cosmicpush.common.accounts.Account;
import com.cosmicpush.common.clients.ApiClient;
import com.cosmicpush.common.plugins.Plugin;
import com.cosmicpush.common.system.PluginManager;
import com.cosmicpush.pub.common.PushType;
import java.net.URI;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.crazyyak.dev.common.exceptions.ExceptionUtils;
import org.crazyyak.dev.common.net.InetMediaType;

public class ManagePluginApi {

  private final PushType pushType;
  private final Account account;
  private final ApiClient apiClient;
  private final UserRequestConfig config;

  public ManagePluginApi(UserRequestConfig config, Account account, ApiClient apiClient, PushType pushType) {
    this.pushType = ExceptionUtils.assertNotNull(pushType, "pushType");
    this.config = ExceptionUtils.assertNotNull(config, "config");
    this.account = ExceptionUtils.assertNotNull(account, "account");
    this.apiClient = ExceptionUtils.assertNotNull(apiClient, "apiClient");
  }

  public Response redirect() throws Exception {
    String path = String.format("manage/api-client/%s", apiClient.getClientName());
    return Response.seeOther(new URI(path)).build();
  }

  @POST
  public Response updateConfig(MultivaluedMap<String, String> formParams) throws Exception {
    Plugin plugin = PluginManager.getPlugin(pushType);
    plugin.updateConfig(config, account, apiClient, formParams);
    return redirect();
  }

  @POST
  @Path("/delete")
  public Response deleteConfig() throws Exception {
    Plugin plugin = PluginManager.getPlugin(pushType);
    plugin.deleteConfig(config, account, apiClient);
    return redirect();
  }

  @POST
  @Path("/test")
  public Response testConfig() throws Exception {
    Plugin plugin = PluginManager.getPlugin(pushType);
    plugin.test(config, account, apiClient);
    return redirect();
  }
}
