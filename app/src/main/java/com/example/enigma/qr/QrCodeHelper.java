/*  Enigma - Onion Routing based messaging app.
    Copyright (C) 2022  Romulus-Emanuel Ruja <romulus-emanuel.ruja@tutanota.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.example.enigma.qr;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.IntRange;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QrCodeHelper {
    private static QrCodeHelper qrCodeHelper = null;
    private ErrorCorrectionLevel mErrorCorrectionLevel;
    private int mMargin;
    private String mContent;
    private int mWidth, mHeight;

    /**
     * private constructor of this class only access by stying in this class.
     */
    private QrCodeHelper(Context context) {
        int m = Math.min(context.getResources().getDisplayMetrics().heightPixels,
                context.getResources().getDisplayMetrics().widthPixels);

        mHeight = m;
        mWidth = m;
    }

    /**
     * This method is for singleton instance od this class.
     *
     * @return the QrCode instance.
     */
    public static QrCodeHelper newInstance(Context context) {
        if (qrCodeHelper == null) {
            qrCodeHelper = new QrCodeHelper(context);
        }
        return qrCodeHelper;
    }

    /**
     * This method is called generate function who generate the qrcode and return it.
     *
     * @return qrcode image with encrypted user in it.
     */

    public Bitmap getQRCOde() {
        return generate();
    }

    /**
     * Simply setting the correctionLevel to qrcode.
     *
     * @param level ErrorCorrectionLevel for Qrcode.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */
    public QrCodeHelper setErrorCorrectionLevel(ErrorCorrectionLevel level) {
        mErrorCorrectionLevel = level;
        return this;
    }

    /**
     * Simply setting the encrypted to qrcode.
     *
     * @param content encrypted content for to store in qrcode.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */

    public QrCodeHelper setContent(String content) {
        mContent = content;
        return this;
    }

    /**
     * Simply setting the width and height for qrcode.
     *
     * @param width  for qrcode it needs to greater than 1.
     * @param height for qrcode it needs to greater than 1.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */

    public QrCodeHelper setWidthAndHeight(@IntRange(from = 1) int width, @IntRange(from = 1) int height) {
        mWidth = width;
        mHeight = height;
        return this;
    }

    /**
     * Simply setting the margin for qrcode.
     *
     * @param margin for qrcode spaces.
     * @return the instance of QrCode helper class for to use remaining function in class.
     */

    public QrCodeHelper setMargin(@IntRange(from = 0) int margin) {
        mMargin = margin;
        return this;
    }

    /**
     * Generate the qrcode with giving the properties.
     *
     * @return the qrcode image.
     */

    private Bitmap generate() {

        if(mContent == null)
        {
            return null;
        }

        Map<EncodeHintType, Object> hintsMap = new HashMap<>();
        hintsMap.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hintsMap.put(EncodeHintType.ERROR_CORRECTION, mErrorCorrectionLevel);
        hintsMap.put(EncodeHintType.MARGIN, mMargin);

        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(mContent, BarcodeFormat.QR_CODE, mWidth,
                    mHeight, hintsMap);
            int[] pixels = new int[mWidth * mHeight];
            for (int i = 0; i < mHeight; i++) {
                for (int j = 0; j < mWidth; j++) {
                    if (bitMatrix.get(j, i)) {
                        pixels[i * mWidth + j] = 0xFFFFFFFF;
                    } else {
                        pixels[i * mWidth + j] = 0x282946;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, mWidth, mHeight, Bitmap.Config.ARGB_8888);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
