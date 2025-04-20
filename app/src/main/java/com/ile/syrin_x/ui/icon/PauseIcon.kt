package com.ile.syrin_x.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val PauseIcon: ImageVector
    get() {
        if (_pause_icon != null) {
            return _pause_icon!!
        }
        _pause_icon = ImageVector.Builder(
            name = "Pause",
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
                moveTo(520f, 760f)
                verticalLineToRelative(-560f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(560f)
                close()
                moveToRelative(-320f, 0f)
                verticalLineToRelative(-560f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(560f)
                close()
                moveToRelative(400f, -80f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-400f)
                horizontalLineToRelative(-80f)
                close()
                moveToRelative(-320f, 0f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-400f)
                horizontalLineToRelative(-80f)
                close()
                moveToRelative(0f, -400f)
                verticalLineToRelative(400f)
                close()
                moveToRelative(320f, 0f)
                verticalLineToRelative(400f)
                close()
            }
        }.build()
        return _pause_icon!!
    }

private var _pause_icon: ImageVector? = null
