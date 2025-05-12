package com.ile.syrin_x.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val ShuffleOnIcon: ImageVector
    get() {
        if (_shuffle_on_icon != null) {
            return _shuffle_on_icon!!
        }
        _shuffle_on_icon = ImageVector.Builder(
            name = "Shuffle_on",
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
                moveTo(120f, 920f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(40f, 840f)
                verticalLineToRelative(-720f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(120f, 40f)
                horizontalLineToRelative(720f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(920f, 120f)
                verticalLineToRelative(720f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(840f, 920f)
                close()
                moveToRelative(440f, -120f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(102f)
                lineTo(594f, 536f)
                lineToRelative(-57f, 57f)
                lineToRelative(127f, 127f)
                horizontalLineTo(560f)
                close()
                moveToRelative(-344f, 0f)
                lineToRelative(504f, -504f)
                verticalLineToRelative(104f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-240f)
                horizontalLineTo(560f)
                verticalLineToRelative(80f)
                horizontalLineToRelative(104f)
                lineTo(160f, 744f)
                close()
                moveToRelative(151f, -377f)
                lineToRelative(56f, -56f)
                lineToRelative(-207f, -207f)
                lineToRelative(-56f, 56f)
                close()
            }
        }.build()
        return _shuffle_on_icon!!
    }

private var _shuffle_on_icon: ImageVector? = null
