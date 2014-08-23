package com.tekpub.messaging;

import android.content.Intent;

public interface INotificationService {
	void createSimpleNotification(int notificationId, Intent intent, String marquee, String title, String message, int iconId);
}
