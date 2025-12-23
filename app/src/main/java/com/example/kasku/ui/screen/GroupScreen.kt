package com.example.kasku.ui.screen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasku.R
import com.example.kasku.entity.Transaction
import com.example.kasku.ui.screen.ui.theme.KasKuTheme
import com.example.kasku.viewmodel.GroupViewModel
import com.example.kasku.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeGroupUi(
    modifier: Modifier = Modifier,
    groupId: Int,
    vm: GroupViewModel = viewModel(),
    transactionVm: TransactionViewModel = viewModel(),
    onBackToHome: () -> Unit,
    onGoToIncExp: (Boolean) -> Unit = {},
    onNavigateToDetailHistory: (Int) -> Unit = {},
) {
    val group by vm.group.collectAsState()
    val totalKas by transactionVm.totalMoney.collectAsState()
    val totalIncome by transactionVm.totalIncome.collectAsState()
    val totalExpense by transactionVm.totalExpanse.collectAsState()
    val history by transactionVm.history.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(groupId) {
        vm.getGroupById(groupId)
        transactionVm.getTotalMoney(groupId)
        transactionVm.getTotalIncome(groupId)
        transactionVm.getTotalExpanse(groupId)
        transactionVm.setGroupId(groupId)
    }

    if (group == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = "Loading",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Gray
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Memuat data grup...",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    } else {
        val primaryColor = randomTopBarColor()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(280.dp)
                ) {
                    // Drawer Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(primaryColor)
                            .padding(vertical = 24.dp, horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AttachMoney,
                                contentDescription = "Group",
                                modifier = Modifier.size(32.dp),
                                tint = primaryColor
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = group!!.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                        Text(
                            text = "Kelola Keuangan",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // Navigation Items - seperti menu biasa
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        // Item untuk mencatat pemasukan
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    onGoToIncExp(true)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Income",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Catat Pemasukan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    "Tambah pemasukan kas",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    onGoToIncExp(false)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Remove,
                                    contentDescription = "Expense",
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Catat Pengeluaran",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    "Tambah pengeluaran kas",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    onNavigateToDetailHistory(groupId)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE3F2FD)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = "History",
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Detail History",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    "Lihat semua transaksi",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Divider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 8.dp),
                            thickness = 0.5.dp,
                            color = Color.LightGray.copy(alpha = 0.3f)
                        )

                        // Item untuk kembali ke home
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { drawerState.close() }
                                    onBackToHome()
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Kembali ke Home",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Text(
                                    "Ke halaman utama",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            if (drawerState.isClosed) drawerState.open()
                                            else drawerState.close()
                                        }
                                    },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        group!!.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Text(
                                        "Detail Keuangan",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = primaryColor
                        )
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFF8F9FA))
                ) {
                    // Summary Cards
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "SALDO KAS",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    letterSpacing = 1.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = formatCurrency(totalKas ?: 0.0),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((totalKas ?: 0.0) >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Total keseluruhan keuangan grup",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE8F5E9)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.TrendingUp,
                                            contentDescription = "Income",
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "PEMASUKAN",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = formatCurrency(totalIncome ?: 0.0),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }

                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFEBEE)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.TrendingDown,
                                            contentDescription = "Expense",
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "PENGELUARAN",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = formatCurrency(totalExpense ?: 0.0),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFC62828)
                                    )
                                }
                            }
                        }
                    }


                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = "History",
                                        tint = Color(0xFF08B333),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = "AKTIVITAS TERBARU",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                }

                                if (history.isNotEmpty()) {
                                    TextButton(
                                        onClick = {
                                            onNavigateToDetailHistory(groupId)
                                        },
                                        modifier = Modifier.padding(start = 8.dp)
                                    ) {
                                        Text(
                                            "Lihat Semua",
                                            fontSize = 12.sp,
                                            color = Color(0xFF08B333),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Divider(
                                color = Color.LightGray.copy(alpha = 0.3f)
                            )

                            if (history.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        Icons.Default.History,
                                        contentDescription = "No History",
                                        modifier = Modifier.size(60.dp),
                                        tint = Color.LightGray
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        text = "Belum ada transaksi",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "Buka menu untuk mencatat transaksi pertama",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    items(history.take(10)) { transaction ->
                                        TransactionItem(transaction = transaction)
                                    }

                                    item {
                                        if (history.size > 15) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    "Menampilkan 10 dari ${history.size} transaksi",
                                                    fontSize = 12.sp,
                                                    color = Color.Gray,
                                                    modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                                Button(
                                                    onClick = {
                                                        onNavigateToDetailHistory(groupId)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color(0xFF08B333),
                                                        contentColor = Color.White
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 32.dp),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Text("Lihat Semua Transaksi")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "Buka menu untuk mencatat transaksi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp) // Reduced size from 44.dp to 36.dp
                    .background(
                        color = if (transaction.amount > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (transaction.amount > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (transaction.amount > 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp)) // Reduced spacing

            Column {
                Text(
                    transaction.description ?: if (transaction.amount > 0) "Pemasukan" else "Pengeluaran",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    formatDate(transaction.date),
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                formatCurrency(abs(transaction.amount)),
                fontSize = 14.sp, // Reduced font size
                fontWeight = FontWeight.SemiBold,
                color = if (transaction.amount > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
            Text(
                if (transaction.amount > 0) "Pemasukan" else "Pengeluaran",
                fontSize = 10.sp, // Reduced font size
                color = Color.Gray,
                modifier = Modifier.padding(top = 1.dp) // Reduced padding
            )
        }
    }
}

fun formatDate(dateTime:String) : String{
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val parsed = LocalDateTime.parse(dateTime, inputFormatter)
    return parsed.format(outputFormatter)
}
fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}

fun randomTopBarColor(): Color {
    val colors = listOf(
        Color(0xFF08B333),  // Hijau
        Color(0xFF1E88E5),  // Biru
        Color(0xFFF4511E),  // Oranye
        Color(0xFF6A1B9A),  // Ungu
        Color(0xFF0D47A1),  // Biru Tua
        Color(0xFF00695C),  // Teal
        Color(0xFFE65100),  // Oranye Tua
    )
    return colors.random()
}

@Preview(showBackground = true)
@Composable
private fun Prev() {
    KasKuTheme {
        HomeGroupUi(
            groupId = 1,
            onBackToHome = {},
            onGoToIncExp = {}
        )
    }
}