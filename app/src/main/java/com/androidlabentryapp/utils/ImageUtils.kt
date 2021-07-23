package com.androidlabentryapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

internal fun Context.handleBitmap(imageUri: Uri) =
    cutBitmapToSquare(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            handleBitmapCutAndSampling(imageUri)
        } else {
            getBitmapByUri(imageUri)
        }
    )

private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    img.recycle()
    return rotatedImg
}

internal fun Context.getBitmapByUri(imageUri: Uri) =
    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)


internal fun compressBitmap(bitmap: Bitmap, quality: Int): Bitmap {
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
    return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
}

internal fun cutBitmapToSquare(sourceBitmap: Bitmap): Bitmap {
    val width = sourceBitmap.width
    val height = sourceBitmap.height
    return if (width > height) {
        Bitmap.createBitmap(sourceBitmap, (width - height) / 2, 0, height, height)
    } else {
        Bitmap.createBitmap(sourceBitmap, 0, (height - width) / 2, width, width)
    }
}

internal fun bitmapToString(bitmap: Bitmap) =
    with(ByteArrayOutputStream()) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
        Base64.encodeToString(toByteArray(), Base64.DEFAULT)
    }

internal fun stringToBitmap(encodedString: String) =
    try {
        val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
    } catch (e: Exception) {
        e.message
        null
    }

@RequiresApi(Build.VERSION_CODES.N)
internal fun Context.handleBitmapCutAndSampling(selectedImage: Uri?): Bitmap {
    val maxHeight = 1024
    val maxWidth = 1024
    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    var imageStream: InputStream =
        contentResolver.openInputStream(selectedImage!!)!!
    BitmapFactory.decodeStream(imageStream, null, options)
    imageStream.close()

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    imageStream = contentResolver.openInputStream(selectedImage)!!
    var img = BitmapFactory.decodeStream(imageStream, null, options)
    img = rotateImageIfRequired(this, img!!, selectedImage)
    return img
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int, reqHeight: Int
): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
        val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())

        // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
        // with both dimensions larger than or equal to the requested height and width.
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio

        // This offers some additional logic in case the image has a strange
        // aspect ratio. For example, a panorama may have a much larger
        // width than height. In these cases the total pixels might still
        // end up being too large to fit comfortably in memory, so we should
        // be more aggressive with sample down the image (=larger inSampleSize).
        val totalPixels = (width * height).toFloat()

        // Anything more than 2x the requested pixels we'll sample down further
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
    }
    return inSampleSize
}

@RequiresApi(Build.VERSION_CODES.N)
private fun rotateImageIfRequired(
    context: Context,
    img: Bitmap,
    selectedImage: Uri
): Bitmap {
    val imageStream: InputStream? =
        context.contentResolver.openInputStream(selectedImage)
    val ei = ExifInterface(imageStream!!)
    return when (ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
        else -> img
    }
}