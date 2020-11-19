package co.haslo.excelregistermapcontrolboard.NativeProcessing;

public class NativeWrapper {

    static {
        System.loadLibrary("native-lib");
    }

    public native int[] nativeByteToIntArray(byte[] bufferArray);

    public native int[] nativeImaging(byte[] bufferArray);

    public native int[] nativeDoppler(byte[] bufferArray);

    public native double[][] nativeConvertIQ(byte[] bufferArray);

    public native double[] nativeIIRFilter(double[] doubleArray);

    public native double[][] nativeFFT(int dir, int m, double[] realArray, double[] imagArray);

    public native double[] nativeFFTMagni(double[] i_realArray, double[] i_imagArray, double[] q_realArray, double[] q_imagArray, int full);

    public native double[] nativeMagShift(double[] magniArray, int full);

    public native int[] nativeDopplerImaging(double[] magniArray);

}
