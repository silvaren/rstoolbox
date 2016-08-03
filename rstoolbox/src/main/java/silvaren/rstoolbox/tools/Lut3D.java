package silvaren.rstoolbox.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsic3DLUT;
import android.support.v8.renderscript.Type;

public class Lut3D {
    private static Allocation initSampleCube(RenderScript rs) {
        final int sx = 2;
        final int sy = 2;
        final int sz = 2;
        Type.Builder tb = new Type.Builder(rs, Element.U8_4(rs));
        tb.setX(sx);
        tb.setY(sy);
        tb.setZ(sz);
        Type t = tb.create();
        Allocation cube = Allocation.createTyped(rs, t);
        int dat[] = new int[sx * sy * sz];
        dat[0] = 0xffffffff;
        dat[7] = 0xff000000;
        cube.copyFromUnchecked(dat);

        return cube;
    }

    public static void do3dLut(Context context, Bitmap inputBitmap) {
        RSToolboxContext bitmapRSContext = RSToolboxContext.createFromBitmap(context, inputBitmap);
        Allocation aout = Allocation.createTyped(bitmapRSContext.rs, bitmapRSContext.ain.getType());

        ScriptIntrinsic3DLUT script3dLut = ScriptIntrinsic3DLUT.create(
                bitmapRSContext.rs, bitmapRSContext.ain.getElement());
        Allocation lut = initSampleCube(bitmapRSContext.rs);
        script3dLut.setLUT(lut);
        script3dLut.forEach(bitmapRSContext.ain, aout);
        aout.copyTo(inputBitmap);
    }

    public static void do3dLut(Context context, byte[] nv21ByteArray, int width, int height) {
        Bitmap srcBitmap = Nv21Image.nv21ToBitmap(nv21ByteArray, width, height);
        do3dLut(context, srcBitmap);
        Nv21Image resultNv21 = Nv21Image.convertToNV21(context, srcBitmap);
        System.arraycopy(resultNv21.nv21ByteArray,0,nv21ByteArray,0,nv21ByteArray.length);
    }
}
