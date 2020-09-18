package co.haslo.excelregistermapcontrolboard.NativeProcessing;

public class NativeWrapper {

    static {
        System.loadLibrary("native-lib");
    }

    public native int[] nativeByteToIntArray(byte[] bufferArray);

    public native int[] nativeImaging(byte[] bufferArray);

}
