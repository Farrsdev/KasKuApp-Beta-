package com.example.kasku.ui.screen

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasku.entity.Transaction
import com.example.kasku.ui.screen.ui.theme.KasKuTheme
import com.example.kasku.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryUI(
    modifier: Modifier = Modifier,
    groupId: Int,
    transactionVm: TransactionViewModel = viewModel(),
    onBack: () -> Unit
) {
    val history by transactionVm.history.collectAsState()
    val incomeHistory by transactionVm.incomeHistory.collectAsState()
    val expanseHistory by transactionVm.expanseHistory.collectAsState()

    var selectedFilter by remember { mutableIntStateOf(0) } // 0: Semua, 1: Income, 2: Expanse
    var selectedSort by remember { mutableIntStateOf(0) } // 0: Terbaru, 1: Terlama
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        transactionVm.setGroupId(groupId)
    }

    val filteredAndSortedTransactions = remember(history, incomeHistory, expanseHistory, selectedFilter, selectedSort) {
        val baseList = when (selectedFilter) {
            1 -> incomeHistory
            2 -> expanseHistory
            else -> history
        }

        when (selectedSort) {
            0 -> baseList.sortedByDescending { LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) }
            1 -> baseList.sortedBy { LocalDateTime.parse(it.date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) }
            else -> baseList
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detail History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = randomTopBarColor(),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    // Sort Button
                    Box {
                        IconButton(
                            onClick = { isSortMenuExpanded = true }
                        ) {
                            Icon(
                                Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = isSortMenuExpanded,
                            onDismissRequest = { isSortMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Terbaru")
                                        Spacer(Modifier.weight(1f))
                                        if (selectedSort == 0) {
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedSort = 0
                                    isSortMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Terlama")
                                        Spacer(Modifier.weight(1f))
                                        if (selectedSort == 1) {
                                            Icon(
                                                Icons.Default.ArrowDropDown,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedSort = 1
                                    isSortMenuExpanded = false
                                }
                            )
                        }
                    }

                    // Filter Button
                    Box {
                        IconButton(
                            onClick = { isFilterMenuExpanded = true }
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = isFilterMenuExpanded,
                            onDismissRequest = { isFilterMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Money,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFF4CAF50)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Semua")
                                    }
                                },
                                onClick = {
                                    selectedFilter = 0
                                    isFilterMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.TrendingUp,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFF2E7D32)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Income")
                                    }
                                },
                                onClick = {
                                    selectedFilter = 1
                                    isFilterMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.TrendingDown,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFFC62828)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text("Expanse")
                                    }
                                },
                                onClick = {
                                    selectedFilter = 2
                                    isFilterMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == 0,
                    onClick = { selectedFilter = 0 },
                    label = { Text("Semua") },
                    leadingIcon = if (selectedFilter == 0) {
                        {
                            Icon(
                                Icons.Default.Money,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF08B333),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = selectedFilter == 1,
                    onClick = { selectedFilter = 1 },
                    label = { Text("Income") },
                    leadingIcon = if (selectedFilter == 1) {
                        {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF2E7D32),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = selectedFilter == 2,
                    onClick = { selectedFilter = 2 },
                    label = { Text("Expanse") },
                    leadingIcon = if (selectedFilter == 2) {
                        {
                            Icon(
                                Icons.Default.TrendingDown,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFC62828),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Sort Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (selectedSort == 0) "Diurutkan: Terbaru" else "Diurutkan: Terlama",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${filteredAndSortedTransactions.size} transaksi",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (filteredAndSortedTransactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
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
                            Icons.Default.History,
                            contentDescription = "No History",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Gray
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Tidak ada transaksi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = when (selectedFilter) {
                            1 -> "Belum ada transaksi pemasukan"
                            2 -> "Belum ada transaksi pengeluaran"
                            else -> "Belum ada transaksi yang tercatat"
                        },
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(filteredAndSortedTransactions) { transaction ->
                        HistoryTransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryTransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (transaction.amount > 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.amount > 0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (transaction.amount > 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        transaction.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Spacer(Modifier.height(4.dp))

                    if (!transaction.description.isNullOrBlank()) {
                        Text(
                            transaction.description!!,
                            fontSize = 13.sp,
                            color = Color.Gray,
                            maxLines = 1
                        )
                        Spacer(Modifier.height(2.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            formatDateTime(transaction.date),
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    formatCurrency(abs(transaction.amount)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.amount > 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Text(
                    if (transaction.amount > 0) "Pemasukan" else "Pengeluaran",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

fun formatDateTime(dateTime: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm")

    val parsed = LocalDateTime.parse(dateTime, inputFormatter)
    return parsed.format(outputFormatter)
}