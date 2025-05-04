package com.ile.syrin_x.utils

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object EnvLoader {
    private val dotenv: Dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    val spotifyClientId: String by lazy { dotenv["SPOTIFY_CLIENT_ID"] }
    val spotifyClientSecret: String by lazy { dotenv["SPOTIFY_CLIENT_SECRET"] }
    val soundCloudClientId: String by lazy { dotenv["SOUNDCLOUD_CLIENT_ID"] }
    val soundCloudClientSecret: String by lazy { dotenv["SOUNDCLOUD_CLIENT_SECRET"] }
    val stripeSecretKey: String by lazy { dotenv["STRIPE_SECRET_KEY"] }
    val stripePublishableKey: String by lazy { dotenv["STRIPE_PUBLISHABLE_KEY"] }
}
