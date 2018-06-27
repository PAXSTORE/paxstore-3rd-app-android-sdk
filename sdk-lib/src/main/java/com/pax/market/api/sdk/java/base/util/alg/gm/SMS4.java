/*
 *
 *  * *******************************************************************************
 *  * COPYRIGHT
 *  *               PAX TECHNOLOGY, Inc. PROPRIETARY INFORMATION
 *  *   This software is supplied under the terms of a license agreement or
 *  *   nondisclosure agreement with PAX  Technology, Inc. and may not be copied
 *  *   or disclosed except in accordance with the terms in that agreement.
 *  *
 *  *      Copyright (C) 2017 PAX Technology, Inc. All rights reserved.
 *  * *******************************************************************************
 *
 */

package com.pax.market.api.sdk.java.base.util.alg.gm;

public class SMS4 {
    private static final int DECRYPT = 0;
    public static final int ROUND = 32;
    private static final int BLOCK = 16;
    private byte[] Sbox = {-42, -112, -23, -2, -52, -31, 61, -73, 22, -74, 20, -62, 40, -5, 44, 5, 43, 103, -102, 118, 42, -66, 4, -61, -86, 68, 19, 38, 73, -122, 6, -103, -100, 66, 80, -12, -111, -17, -104, 122, 51, 84, 11, 67, -19, -49, -84, 98, -28, -77, 28, -87, -55, 8, -24, -107, Byte.MIN_VALUE, -33, -108, -6, 117, -113, 63, -90, 71, 7, -89, -4, -13, 115, 23, -70, -125, 89, 60, 25, -26, -123, 79, -88, 104, 107, -127, -78, 113, 100, -38, -117, -8, -21, 15, 75, 112, 86, -99, 53, 30, 36, 14, 94, 99, 88, -47, -94, 37, 34, 124, 59, 1, 33, 120, -121, -44, 0, 70, 87, -97, -45, 39, 82, 76, 54, 2, -25, -96, -60, -56, -98, -22, -65, -118, -46, 64, -57, 56, -75, -93, -9, -14, -50, -7, 97, 21, -95, -32, -82, 93, -92, -101, 52, 26, 85, -83, -109, 50, 48, -11, -116, -79, -29, 29, -10, -30, 46, -126, 102, -54, 96, -64, 41, 35, -85, 13, 83, 78, 111, -43, -37, 55, 69, -34, -3, -114, 47, 3, -1, 106, 114, 109, 108, 91, 81, -115, 27, -81, -110, -69, -35, -68, Byte.MAX_VALUE, 17, -39, 92, 65, 31, 16, 90, -40, 10, -63, 49, -120, -91, -51, 123, -67, 45, 116, -48, 18, -72, -27, -76, -80, -119, 105, -105, 74, 12, -106, 119, 126, 101, -71, -15, 9, -59, 110, -58, -124, 24, -16, 125, -20, 58, -36, 77, 32, 121, -18, 95, 62, -41, -53, 57, 72};
    private int[] CK = {462357, 472066609, 943670861, 1415275113, 1886879365, -1936483679, -1464879427, -993275175, -521670923, -66909679, 404694573, 876298825, 1347903077, 1819507329, -2003855715, -1532251463, -1060647211, -589042959, -117504499, 337322537, 808926789, 1280531041, 1752135293, -2071227751, -1599623499, -1128019247, -656414995, -184876535, 269950501, 741554753, 1213159005, 1684763257};

    private int Rotl(int paramInt1, int paramInt2) {
        return paramInt1 << paramInt2 | paramInt1 >>> 32 - paramInt2;
    }

    private int ByteSub(int paramInt) {
        return (this.Sbox[(paramInt >>> 24 & 0xFF)] & 0xFF) << 24 | (this.Sbox[(paramInt >>> 16 & 0xFF)] & 0xFF) << 16 | (this.Sbox[(paramInt >>> 8 & 0xFF)] & 0xFF) << 8 | this.Sbox[(paramInt & 0xFF)] & 0xFF;
    }

    private int L1(int paramInt) {
        return paramInt ^ Rotl(paramInt, 2) ^ Rotl(paramInt, 10) ^ Rotl(paramInt, 18) ^ Rotl(paramInt, 24);
    }

    private int L2(int paramInt) {
        return paramInt ^ Rotl(paramInt, 13) ^ Rotl(paramInt, 23);
    }

    void SMS4Crypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int[] paramArrayOfInt) {
        int[] arrayOfInt1 = new int[4];
        int[] arrayOfInt2 = new int[4];
        for (int k = 0; k < 4; k++) {
            arrayOfInt2[0] = (paramArrayOfByte1[(0 + 4 * k)] & 0xFF);
            arrayOfInt2[1] = (paramArrayOfByte1[(1 + 4 * k)] & 0xFF);
            arrayOfInt2[2] = (paramArrayOfByte1[(2 + 4 * k)] & 0xFF);
            arrayOfInt2[3] = (paramArrayOfByte1[(3 + 4 * k)] & 0xFF);
            arrayOfInt1[k] = (arrayOfInt2[0] << 24 | arrayOfInt2[1] << 16 | arrayOfInt2[2] << 8 | arrayOfInt2[3]);
        }
        for (int i = 0; i < 32; i += 4) {
            int j = arrayOfInt1[1] ^ arrayOfInt1[2] ^ arrayOfInt1[3] ^ paramArrayOfInt[(i + 0)];
            j = ByteSub(j);
            arrayOfInt1[0] ^= L1(j);
            j = arrayOfInt1[2] ^ arrayOfInt1[3] ^ arrayOfInt1[0] ^ paramArrayOfInt[(i + 1)];
            j = ByteSub(j);
            arrayOfInt1[1] ^= L1(j);
            j = arrayOfInt1[3] ^ arrayOfInt1[0] ^ arrayOfInt1[1] ^ paramArrayOfInt[(i + 2)];
            j = ByteSub(j);
            arrayOfInt1[2] ^= L1(j);
            j = arrayOfInt1[0] ^ arrayOfInt1[1] ^ arrayOfInt1[2] ^ paramArrayOfInt[(i + 3)];
            j = ByteSub(j);
            arrayOfInt1[3] ^= L1(j);
        }
        for (int k = 0; k < 16; k += 4) {
            paramArrayOfByte2[k] = ((byte) (arrayOfInt1[(3 - k / 4)] >>> 24 & 0xFF));
            paramArrayOfByte2[(k + 1)] = ((byte) (arrayOfInt1[(3 - k / 4)] >>> 16 & 0xFF));
            paramArrayOfByte2[(k + 2)] = ((byte) (arrayOfInt1[(3 - k / 4)] >>> 8 & 0xFF));
            paramArrayOfByte2[(k + 3)] = ((byte) (arrayOfInt1[(3 - k / 4)] & 0xFF));
        }
    }

    private void SMS4KeyExt(byte[] paramArrayOfByte, int[] paramArrayOfInt, int paramInt) {
        int[] arrayOfInt1 = new int[4];
        int[] arrayOfInt2 = new int[4];
        for (int k = 0; k < 4; k++) {
            arrayOfInt2[0] = (paramArrayOfByte[(0 + 4 * k)] & 0xFF);
            arrayOfInt2[1] = (paramArrayOfByte[(1 + 4 * k)] & 0xFF);
            arrayOfInt2[2] = (paramArrayOfByte[(2 + 4 * k)] & 0xFF);
            arrayOfInt2[3] = (paramArrayOfByte[(3 + 4 * k)] & 0xFF);
            arrayOfInt1[k] = (arrayOfInt2[0] << 24 | arrayOfInt2[1] << 16 | arrayOfInt2[2] << 8 | arrayOfInt2[3]);
        }
        arrayOfInt1[0] ^= 0xA3B1BAC6;
        arrayOfInt1[1] ^= 0x56AA3350;
        arrayOfInt1[2] ^= 0x677D9197;
        arrayOfInt1[3] ^= 0xB27022DC;
        int j;
        for (int i = 0; i < 32; i += 4) {
            j = arrayOfInt1[1] ^ arrayOfInt1[2] ^ arrayOfInt1[3] ^ this.CK[(i + 0)];
            j = ByteSub(j);
            paramArrayOfInt[(i + 0)] = (arrayOfInt1[0] ^= L2(j));
            j = arrayOfInt1[2] ^ arrayOfInt1[3] ^ arrayOfInt1[0] ^ this.CK[(i + 1)];
            j = ByteSub(j);
            paramArrayOfInt[(i + 1)] = (arrayOfInt1[1] ^= L2(j));
            j = arrayOfInt1[3] ^ arrayOfInt1[0] ^ arrayOfInt1[1] ^ this.CK[(i + 2)];
            j = ByteSub(j);
            paramArrayOfInt[(i + 2)] = (arrayOfInt1[2] ^= L2(j));
            j = arrayOfInt1[0] ^ arrayOfInt1[1] ^ arrayOfInt1[2] ^ this.CK[(i + 3)];
            j = ByteSub(j);
            paramArrayOfInt[(i + 3)] = (arrayOfInt1[3] ^= L2(j));
        }
        if (paramInt == 0) {
            for (int i = 0; i < 16; i++) {
                j = paramArrayOfInt[i];
                paramArrayOfInt[i] = paramArrayOfInt[(31 - i)];
                paramArrayOfInt[(31 - i)] = j;
            }
        }
    }

    public int SM4(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, int paramInt2) {
        int i = 0;
        int[] arrayOfInt = new int[32];
        SMS4KeyExt(paramArrayOfByte2, arrayOfInt, paramInt2);
        byte[] arrayOfByte1 = new byte[16];
        byte[] arrayOfByte2 = new byte[16];
        while (paramInt1 >= 16) {
            System.arraycopy(arrayOfByte1, i, paramArrayOfByte1, i + 16, 16);
            SMS4Crypt(arrayOfByte1, arrayOfByte2, arrayOfInt);
            System.arraycopy(arrayOfByte2, 0, paramArrayOfByte3, i, 16);
            paramInt1 -= 16;
            i += 16;
        }
        return 0;
    }
}