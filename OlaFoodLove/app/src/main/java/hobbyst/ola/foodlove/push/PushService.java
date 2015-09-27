/**
 * 
 * @author Arshad
 * 
 * @Company Konstant
 *
 */

package hobbyst.ola.foodlove.push;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import hobbyst.ola.foodlove.FoodLoveApplication;
import hobbyst.ola.foodlove.MainActivity;
import hobbyst.ola.foodlove.R;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class PushService extends IntentService {
	private NotificationManager manager;
	private NotificationCompat.Builder builder;
	
	public PushService() {
		super("PushService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The intent parameter must be the intent you received in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		try {
			if (!extras.isEmpty()) {
				// has effect of unparcelling Bundle
				/*
				 * Filter messages based on message type. Since it is likely that
				 * GCM will be extended in the future with new message types, just
				 * ignore any message types you're not interested in, or that you
				 * don't recognise.
				 */
				
				if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
					// This represents the service doing some work.
					// Post notification of received message.
					sendNotification(PushService.this, extras);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		PushReceiver.completeWakefulIntent(intent);
	}

	/* 
	 * Put the message into a notification and post it.
	 * This is just one simple example of what you might choose to do with a GCM message. 
	 */

	public void sendNotification(Context con, Bundle msg) {
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent notificationIntent = new Intent(this, MainActivity.class);
		if (FoodLoveApplication.myPrefs.contains("user_id")) {
    		if(!FoodLoveApplication.myPrefs.getString("user_id", "0").equals("0"))
    			notificationIntent.putExtra("push", true);
    		else
    			notificationIntent.putExtra("push", false);
		} else
			notificationIntent.putExtra("push", false);
		
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

	    builder = new NotificationCompat.Builder(this)
	    	.setSmallIcon(R.mipmap.ic_logo)
	    	.setContentTitle(getString(R.string.app_name))
	    	.setContentText(msg.getString("message"))
	    	.setTicker("Ola Mood Flix - " + msg.getString("message"))
	    	.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
	    	.setAutoCancel(true)
	    	.setContentIntent(pendingIntent);
	    
	    Log.d("Push Message = ", msg.toString());

		manager.notify(0, builder.build());
	}
}