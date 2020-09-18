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

double* iirFilter(array<double, 4>, array<double, 4>, array<double, 128>);


/**
 * Imaging Value
 */
int bit_width = 16;

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
jdouble maxVal = pow(2, 26);
int dynamic_range = 50;

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

JNIEXPORT jintArray JNICALL Java_co_haslo_excelregistermapcontrolboard_NativeProcessing_NativeWrapper_nativeImaging
(JNIEnv *env, jobject, jbyteArray buffer_array) {

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
        bMode_convertBuffer = bMode_Data00 << 24 | (bMode_Data01 & 0xFF) << 16 | (bMode_Data02 & 0xFF) << 8 | (bMode_Data03 & 0xFF);
        bMode_dataInphase = floor(bMode_convertBuffer/pow(2,16));// I = floor(result_mj/2^16);
        bMode_dataQuad = bMode_convertBuffer - (bMode_dataInphase  * pow(2, 16)); // Q = result_mj-I*2^16;
        bMode_convertInphase = ( bMode_dataInphase >= pow( 2, bit_width-1) ) ? ( bMode_dataInphase - pow(2, bit_width) ) : bMode_dataInphase;
        bMode_convertMag = pow(bMode_dataInphase , 2) + pow(bMode_dataQuad, 2);
        bMode_findHighLow = (10*log10(bMode_convertMag/maxVal) + dynamic_range);
        bMode_finalData = max(bMode_findHighLow, 0.0) /dynamic_range*255;
        intBuffer[counter] = bMode_finalData;
    }

    env->SetIntArrayRegion(resultIntArray, 0, size/4, (const jint *)intBuffer);
    env->ReleaseByteArrayElements(buffer_array, byteBuffer, 0);
    free(intBuffer);

    return resultIntArray;
}