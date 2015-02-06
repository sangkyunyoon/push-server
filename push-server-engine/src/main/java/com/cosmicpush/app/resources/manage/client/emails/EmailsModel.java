package com.cosmicpush.app.resources.manage.client.emails;

import com.cosmicpush.common.accounts.Account;
import com.cosmicpush.common.clients.ApiClient;
import com.cosmicpush.common.requests.ApiRequest;

import java.util.*;

public class EmailsModel {
  private final Account account;
  private final ApiClient apiClient;
  private final List<ApiRequest> requests = new ArrayList<>();

  public EmailsModel(Account account, ApiClient apiClient, Collection<ApiRequest> requests) {

    this.account = account;
    this.apiClient = apiClient;

    Set<ApiRequest> sortedSet = new TreeSet<>(requests);
    List<ApiRequest> sortedList = new ArrayList<>(sortedSet);
    Collections.reverse(sortedList);
    this.requests.addAll(sortedList);
  }

  public Account getAccount() {
    return account;
  }

  public ApiClient getApiClient() {
    return apiClient;
  }

  public List<ApiRequest> getRequests() {
    return requests;
  }
}