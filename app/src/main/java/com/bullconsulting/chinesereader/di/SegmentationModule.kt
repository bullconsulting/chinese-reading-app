package com.bullconsulting.chinesereader.di

import com.bullconsulting.chinesereader.data.segmentation.JiebaSegmenterImpl
import com.bullconsulting.chinesereader.data.segmentation.JiebaWordFrequency
import com.bullconsulting.chinesereader.domain.repository.Segmenter
import com.bullconsulting.chinesereader.domain.repository.WordFrequency
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Jieba-backed implementations for segmentation and word frequency. */
@Module
@InstallIn(SingletonComponent::class)
abstract class SegmentationModule {

    @Binds
    abstract fun bindSegmenter(impl: JiebaSegmenterImpl): Segmenter

    @Binds
    abstract fun bindWordFrequency(impl: JiebaWordFrequency): WordFrequency
}
