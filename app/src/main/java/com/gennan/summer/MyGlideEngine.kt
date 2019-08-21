package com.gennan.summer

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Priority
import com.zhihu.matisse.engine.ImageEngine


/**
 *Created by Gennan on 2019/8/19.
 * 配合知乎图像选择框架做的一些更改
 */
class MyGlideEngine : ImageEngine {
    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        GlideApp.with(context)
            .asBitmap()  // some .jpeg files are actually gif
            .load(uri)
            .override(resize, resize)
            .centerCrop()
            .into(imageView)
    }

    override fun loadAnimatedGifThumbnail(
        context: Context, resize: Int, placeholder: Drawable, imageView: ImageView,
        uri: Uri
    ) {
        GlideApp.with(context)
            .asBitmap()
            .load(uri)
            .placeholder(placeholder)
            .override(resize, resize)
            .centerCrop()
            .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        GlideApp.with(context)
            .load(uri)
            .override(resizeX, resizeY)
            .priority(Priority.HIGH)
            .into(imageView)
    }

    override fun loadAnimatedGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        GlideApp.with(context)
            .load(uri)
            .override(resizeX, resizeY)
            .priority(Priority.HIGH)
            .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }
}