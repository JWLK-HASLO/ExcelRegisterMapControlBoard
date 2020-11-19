//
// Created by urxtion on 9/18/2020.
//

#include "co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper.h"
#include <cstdio>
#include <cstdlib>
#include <cmath>
#include <iostream>
#include <algorithm>
#include <array>
#include <fstream>

using namespace std;

#define N_DATA 128
#define N_FFT 256

/**
 * Imaging Value
 */
int bit_width = 16;
jdouble maxVal = pow(2, 26);
jdouble maxVal_dpplr = pow(2, 16);
int dynamic_range = 30;

//*/ B-Mode Value
jbyte bMode_Data03;
jbyte bMode_Data02;
jbyte bMode_Data01;
jbyte bMode_Data00;
jint bMode_convertBuffer = 0;
jdouble bMode_dataInphase = 0.0;
jdouble bMode_dataQuad = 0.0;
jdouble bMode_convertInphase = 0.0;
jdouble bMode_convertQuad = 0.0;
jdouble bMode_convertMag = 0.0;
jdouble bMode_findHighLow = 0.0;
jdouble bMode_finalData = 0.0;

//*/ D-Mode Value
array<double, 4> aData = { 1, -2.686157396548143, 2.419655110966472, -0.730165345305723 };
array<double, 4> bData = { 0.854497231602542, -2.563491694807627, 2.563491694807627, -0.854497231602542 };
jbyte dMode_Data03;
jbyte dMode_Data02;
jbyte dMode_Data01;
jbyte dMode_Data00;
jint dMode_convertBuffer = 0;
jdouble dMode_dataInphase = 0.0;
jdouble dMode_dataQuad = 0.0;
jdouble dMode_convertInphase = 0.0;
jdouble dMode_convertQuad = 0.0;
jdouble dMode_convertMag = 0.0;
jdouble dMode_findHighLow = 0.0;
jdouble dMode_finalData = 0.0;


//N_DATA = 128;
//N_FFT = 256;
array<double, 128> I_Data;
array<double, 128> Q_Data;
double resultIIR[128] = { 0 };
double* I_Data_customHPF;
double* Q_Data_customHPF;
double I_real[512] = { 0 };
double I_imag[512] = { 0 };
double Q_real[512] = { 0 };
double Q_imag[512] = { 0 };
double Magni_result[512] = { 0 };
int logConvert[512] = { 0 };

double convertDoubleArrayInphase_IIR[N_DATA];
double convertDoubleArrayQuadrature_IIR[N_DATA];

double convertDoubleArrayInphase_FFT[2][N_FFT];
double convertDoubleArrayQuadrature_FFT[2][N_FFT];

double resultMagnitude[N_FFT] ;

double* iirFilter(array<double, 4>, array<double, 4>, array<double, 128>);
bool FFT(short int, long, double*, double*);
bool FFTShift(double*, double*, int);
double* FFTMagni(double*, double*, double*, double*, int);
int* logComp(double*, int );


extern "C"
JNIEXPORT jintArray JNICALL Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeByteToIntArray
(JNIEnv *env, jobject, jbyteArray buffer_array) {
    int size = env -> GetArrayLength(buffer_array);
    printf("In JNI >> new int array : %d\n", size);
    jbyte * byteBuffer;
    byteBuffer = env -> GetByteArrayElements(buffer_array, NULL);
    jint * intBuffer = (jint *)malloc(sizeof(jint) * size/4);
    jintArray resultIntArray = env->NewIntArray(size/4);

    for(int i = 0, counter = 0; i < size; i+=4, counter++) {
        jbyte Data03 = byteBuffer[i+3];
        jbyte Data02 = byteBuffer[i+2];
        jbyte Data01 = byteBuffer[i+1];
        jbyte Data00 = byteBuffer[i+0];
        intBuffer[counter] = Data00 << 24 | (Data01 & 0xFF) << 16 | (Data02 & 0xFF) << 8 | (Data03 & 0xFF);
    }
    env->SetIntArrayRegion(resultIntArray, 0, size/4, (const jint *)intBuffer);
    env->ReleaseByteArrayElements(buffer_array, byteBuffer, 0);
    free(intBuffer);
    return resultIntArray;
}

extern "C"
JNIEXPORT jintArray JNICALL Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeImaging
(JNIEnv *env, jobject thiz, jbyteArray buffer_array) {

    int size = env -> GetArrayLength(buffer_array);
    jbyte * byteBuffer;
    byteBuffer = env -> GetByteArrayElements(buffer_array, NULL);

    jint * intBuffer = (jint *)malloc(sizeof(jint) * size/4);
    jintArray resultIntArray = env->NewIntArray(size/4);

    for(int i = 0, counter = 0; i < size; i+=4, counter++) {
        bMode_Data03 = byteBuffer[i+3];
        bMode_Data02 = byteBuffer[i+2];
        bMode_Data01 = byteBuffer[i+1];
        bMode_Data00 = byteBuffer[i+0];
        bMode_convertBuffer = bMode_Data03 << 24 | (bMode_Data02 & 0xFF) << 16 | (bMode_Data01 & 0xFF) << 8 | (bMode_Data00 & 0xFF);
        bMode_dataInphase = floor(bMode_convertBuffer/pow(2,16));// I = floor(result_mj/2^16);
        bMode_dataQuad = bMode_convertBuffer - (bMode_dataInphase  * pow(2, 16)); // Q = result_mj-I*2^16;

        bMode_convertInphase = ( bMode_dataInphase >= pow( 2, bit_width-1) ) ? ( bMode_dataInphase - pow(2, bit_width) ) : bMode_dataInphase;
        bMode_convertQuad = ( bMode_dataQuad >= pow( 2, bit_width-1) ) ? ( bMode_dataQuad - pow(2, bit_width) ) : bMode_convertQuad;

        bMode_convertMag = pow(bMode_convertInphase , 2) + pow(bMode_convertQuad, 2);
        bMode_findHighLow = (10*log10(bMode_convertMag/maxVal) + dynamic_range);
        bMode_finalData = max(bMode_findHighLow, 0.0) / dynamic_range * 255;
        intBuffer[counter] =  (int) bMode_finalData;
    }

    env->SetIntArrayRegion(resultIntArray, 0, size/4, (const jint *)intBuffer);
    env->ReleaseByteArrayElements(buffer_array, byteBuffer, 0);
    free(intBuffer);
    return resultIntArray;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeDoppler(JNIEnv *env,
                                                                                        jobject thiz,
                                                                                        jbyteArray buffer_array) {
    // TODO: implement nativeDoppler()

    int size = env -> GetArrayLength(buffer_array); // 128 * 4
    jbyte * byteBuffer;
    byteBuffer = env -> GetByteArrayElements(buffer_array, NULL);
    jint * intBuffer;
    jintArray resultIntArray = env->NewIntArray(256);

    for(int i = 0, counter = 0; i < size; i+=4, counter++) {
        dMode_Data03 = byteBuffer[i+3];
        dMode_Data02 = byteBuffer[i+2];
        dMode_Data01 = byteBuffer[i+1];
        dMode_Data00 = byteBuffer[i+0];
        dMode_convertBuffer = dMode_Data03 << 24 | (dMode_Data02 & 0xFF) << 16 | (dMode_Data01 & 0xFF) << 8 | (dMode_Data00 & 0xFF);

        dMode_dataInphase = floor(dMode_convertBuffer/pow(2,16));// I = floor(result_mj/2^16);
        dMode_dataQuad = dMode_convertBuffer - (dMode_dataInphase  * pow(2, 16)); // Q = result_mj-I*2^16;

        I_Data[counter] = ( dMode_dataInphase >= pow( 2, bit_width-1) ) ? ( dMode_dataInphase - pow(2, bit_width) ) : dMode_dataInphase;
        Q_Data[counter] = ( dMode_dataQuad >= pow( 2, bit_width-1) ) ? ( dMode_dataQuad - pow(2, bit_width) ) : dMode_convertQuad;
    }

    I_Data_customHPF = iirFilter(bData, aData, I_Data);
    Q_Data_customHPF = iirFilter(bData, aData, Q_Data);
    copy(I_Data_customHPF, I_Data_customHPF+128, I_real);
    copy(Q_Data_customHPF, Q_Data_customHPF+128, Q_real);

    FFT(-1, 9, I_real, I_imag);
    FFT(-1, 9, Q_real, Q_imag);
    FFTShift(I_real, I_imag, sizeof(I_real) / sizeof(double));
    FFTShift(Q_real, Q_imag, sizeof(Q_real) / sizeof(double));
    FFTMagni(I_real, I_imag, Q_real, Q_imag, sizeof(I_real) / sizeof(double));

    intBuffer = logComp(Magni_result, 256);
    env->SetIntArrayRegion(resultIntArray, 0, 256, (const jint *)intBuffer);

    env->ReleaseByteArrayElements(buffer_array, byteBuffer, 0);
    env->ReleaseIntArrayElements(resultIntArray, intBuffer, 0);

    free(byteBuffer);
    free(intBuffer);

    return resultIntArray;
}
double* iirFilter(array<double, 4> b, array<double, 4> a, array<double, 128> X)
{
    array<double, 128> z = { 0 };
    for (int m = 0; m < X.size(); m++) {
        resultIIR[m] = b[0] * X[m] + z[0];
        for (int i = 1; i < a.size(); i++) {
            z[i - 1] = b[i] * X[m] + z[i] - a[i] * resultIIR[m];
        }
    }
    return &resultIIR[0];
}

bool FFTShift(double* x, double* y, int full)
{
    double* x_temp = (double*) malloc(full * sizeof(double));
    double* y_temp = (double*)malloc(full * sizeof(double));
    //cout << "X_Size : " << full << "\n\r";
    int half = full / 2;

    if (full % 2 == 0)
    {
        copy(x, x + half, x_temp);
        copy(x + half, x + full, x);
        copy(x_temp, x_temp + half, x + half);

        copy(y, y + half, y_temp);
        copy(y + half, y + full, y);
        copy(y_temp, y_temp + half, y + half);

    }
    else
    {
        return false;
    }

    free(x_temp);
    free(y_temp);

    return true;
}

bool FFT(short int dir, long m, double* x, double* y)
{
    long n, i, i1, j, k, i2, l, l1, l2;
    double c1, c2, tx, ty, t1, t2, u1, u2, z;

    /* Calculate the number of points */
    n = 1;
    for (i = 0;i < m;i++)
        n *= 2;

    /* Do the bit reversal */
    i2 = n >> 1;
    j = 0;
    for (i = 0;i < n - 1;i++) {
        if (i < j) {
            tx = x[i];
            ty = y[i];
            x[i] = x[j];
            y[i] = y[j];
            x[j] = tx;
            y[j] = ty;
        }
        k = i2;
        while (k <= j) {
            j -= k;
            k >>= 1;
        }
        j += k;
    }

    /* Compute the FFT */
    c1 = -1.0;
    c2 = 0.0;
    l2 = 1;
    for (l = 0;l < m;l++) {
        l1 = l2;
        l2 <<= 1;
        u1 = 1.0;
        u2 = 0.0;
        for (j = 0;j < l1;j++) {
            for (i = j;i < n;i += l2) {
                i1 = i + l1;
                t1 = u1 * x[i1] - u2 * y[i1];
                t2 = u1 * y[i1] + u2 * x[i1];
                x[i1] = x[i] - t1;
                y[i1] = y[i] - t2;
                x[i] += t1;
                y[i] += t2;
            }
            z = u1 * c1 - u2 * c2;
            u2 = u1 * c2 + u2 * c1;
            u1 = z;
        }
        c2 = sqrt((1.0 - c1) / 2.0);
        if (dir == -1)
            c2 = -c2;
        c1 = sqrt((1.0 + c1) / 2.0);
    }

    /* Scaling for forward transform */
    if (dir == 1) {
        for (i = 0;i < n;i++) {
            x[i] /= n;
            y[i] /= n;
        }
    }

    return true;
}

double* FFTMagni(double* i_real, double* i_imag, double* q_real, double* q_imag, int full)
{
    for (int i = 0; i < full; i++) {
        Magni_result[i] = sqrt(pow(i_real[i] - q_imag[i], 2) + pow(i_imag[i] + q_real[i], 2));
    }

    return &Magni_result[0];
}

int* logComp(double* magni, int full)
{
    double midConvert;
    for (int i = 0; i < full; i++) {
        midConvert = (20*log10(magni[i]/maxVal) + dynamic_range);
        logConvert[i] = max(midConvert, 0.0) / dynamic_range * 255;
    }

    return &logConvert[0];
}


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeConvertIQ(
        JNIEnv *env, jobject thiz, jbyteArray buffer_array) {
    // TODO: implement nativeConvertIQ()
    int size = env -> GetArrayLength(buffer_array); // 128 * 4
    jbyte * byteBuffer;
    byteBuffer = env -> GetByteArrayElements(buffer_array, NULL);

    jdouble * inphaseBuffer = (jdouble *)malloc(sizeof(jdouble) * size/4);
    jdoubleArray inphaseArray = env->NewDoubleArray(size/4);

    jdouble * quadBuffer = (jdouble *)malloc(sizeof(jdouble) * size/4);
    jdoubleArray quadArray = env->NewDoubleArray(size/4);

    jclass doubleArrayClass = env->FindClass("[D");
    jobjectArray resultDoubleArray = env->NewObjectArray(2, doubleArrayClass, NULL);

    for(int i = 0, counter = 0; i < size; i+=4, counter++) {
        dMode_Data03 = byteBuffer[i+3];
        dMode_Data02 = byteBuffer[i+2];
        dMode_Data01 = byteBuffer[i+1];
        dMode_Data00 = byteBuffer[i+0];
        dMode_convertBuffer = dMode_Data03 << 24 | (dMode_Data02 & 0xFF) << 16 | (dMode_Data01 & 0xFF) << 8 | (dMode_Data00 & 0xFF);

        dMode_dataInphase = floor(dMode_convertBuffer/pow(2,16));// I = floor(result_mj/2^16);
        dMode_dataQuad = dMode_convertBuffer - (dMode_dataInphase  * pow(2, 16)); // Q = result_mj-I*2^16;

        inphaseBuffer[counter] = ( dMode_dataInphase >= pow( 2, bit_width-1) ) ? ( dMode_dataInphase - pow(2, bit_width) ) : dMode_dataInphase;
        quadBuffer[counter] = ( dMode_dataQuad >= pow( 2, bit_width-1) ) ? ( dMode_dataQuad - pow(2, bit_width) ) : dMode_convertQuad;
    }

    env->SetDoubleArrayRegion(inphaseArray, 0, size/4, (const jdouble *)inphaseBuffer);
    env->SetDoubleArrayRegion(quadArray, 0, size/4, (const jdouble *)quadBuffer);


    env->SetObjectArrayElement(resultDoubleArray , 0, inphaseArray);
    env->SetObjectArrayElement(resultDoubleArray , 1, quadArray);

    env->ReleaseDoubleArrayElements(inphaseArray, inphaseBuffer, 0);
    env->ReleaseDoubleArrayElements(quadArray, quadBuffer, 0);
    env->ReleaseByteArrayElements(buffer_array, byteBuffer, 0);

    return resultDoubleArray;
}


extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeIIRFilter
        (JNIEnv *env, jobject thiz, jdoubleArray double_array) {

    int size = env -> GetArrayLength(double_array);
    jdouble * doubleBuffer;
    doubleBuffer = env -> GetDoubleArrayElements(double_array, NULL);
    jdouble * doubleResultBuffer = (jdouble *)malloc(sizeof(jdouble) * size);
    jdoubleArray resultDoubleArray = env->NewDoubleArray(size);

    array<double, 128> z = { 0 };
    for (int m = 0; m < size; m++) {
        doubleResultBuffer[m] = bData[0] * doubleBuffer[m] + z[0];
        for (int i = 1; i < aData.size(); i++) {
            z[i - 1] = bData[i] * doubleBuffer[m] + z[i] - aData[i] * doubleResultBuffer[m];
        }
    }
    env->SetDoubleArrayRegion(resultDoubleArray, 0, size, (const jdouble *)doubleResultBuffer);
    env->ReleaseDoubleArrayElements(double_array, doubleBuffer, 0);
    free(doubleResultBuffer);
    return resultDoubleArray;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeFFT(JNIEnv *env, jobject thiz, jint dir, jint m,
                                                   jdoubleArray real_array,
                                                   jdoubleArray imag_array) {
    int size = env -> GetArrayLength(real_array);
    jdouble * realBuffer;
    realBuffer = env -> GetDoubleArrayElements(real_array, NULL);

    jdouble * imagBuffer;
    imagBuffer = env -> GetDoubleArrayElements(imag_array, NULL);

    jclass doubleArrayClass = env->FindClass("[D");
    jobjectArray resultDoubleArray = env->NewObjectArray(2, doubleArrayClass, NULL);


    int n, i, i1, j, k, i2, l, l1, l2;
    jdouble c1, c2, tx, ty, t1, t2, u1, u2, z;

    /* Calculate the number of points */
    n = 1;
    for (i = 0;i < m;i++)
        n *= 2;

    /* Do the bit reversal */
    i2 = n >> 1;
    j = 0;
    for (i = 0;i < n - 1;i++) {
        if (i < j) {
            tx = realBuffer[i];
            ty = imagBuffer[i];
            realBuffer[i] = realBuffer[j];
            imagBuffer[i] = imagBuffer[j];
            realBuffer[j] = tx;
            imagBuffer[j] = ty;
        }
        k = i2;
        while (k <= j) {
            j -= k;
            k >>= 1;
        }
        j += k;
    }

    /* Compute the FFT */
    c1 = -1.0;
    c2 = 0.0;
    l2 = 1;
    for (l = 0;l < m;l++) {
        l1 = l2;
        l2 <<= 1;
        u1 = 1.0;
        u2 = 0.0;
        for (j = 0;j < l1;j++) {
            for (i = j;i < n;i += l2) {
                i1 = i + l1;
                t1 = u1 * realBuffer[i1] - u2 * imagBuffer[i1];
                t2 = u1 * imagBuffer[i1] + u2 * realBuffer[i1];
                realBuffer[i1] = realBuffer[i] - t1;
                imagBuffer[i1] = imagBuffer[i] - t2;
                realBuffer[i] += t1;
                imagBuffer[i] += t2;
            }
            z = u1 * c1 - u2 * c2;
            u2 = u1 * c2 + u2 * c1;
            u1 = z;
        }
        c2 = sqrt((1.0 - c1) / 2.0);
        if (dir == -1)
            c2 = -c2;
        c1 = sqrt((1.0 + c1) / 2.0);
    }

    /* Scaling for forward transform */
    if (dir == 1) {
        for (i = 0;i < n;i++) {
            realBuffer[i] /= n;
            imagBuffer[i] /= n;
        }
    }

    env->SetDoubleArrayRegion(real_array, 0, size, (const jdouble *)realBuffer);
    env->SetDoubleArrayRegion(imag_array, 0, size, (const jdouble *)imagBuffer);


    env->SetObjectArrayElement(resultDoubleArray , 0, real_array);
    env->SetObjectArrayElement(resultDoubleArray , 1, imag_array);

    env->ReleaseDoubleArrayElements(real_array, realBuffer, 0);
    env->ReleaseDoubleArrayElements(imag_array, imagBuffer, 0);

    return resultDoubleArray;
}

extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeFFTMagni(JNIEnv *env, jobject thiz,
                                                        jdoubleArray i_real_array,
                                                        jdoubleArray i_imag_array,
                                                        jdoubleArray q_real_array,
                                                        jdoubleArray q_imag_array, jint full) {

    jdouble * magniResult = (jdouble *)malloc(sizeof(jdouble) * full);
    jdoubleArray resultMagDoubleArray = env->NewDoubleArray(full);

    jdouble * i_realBuffer;
    i_realBuffer = env -> GetDoubleArrayElements(i_real_array, NULL);

    jdouble * i_imagBuffer;
    i_imagBuffer = env -> GetDoubleArrayElements(i_imag_array, NULL);

    jdouble * q_realBuffer;
    q_realBuffer = env -> GetDoubleArrayElements(q_real_array, NULL);

    jdouble * q_imagBuffer;
    q_imagBuffer = env -> GetDoubleArrayElements(q_imag_array, NULL);

    for (int i = 0; i < full; i++) {
        magniResult[i] = sqrt(pow(i_realBuffer[i] - q_imagBuffer[i], 2) + pow(i_imagBuffer[i] + q_realBuffer[i], 2));
    }

    env->SetDoubleArrayRegion(resultMagDoubleArray, 0, full, (const jdouble *)magniResult);

    env->ReleaseDoubleArrayElements(i_real_array, i_realBuffer, 0);
    env->ReleaseDoubleArrayElements(i_imag_array, i_imagBuffer, 0);
    env->ReleaseDoubleArrayElements(q_real_array, q_realBuffer, 0);
    env->ReleaseDoubleArrayElements(q_imag_array, q_imagBuffer, 0);

    free(magniResult);

    return resultMagDoubleArray;
}

extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeMagShift(
        JNIEnv *env, jobject thiz, jdoubleArray magni_array, jint full) {
    // TODO: implement nativeMagShift()
    jdouble * magniBuffer;
    magniBuffer = env -> GetDoubleArrayElements(magni_array, NULL);
    auto * magniTemp = (jdouble *)malloc(sizeof(jdouble) * full);

    copy(magniBuffer, magniBuffer + full/2, magniTemp);
    copy(magniBuffer + full/2, magniBuffer + full, magniBuffer);
    copy(magniTemp, magniTemp + full/2, magniBuffer + full/2);

    env->SetDoubleArrayRegion(magni_array, 0, full, (const jdouble *)magniBuffer);
    env->ReleaseDoubleArrayElements(magni_array, magniBuffer, 0);
    free(magniTemp);

    return magni_array;
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeDopplerImaging(
        JNIEnv *env, jobject thiz, jdoubleArray magni_array) {
    // TODO: implement nativeDopplerImaging()
    int size = env -> GetArrayLength(magni_array);
    double midConvert;
    jdouble * magniBuffer;
    magniBuffer = env -> GetDoubleArrayElements(magni_array, NULL);

    jint * intBuffer = (jint *)malloc(sizeof(jint) * size);
    jintArray resultIntArray = env->NewIntArray(size);

    for(int i = 0; i < size; i++){
        midConvert = (20*log10(magniBuffer[i]/maxVal_dpplr) + dynamic_range);
        intBuffer[i] = (int)(max(midConvert, 0.0) / dynamic_range * 255);
    }

    env->SetIntArrayRegion(resultIntArray, 0, size, (const jint *)intBuffer);
    env->ReleaseDoubleArrayElements(magni_array, magniBuffer, 0);
    free(intBuffer);

    return resultIntArray;
}