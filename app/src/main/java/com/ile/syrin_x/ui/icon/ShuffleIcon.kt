package com.ile.syrin_x.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ShuffleIcon: ImageVector
    get() {
        if (_shuffle_icon != null) {
            return _shuffle_icon!!
        }
        _shuffle_icon = ImageVector.Builder(
            name = "Shuffle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                stroke = null,
                strokeAlpha = 1.0f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1.0f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(560f, 800f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(104f)
                lineTo(537f, 593f)
                lineToRelative(57f, -57f)
                lineToRelative(126f, 126f)
                verticalLineToRelative(-102f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(240f)
                close()
                moveToRelative(-344f, 0f)
                lineToRelative(-56f, -56f)
                lineToRelative(504f, -504f)
                horizontalLineTo(560f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-104f)
                close()
                moveToRelative(151f, -377f)
                lineTo(160f, 216f)
                lineToRelative(56f, -56f)
                lineToRelative(207f, 207f)
                close()
            }
        }.build()
        return _shuffle_icon!!
    }

private var _shuffle_icon: ImageVector? = null
