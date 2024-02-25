package com.spilios.adoptapet

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object SignUp: Screen("signup")
    object Details: Screen("details")
    object Account: Screen("account")
}
