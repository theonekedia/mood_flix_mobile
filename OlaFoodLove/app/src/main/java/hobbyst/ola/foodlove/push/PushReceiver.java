/**
 * 
 * @author Arshad
 * 
 * @Company Konstant
 *
 */

package hobbyst.ola.foodlove.push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code PushReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class PushReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, PushService.class);
            context.startService(serviceIntent);
        }
		
		// Explicitly specify that PushService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(), PushService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}