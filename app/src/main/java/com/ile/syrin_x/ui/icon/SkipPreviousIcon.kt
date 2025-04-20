package com.ile.syrin_x.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val SkipPreviousIcon: ImageVector
    get() {
        if (_skip_previous_icon != null) {
            return _skip_previous_icon!!
        }
        _skip_previous_icon = ImageVector.Builder(
            name = "Skip_previous",
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
                moveTo(220f, 720f)
                verticalLineToRelative(-480f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(480f)
                close()
                moveToRelative(520f, 0f)
                lineTo(380f, 480f)
                lineToRelative(360f, -240f)
                close()
                moveToRelative(-80f, -150f)
                verticalLineToRelative(-180f)
                lineToRelative(-136f, 90f)
                close()
            }
        }.build()
        return _skip_previous_icon!!
    }

private var _skip_previous_icon: ImageVector? = null
