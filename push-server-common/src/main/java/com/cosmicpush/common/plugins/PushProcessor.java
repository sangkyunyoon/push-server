package com.cosmicpush.common.plugins;

import com.cosmicpush.common.AbstractDelegate;
import com.cosmicpush.common.clients.Domain;
import com.cosmicpush.common.requests.PushRequest;
import com.cosmicpush.common.system.PluginManager;
import com.cosmicpush.pub.common.Push;
import com.cosmicpush.pub.common.PushResponse;

public class PushProcessor {

  private final PluginContext context;

  public PushProcessor(PluginContext context) {
    this.context = context;
  }

  public PushResponse execute(int apiVersion, Domain domain, Push push) {

    // TODO - validate the remoteHost and remoteAddress specifeid in the push as really coming from them.

    PushRequest pushRequest = new PushRequest(apiVersion, domain, push);
    context.getPushRequestStore().create(pushRequest);

    Plugin plugin = PluginManager.getPlugin(push.getPushType());
    AbstractDelegate delegate = plugin.newDelegate(context, domain, pushRequest, push);

    delegate.start();

    return new PushResponse(
        domain.getDomainId(),
        pushRequest.getPushRequestId(),
        pushRequest.getCreatedAt(),
        pushRequest.getRequestStatus(),
        pushRequest.getNotes()
    );
  }
}
