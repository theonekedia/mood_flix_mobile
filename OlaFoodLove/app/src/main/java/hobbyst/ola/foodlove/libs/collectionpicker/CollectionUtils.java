/**
 * 
 * @author Arshad Parwez
 * 
 * @Company Freelancing
 *
 */

package hobbyst.ola.foodlove.libs.collectionpicker;

import android.content.Context;

public class CollectionUtils {

	public static int dpToPx(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
}