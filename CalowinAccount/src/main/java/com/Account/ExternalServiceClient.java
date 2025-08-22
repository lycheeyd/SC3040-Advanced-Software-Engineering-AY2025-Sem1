package com.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.Account.Entities.FriendStatus;

@Service
public class ExternalServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${friend.module.urlPrefix}")
    private String urlPrefix;

    public FriendStatus getFriendStatus(String selfID, String otherID) {
        try {
            String url = urlPrefix + "/friend-requests/status?userId1=" + selfID + "&userId2=" + otherID;
            return restTemplate.getForObject(url, FriendStatus.class);
        } catch (RestClientException e) {
            // Handle the error or log it
            throw new RuntimeException("Failed to retrieve friend status", e);
        }
    }

}