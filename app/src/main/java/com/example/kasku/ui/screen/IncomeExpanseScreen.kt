package com.example.kasku.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasku.viewmodel.TransactionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncomeExpanseUi(
    modifier: Modifier = Modifier,
    groupId: Int,
    isIncome: Boolean,
    onBack: () -> Unit = {},
    vm: TransactionViewModel = viewModel()
) {
    val nameInput by vm.nameInput.collectAsState()
    val suggestions by vm.nameSuggestion.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }

    // Format tanggal waktu
    val dateTimeText = remember { derivedStateOf {
        if (selectedDateMillis != null && selectedTime != null) {
            val date = Instant.ofEpochMilli(selectedDateMillis!!)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            val dateTime = LocalDateTime.of(date, selectedTime!!)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            dateTime.format(formatter)
        } else {
            "Pilih tanggal dan waktu"
        }
    }}

    val primaryColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F)
    val title = if (isIncome) "Tambah Pemasukan" else "Tambah Pengeluaran"

    // Date Picker State
    val datePickerState = rememberDatePickerState()

    // Time Picker State
    val timePickerState = rememberTimePickerState(
        initialHour = LocalTime.now().hour,
        initialMinute = LocalTime.now().minute,
        is24Hour = true
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
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
                .background(Color(0xFFFAFAFA))
        ) {
            // Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isIncome) "Catat pemasukan kas" else "Catat pengeluaran kas",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Anggota
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = {
                        vm.onNameChange(it)
                        expanded = it.isNotBlank()
                    },
                    label = { Text("Nama Anggota") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, null, tint = primaryColor)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (expanded && suggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column {
                            suggestions.forEach { name ->
                                TextButton(
                                    onClick = {
                                        vm.selectName(name)
                                        expanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        text = name,
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Nominal
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Nominal") },
                    leadingIcon = {
                        Text(
                            "Rp",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Keterangan
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Keterangan (Opsional)") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, null, tint = primaryColor)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Tanggal & Waktu
                OutlinedTextField(
                    value = dateTimeText.value,
                    onValueChange = {},
                    label = { Text("Tanggal & Waktu") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, null, tint = primaryColor)
                    },
                    trailingIcon = {
                        Icon(Icons.Default.Schedule, null, tint = primaryColor)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Tombol Simpan
                Button(
                    onClick = {
                        if (nameInput.isEmpty() || amount.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Nama dan nominal harus diisi!"
                                )
                            }
                            return@Button
                        }

                        if (selectedDateMillis == null || selectedTime == null) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Tanggal dan waktu harus dipilih!"
                                )
                            }
                            return@Button
                        }

                        val amountValue = amount.toDoubleOrNull() ?: 0.0
                        if (amountValue <= 0) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Nominal harus lebih dari 0!"
                                )
                            }
                            return@Button
                        }

                        // Format untuk database
                        val date = Instant.ofEpochMilli(selectedDateMillis!!)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val dateTime = LocalDateTime.of(date, selectedTime!!)
                        val dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        val dbDateText = dateTime.format(dbFormatter)

                        if (isIncome) {
                            vm.addIncome(
                                name = nameInput,
                                amount = amountValue,
                                groupId = groupId,
                                date = dbDateText,
                                description = if (description.isEmpty()) "-" else description
                            )
                        } else {
                            vm.addExpanse(
                                name = nameInput,
                                amount = amountValue,
                                groupId = groupId,
                                date = dbDateText,
                                description = if (description.isEmpty()) "-" else description
                            )
                        }

                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "${if (isIncome) "Pemasukan" else "Pengeluaran"} berhasil disimpan"
                            )
                            delay(1500)
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Simpan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (datePickerState.selectedDateMillis != null) {
                                selectedDateMillis = datePickerState.selectedDateMillis
                                showDatePicker = false
                                showTimePicker = true
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Pilih tanggal terlebih dahulu"
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = primaryColor
                        )
                    ) {
                        Text("PILIH")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text("BATAL")
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color.White
                    )
                )
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            selectedTime = LocalTime.of(
                                timePickerState.hour,
                                timePickerState.minute
                            )
                            showTimePicker = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = primaryColor
                        )
                    ) {
                        Text("SIMPAN", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showTimePicker = false },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text("BATAL")
                    }
                },
                containerColor = Color.White,
                title = {
                    Text(
                        "Pilih Waktu",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    TimePicker(state = timePickerState)
                }
            )
        }
    }
}
