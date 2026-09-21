package com.secureworld.secure.services;

import java.util.concurrent.CompletableFuture;

public interface NotificationService {

    CompletableFuture<Void> sendNotificationsAsync();
}
