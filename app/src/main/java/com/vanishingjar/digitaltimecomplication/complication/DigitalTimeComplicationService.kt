package com.vanishingjar.digitaltimecomplication.complication

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon.createWithResource
import android.provider.AlarmClock
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationText
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.MonochromaticImage
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.TimeFormatComplicationText
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import com.vanishingjar.digitaltimecomplication.R
import java.time.LocalDateTime

class DigitalTimeComplicationService : ComplicationDataSourceService() {

    private fun openScreen(): PendingIntent? {

        val mClockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)

        return PendingIntent.getActivity(
            this, 0, mClockIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = 741f,
                    min = 0f,
                    max =  1440f,
                    contentDescription = ComplicationText.EMPTY)
                    .setText(PlainComplicationText.Builder(text = "12:21").build())
                    .setMonochromaticImage(MonochromaticImage.Builder(image = createWithResource(this, R.drawable.ic_clock)).build())
                    .build()
            }
            else -> null
        }
    }

    override fun onComplicationRequest(request: ComplicationRequest, listener: ComplicationRequestListener) {
        val hour = LocalDateTime.now().hour
        val min = LocalDateTime.now().minute
        val progress = hour * 60 + min.toFloat()

        //Log.d("DigitalTimeComplicationService", "onComplicationRequest: Update requested")

        val timeFormat = if (android.text.format.DateFormat.is24HourFormat(this)) "HH:mm" else "h:mm"

        val formattedTime = TimeFormatComplicationText.Builder(format = timeFormat).build()

        val complicationData = when (request.complicationType) {
            ComplicationType.RANGED_VALUE -> createRangedValueComplicationData(formattedTime, progress)
            else -> null
        }

        listener.onComplicationData(complicationData)
    }

    private fun createRangedValueComplicationData(time: TimeFormatComplicationText, progress: Float): RangedValueComplicationData {
        return RangedValueComplicationData.Builder(
            dynamicValue = DynamicBuilders.DynamicFloat.constant(progress),
            fallbackValue = progress,
            min = 0f,
            max =  1440f,
            contentDescription = PlainComplicationText.Builder(text = "Current Time: $time").build())
            .setText(time)
            .setMonochromaticImage(MonochromaticImage.Builder(image = createWithResource(this, R.drawable.ic_clock)).build())
            .setTapAction(openScreen())
            .build()
    }
}