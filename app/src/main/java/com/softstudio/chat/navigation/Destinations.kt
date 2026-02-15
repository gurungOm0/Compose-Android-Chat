package com.softstudio.chat.navigation

interface Destinations {
    val route: String
}

object AuthenticationGroupDes: Destinations{
    override val route: String = "authGroup"
}

object AuthenticationScreenDes: Destinations {
    override val route = "auth"
}

object OnBoardingDes: Destinations {
    override val route = "onBoardingA"
}

object HomeDes: Destinations {
    override val route = "home"
}

object ChatDes: Destinations {
    override val route = "chat"
}

object ProfileDes: Destinations {
    override val route = "profile"
}

object SettingsDes: Destinations {
    override val route = "setting"
}