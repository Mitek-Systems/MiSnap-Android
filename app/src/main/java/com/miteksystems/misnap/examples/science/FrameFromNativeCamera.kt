package com.miteksystems.misnap.examples.science

import android.graphics.ImageFormat
import android.media.Image
import android.util.Size
import androidx.camera.core.ImageProxy
import com.miteksystems.misnap.core.Frame
import com.miteksystems.misnap.core.FrameImage
import com.miteksystems.misnap.core.FrameUtil

private class FrameFromNativeCamera {
    fun camera1toFrame(bytes: ByteArray, width: Int, height: Int, imageFormat: Int) = object: Frame {
        override val imageSize = Size(width, height)

        override val imageFormat = imageFormat

        // NOTE: Please use actual rotation derived from camera and device orientation
        override val rotationDegrees = 0

        override val imageBytes = bytes

        override fun close() {
        }
    }

    fun camera2ToFrame(image: Image) = object : Frame {
        override val imageSize = Size(image.width, image.height)

        override val imageFormat =
            if (image.format == ImageFormat.YUV_420_888) ImageFormat.NV21 else image.format

        // NOTE: Please use actual rotation derived from camera and device orientation
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

    private fun imageToFrameImage(image: Image) = object : FrameImage {
        override val format = image.format
        override val width = image.width
        override val height = image.height
        override val planes: Array<FrameImage.Plane> =
            image.planes.map { imagePlaneToFrameImagePlane(it) }.toTypedArray()
    }

    private fun imagePlaneToFrameImagePlane(plane: Image.Plane) =
        object : FrameImage.Plane {
            override val byteBuffer = plane.buffer
            override val rowStride = plane.rowStride
            override val pixelStride = plane.pixelStride
        }

    private fun imageProxyToFrameImage(imageProxy: ImageProxy) = object : FrameImage {
        override val format = imageProxy.format
        override val width = imageProxy.width
        override val height = imageProxy.height
        override val planes: Array<FrameImage.Plane> =
            imageProxy.planes.map { planeProxyToFrameImagePlane(it) }.toTypedArray()
    }

    private fun planeProxyToFrameImagePlane(planeProxy: ImageProxy.PlaneProxy) =
        object : FrameImage.Plane {
            override val byteBuffer = planeProxy.buffer
            override val rowStride = planeProxy.rowStride
            override val pixelStride = planeProxy.pixelStride
        }
}
