package com.ile.syrin_x.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val MusicCastIcon: ImageVector
    get() {
        if (_music_icon_cast != null) {
            return _music_icon_cast!!
        }
        _music_icon_cast = ImageVector.Builder(
            name = "Music_cast",
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
                quadToRelative(-66f, 0f, -113f, -47f)
                reflectiveQuadToRelative(-47f, -113f)
                reflectiveQuadToRelative(47f, -113f)
                reflectiveQuadToRelative(113f, -47f)
                quadToRelative(23f, 0f, 42.5f, 5.5f)
                reflectiveQuadTo(640f, 502f)
                verticalLineToRelative(-342f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(120f)
                horizontalLineTo(720f)
                verticalLineToRelative(360f)
                quadToRelative(0f, 66f, -47f, 113f)
                reflectiveQuadToRelative(-113f, 47f)
                moveTo(80f, 640f)
                quadToRelative(0f, -99f, 38f, -186.5f)
                reflectiveQuadTo(221f, 301f)
                reflectiveQuadToRelative(152.5f, -103f)
                reflectiveQuadTo(560f, 160f)
                verticalLineToRelative(80f)
                quadToRelative(-82f, 0f, -155f, 31.5f)
                reflectiveQuadToRelative(-127.5f, 86f)
                reflectiveQuadToRelative(-86f, 127f)
                reflectiveQuadTo(160f, 640f)
                close()
                moveToRelative(160f, 0f)
                quadToRelative(0f, -66f, 25.5f, -124.5f)
                reflectiveQuadToRelative(69f, -102f)
                reflectiveQuadTo(436f, 345f)
                reflectiveQuadToRelative(124f, -25f)
                verticalLineToRelative(80f)
                quadToRelative(-100f, 0f, -170f, 70f)
                reflectiveQuadToRelative(-70f, 170f)
                close()
            }
        }.build()
        return _music_icon_cast!!
    }

private var _music_icon_cast: ImageVector? = null
