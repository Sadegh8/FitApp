package com.panda.app.fitapp.Util

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources

import android.os.Build
import java.util.*


fun changeLang(context: Context, lang_code: String): ContextWrapper? {
    var context: Context = context
    val sysLocale: Locale
    val rs: Resources = context.resources
    val config: Configuration = rs.configuration
    sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.locales.get(0)
    } else {
        config.locale
    }
    if (lang_code != "" && sysLocale.language != lang_code) {
        val locale = Locale(lang_code)
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(config)
        } else {
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        }
    }
    return ContextWrapper(context)
}