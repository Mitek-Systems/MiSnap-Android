package com.miteksystems.misnap.sampleapp.results

import android.media.MediaDataSource
import kotlin.math.min

class MiSnapVideoSource(private val data: ByteArray) : MediaDataSource() {
    override fun close() {}

    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        if (position >= data.size) {
            return -1
        }

        val maxReadSize = min(data.size - position.toInt(), size)
        val maxWriteSize = min(buffer.size - offset, size)
        val transferSize = min(maxReadSize, maxWriteSize)

        data.copyInto(buffer, offset, position.toInt(), position.toInt() + transferSize)

        return transferSize
    }

    override fun getSize() = data.size.toLong()
}