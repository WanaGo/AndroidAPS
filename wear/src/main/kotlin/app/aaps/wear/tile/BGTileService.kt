package app.aaps.wear.tile

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ModifiersBuilders.Clickable
import androidx.wear.protolayout.ModifiersBuilders.Modifiers
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.DimensionBuilders.sp
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import app.aaps.wear.data.ComplicationDataRepository
import app.aaps.wear.interaction.menus.MainMenuActivity
import com.google.common.util.concurrent.ListenableFuture
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.guava.future
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class BGTileService : TileService() {

    @Inject lateinit var complicationDataRepository: ComplicationDataRepository

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<Tile> = serviceScope.future {
        val data = complicationDataRepository.complicationData.first()
        val bgData = data.bgData
        val statusData = data.statusData

        val minutesAgo = if (bgData.timeStamp > 0) {
            val diffMs = System.currentTimeMillis() - bgData.timeStamp
            val mins = TimeUnit.MILLISECONDS.toMinutes(diffMs)
            "$mins min ago"
        } else {
            "--"
        }

        val launchAction = ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(ActionBuilders.AndroidActivity.Builder()
                .setPackageName(packageName)
                .setClassName(MainMenuActivity::class.java.name)
                .build())
            .build()

        val clickable = Clickable.Builder()
            .setOnClick(launchAction)
            .build()

        val layout = Column.Builder()
            .setHorizontalAlignment(HORIZONTAL_ALIGN_CENTER)
            .setModifiers(Modifiers.Builder()
                .setClickable(clickable)
                .build())
            .addContent(
                Text.Builder()
                    .setText(bgData.sgvString)
                    .setFontStyle(FontStyle.Builder()
                        .setSize(sp(60f))
                        .setWeight(FONT_WEIGHT_BOLD)
                        .setColor(argb(getBgColor(bgData.sgvLevel.toInt())))
                        .build())
                    .build()
            )
            .addContent(Spacer.Builder().setHeight(dp(2f)).build())
            .addContent(
                Row.Builder()
                    .addContent(Text.Builder().setText(bgData.slopeArrow).setFontStyle(FontStyle.Builder().setSize(sp(24f)).build()).build())
                    .addContent(Spacer.Builder().setWidth(dp(10f)).build())
                    .addContent(Text.Builder().setText(bgData.delta).setFontStyle(FontStyle.Builder().setSize(sp(24f)).build()).build())
                    .build()
            )
            .addContent(Spacer.Builder().setHeight(dp(10f)).build())
            .addContent(
                Row.Builder()
                    .addContent(Text.Builder().setText("IOB: ${statusData.iobSum}").setFontStyle(FontStyle.Builder().setSize(sp(16f)).build()).build())
                    .addContent(Spacer.Builder().setWidth(dp(12f)).build())
                    .addContent(Text.Builder().setText("COB: ${statusData.cob}").setFontStyle(FontStyle.Builder().setSize(sp(16f)).build()).build())
                    .build()
            )
            .addContent(Spacer.Builder().setHeight(dp(12f)).build())
            .addContent(
                Text.Builder()
                    .setText(minutesAgo)
                    .setFontStyle(FontStyle.Builder()
                        .setSize(sp(14f))
                        .setColor(argb(0xFFB0BEC5.toInt()))
                        .build())
                    .build()
            )
            .build()

        Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(Timeline.fromLayoutElement(layout))
            .setFreshnessIntervalMillis(60 * 1000)
            .build()
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> = serviceScope.future {
        ResourceBuilders.Resources.Builder()
            .setVersion("1")
            .build()
    }

    private fun getBgColor(level: Int): Int = when (level) {
        -1 -> 0xFFFF5252.toInt() // Red
        1 -> 0xFFFFD600.toInt()  // Yellow
        else -> 0xFF81C784.toInt() // Green
    }

    override fun onDestroy() {
        serviceJob.cancel()
        super.onDestroy()
    }
}
