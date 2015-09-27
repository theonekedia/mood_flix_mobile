/**
 * 
 * @author Arshad
 * 
 * @Company Konstant
 *
 */

package hobbyst.ola.foodlove.libs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**** Class for Bitmap Compression & Memory management ****/
public class BitmapController {
	
	/**** For Decoding Image File with compression ****/
	public static Bitmap CompressFile(String path) {
		Bitmap b = null;
		File finalfile = new File(path);
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream fis = new FileInputStream(finalfile);
			BitmapFactory.decodeStream(fis, null, o);
			fis.close();
			int IMAGE_MAX_SIZE = 1200;
			int scale = 1;
			if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
				scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE
					/ (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
				if(scale < 3)
					scale = 4;
				else
					scale = 8;
			} else
				scale = 4;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			fis = new FileInputStream(finalfile);
			b = BitmapFactory.decodeStream(fis, null, o2);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
	/**** For calling Recycling the Bitmap & calling Garbage Collector ****/
	public static void clearBitmap(Bitmap bm) {
		if (bm != null) {
			bm.recycle();
			bm = null;
			System.gc();
			Runtime.getRuntime().gc();
		}
	}
}