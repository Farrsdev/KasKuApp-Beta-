package com.example.kasku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.kasku.ui.screen.EditGroupUi
import com.example.kasku.ui.screen.HistoryUI
import com.example.kasku.ui.screen.HomeGroupUi
import com.example.kasku.ui.screen.HomeUi
import com.example.kasku.ui.screen.IncomeExpanseUi
import com.example.kasku.ui.theme.KasKuTheme
import com.example.kasku.viewmodel.GroupViewModel

@Composable
fun NavGraph(nc: NavHostController) {
    NavHost(
        navController = nc,
        startDestination = "home"
    ){
        composable("home") { homeEntry ->
            val groupViewModel: GroupViewModel = viewModel(homeEntry)
            HomeUi(
                vm = groupViewModel,
                onGoToGroup = { groupId ->
                    nc.navigate("home_group/$groupId")
                },
                onEditGroup = { groupId ->
                    nc.navigate("edit_group/$groupId")
                }
            )
        }

        composable("home_group/{groupId}") { bse ->
            val groupId = bse.arguments?.getString("groupId")?.toIntOrNull()

            if(groupId != null){
                HomeGroupUi(
                    groupId = groupId,
                    onBackToHome = {
                        nc.popBackStack()
                    },
                    onGoToIncExp = {isIncome ->
                        val flag = if(isIncome) 1 else 0
                        nc.navigate("income_expanse/$groupId/$flag")
                    },
                    onNavigateToDetailHistory = { id ->
                        nc.navigate("group_history/$id")
                    }
                )
            }
        }

        composable("income_expanse/{groupId}/{isIncome}") { bse->
            val groupId = bse.arguments?.getString("groupId")?.toIntOrNull()
            val isIncome = bse.arguments?.getString("isIncome") == "1"

            if(groupId != null){
                IncomeExpanseUi(
                    groupId = groupId,
                    isIncome = isIncome,
                    onBack = {
                        nc.popBackStack()
                    }
                )
            }
        }

        composable("edit_group/{groupId}") { bse ->
            val groupId = bse.arguments?.getString("groupId")?.toIntOrNull()
            val parentEntry = remember(bse) {
                nc.getBackStackEntry("home")
            }

            val groupViewModel: GroupViewModel = viewModel(parentEntry)
            if(groupId !=   null){
                EditGroupUi(
                    groupId = groupId,
                    onBack = {
                        nc.popBackStack()
                    },
                    vm = groupViewModel
                )
            }
        }

        composable("group_history/{groupId}") { bse ->
            val groupId = bse.arguments?.getString("groupId")?.toIntOrNull()

            HistoryUI(
                groupId = groupId!!,
                onBack = {
                    nc.popBackStack()
                }
            )
        }
    }

}