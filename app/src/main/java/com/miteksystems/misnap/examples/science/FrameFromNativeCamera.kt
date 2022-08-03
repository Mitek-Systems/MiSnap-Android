package com.miteksystems.misnap.examples.science

import android.graphics.ImageFormat
import android.hardware.Camera
import android.media.Image
import android.util.Size
import androidx.camera.core.ImageProxy
import com.miteksystems.misnap.controller.MiSnapController
import com.miteksystems.misnap.core.Frame
import com.miteksystems.misnap.core.FrameImage
import com.miteksystems.misnap.core.FrameUtil

/**
 * This is a series of examples to demonstrate different ways of building a [Frame] instance that can
 * then be analyzed by the [MiSnapController] with the different MiSnap SDK sciences.
 *
 * @see [FrameImage] and [FrameImage.Plane] helper classes for the full list of properties that the
 * Camera2 and CameraX generated frames should be mapped to in order to build a [Frame].
 * @see [FrameUtil] for the full list of helper methods to create or manipulate [Frame] objects.
 */
private class FrameFromNativeCamera {
    /**
     * Convert a Camera API preview frame into a [Frame] object by mapping the Camera1 frame properties to
     * the corresponding MiSnap SDK frame properties. The Camera1 frame data can be mapped directly to
     * a [Frame] object without requiring extra format conversions.
     * NOTE: Camera1 does not calculate the rotation degrees of the frame, you must calculate it and
     *  use it while building a [Frame].
     *
     * @see [Camera.PreviewCallback] for more information about the data and format received from
     * the Camera1 API.
     */
    fun camera1toFrame(bytes: ByteArray, width: Int, height: Int, imageFormat: Int) =
        object : Frame {
            override val imageSize = Size(width, height)

            override val imageFormat = imageFormat

            // Use the actual rotation derived from the camera and the device orientations.
            override val rotationDegrees = 0

            override val imageBytes = bytes

            override fun close() {

            }
        }

    /**
     * Convert a Camera2 API preview frame into a [Frame] object by mapping the [Image] properties to
     * the corresponding MiSnap SDK frame properties. The [FrameUtil] helper methods are used since
     * the Camera2 output can't be directly converted into a [Frame] object.
     * NOTE: NOTE: Camera2 does not calculate the rotation degrees of the frame, you must calculate it and
     *  use it while building a [Frame].
     */
    fun camera2ToFrame(image: Image) = object : Frame {
        override val imageSize = Size(image.width, image.height)

        override val imageFormat =
            if (image.format == ImageFormat.YUV_420_888) ImageFormat.NV21 else image.format

        // Use the actual rotation derived from the camera and the device orientations.
        override val rotationDegrees = 0

        override val imageBytes =
            when (image.format) {
                ImageFormat.YUV_420_888 -> FrameUtil.yuv420ToNv21(imageToFrameImage(image))
                ImageFormat.JPEG -> FrameUtil.stripChannelPadding(
                    image.planes[0].buffer,
                    image.width,
                    image.height,
                    0,
                    1
                )
                else -> byteArrayOf()
            }

        override fun close() {
            image.close()
        }
    }

    /**
     * Convert a CameraX API preview frame into a [Frame] object by mapping the [ImageProxy] properties to
     * the corresponding MiSnap SDK frame properties. The [FrameUtil] helper methods are used since
     * the CameraX output can't be directly converted into a [Frame] object.
     */
    fun cameraxToFrame(imageProxy: ImageProxy) = object : Frame {
        override val imageSize = Size(imageProxy.width, imageProxy.height)

        override val imageFormat =
            if (imageProxy.format == ImageFormat.YUV_420_888) ImageFormat.NV21 else imageProxy.format

        override val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        override val imageBytes =
            when (imageProxy.format) {
                ImageFormat.YUV_420_888 -> FrameUtil.yuv420ToNv21(imageProxyToFrameImage(imageProxy))
                ImageFormat.JPEG -> FrameUtil.stripChannelPadding(
                    imageProxy.planes[0].buffer,
                    imageProxy.width,
                    imageProxy.height,
                    0,
                    1
                )
                else -> byteArrayOf()
            }

        override fun close() {
            imageProxy.close()
        }
    }

    /**
     * Helper method to map the data from a Camera2 [Image] into a [FrameImage] to make the conversion
     * into a [Frame] easier.
     */
    private fun imageToFrameImage(image: Image) = object : FrameImage {
        override val format = image.format
        override val width = image.width
        override val height = image.height
        override val planes: Array<FrameImage.Plane> =
            image.planes.map { imagePlaneToFrameImagePlane(it) }.toTypedArray()
    }

    /**
     * Helper method to map the data from a Camera2 [Image.Plane] into a [FrameImage.Plane] to make
     * the conversion into a [Frame] easier.
     */
    private fun imagePlaneToFrameImagePlane(plane: Image.Plane) =
        object : FrameImage.Plane {
            override val byteBuffer = plane.buffer
            override val rowStride = plane.rowStride
            override val pixelStride = plane.pixelStride
        }

    /**
     * Helper method to map the data from a CameraX [ImageProxy] into a [FrameImage] to make the conversion
     * into a [Frame] easier.
     */
    private fun imageProxyToFrameImage(imageProxy: ImageProxy) = object : FrameImage {
        override val format = imageProxy.format
        override val width = imageProxy.width
        override val height = imageProxy.height
        override val planes: Array<FrameImage.Plane> =
            imageProxy.planes.map { planeProxyToFrameImagePlane(it) }.toTypedArray()
    }

    /**
     * Helper method to map the data from a CameraX [ImageProxy.PlaneProxy] into a [FrameImage.Plane] to make
     * the conversion into a [Frame] easier.
     */
    private fun planeProxyToFrameImagePlane(planeProxy: ImageProxy.PlaneProxy) =
        object : FrameImage.Plane {
            override val byteBuffer = planeProxy.buffer
            override val rowStride = planeProxy.rowStride
            override val pixelStride = planeProxy.pixelStride
        }
}
