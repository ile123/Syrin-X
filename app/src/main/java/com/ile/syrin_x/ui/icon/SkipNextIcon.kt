package com.ile.syrin_x.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val SkipNextIcon: ImageVector
    get() {
        if (_skip_next_icon != null) {
            return _skip_next_icon!!
        }
        _skip_next_icon = ImageVector.Builder(
            name = "Skip_next",
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
                moveTo(660f, 720f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(-440f, 0f)
                verticalLineToRelative(-480f)
                lineToRelative(360f, 240f)
                close()
                moveToRelative(80f, -150f)
                lineToRelative(136f, -90f)
                lineToRelative(-136f, -90f)
                close()
            }
        }.build()
        return _skip_next_icon!!
    }

private var _skip_next_icon: ImageVector? = null
