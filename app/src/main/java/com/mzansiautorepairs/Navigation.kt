package com.mzansiautorepairs

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mzansiautorepairs.screens.CustomerScreen
import com.mzansiautorepairs.screens.DashboardScreen
import com.mzansiautorepairs.screens.JobCardDetailScreen
import com.mzansiautorepairs.screens.JobCardScreen
import com.mzansiautorepairs.screens.LoginScreen
import com.mzansiautorepairs.screens.RegisterScreen
import com.mzansiautorepairs.screens.ServiceScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val CUSTOMERS = "customers"
    const val JOBCARDS = "jobcards"
    const val JOBCARD_DETAIL = "jobcard/{jobId}"
    const val SERVICES = "services"
    fun jobCardDetail(id: String) = "jobcard/$id"
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController = navController)
        }
        composable(Routes.CUSTOMERS) {
            CustomerScreen(navController = navController)
        }
        composable(Routes.JOBCARDS) {
            JobCardScreen(navController = navController)
        }
        composable(
            route = Routes.JOBCARD_DETAIL,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType })
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobCardDetailScreen(navController = navController, jobId = jobId)
        }
        composable(Routes.SERVICES) {
            ServiceScreen(navController = navController)
        }
    }
}
