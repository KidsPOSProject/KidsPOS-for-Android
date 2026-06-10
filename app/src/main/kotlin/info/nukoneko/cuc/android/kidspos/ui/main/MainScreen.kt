package info.nukoneko.cuc.android.kidspos.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.Item
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(state.toastMessageRes) {
        state.toastMessageRes?.let { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
            viewModel.onToastShown()
        }
    }

    val appName = stringResource(R.string.app_name)
    val modeLabel = stringResource(R.string.title_mode_format, state.mode.modeName)
    val testModeLabel = stringResource(R.string.title_test_mode)
    val titleSuffix = buildString {
        state.store?.name?.let { append(" [$it]") }
        append(" $modeLabel")
        if (state.demoMode) append(" $testModeLabel")
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.DrawerTitleSettings)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToSettings()
                    }
                )
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.DrawerTitleChangeStore)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.onChangeStoreClick()
                    }
                )
                if (state.demoMode) {
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.BetaDrawerTitleInputDummyItem)) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.onInsertDummyItem()
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(R.string.BetaDrawerTitleInputDummyStaff)) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.onInsertDummyStaff()
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("$appName$titleSuffix") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painterResource(R.drawable.ic_menu),
                                contentDescription = stringResource(R.string.navigation_drawer_open)
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                state.staff?.let { staff ->
                    Text(
                        stringResource(R.string.staff_name_format, staff.name),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                }
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(state.items) { item -> ItemRow(item) }
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.river_format, state.total),
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = viewModel::onAccountClick,
                        enabled = state.items.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.account))
                    }
                }
            }
        }
    }

    state.calculator?.let { calc ->
        CalculatorDialog(
            state = calc,
            onNumber = viewModel::onCalculatorNumber,
            onClear = viewModel::onCalculatorClear,
            onOk = viewModel::onCalculatorOk,
            onDismiss = viewModel::onCalculatorDismiss
        )
    }

    state.accountResult?.let { result ->
        AccountResultDialog(
            state = result,
            onOk = viewModel::onAccountResultOk,
            onBack = viewModel::onAccountResultBack
        )
    }

    state.storeSelection?.let { selection ->
        StoreSelectionDialog(
            state = selection,
            onSelect = viewModel::onStoreSelected,
            onReload = viewModel::onStoreSelectionReload,
            onDismiss = viewModel::onStoreSelectionDismiss
        )
    }

    val errorMessage = state.errorMessage ?: state.errorMessageRes?.let { stringResource(it) }
    errorMessage?.let { message ->
        ErrorDialog(message = message, onDismiss = viewModel::onErrorDismiss)
    }
}

@Composable
private fun ItemRow(item: Item) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(item.name)
        Text(stringResource(R.string.river_format, item.price))
    }
}

@Composable
private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.error)) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        }
    )
}

@Composable
private fun CalculatorDialog(
    state: CalculatorState,
    onNumber: (Int) -> Unit,
    onClear: () -> Unit,
    onOk: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Surface(
            shape = androidx.compose.material3.MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("${stringResource(R.string.total)}: ${stringResource(R.string.river_format, state.totalPrice)}")
                Text("${stringResource(R.string.deposit)}: ${stringResource(R.string.river_format, state.deposit)}")
                Spacer(modifier = Modifier.padding(8.dp))
                NumberPad(onNumber = onNumber, onClear = onClear)
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.back))
                    }
                    Button(onClick = onOk, enabled = state.accountEnabled) {
                        Text(stringResource(R.string.account))
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPad(onNumber: (Int) -> Unit, onClear: () -> Unit) {
    val rows = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    Column {
        rows.forEach { row ->
            Row {
                row.forEach { number ->
                    Button(
                        onClick = { onNumber(number) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                    ) {
                        Text("$number")
                    }
                }
            }
        }
        Row {
            Button(
                onClick = { onNumber(0) },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Text("0")
            }
            Button(
                onClick = onClear,
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            ) {
                Text(stringResource(R.string.delete))
            }
        }
    }
}

@Composable
private fun AccountResultDialog(
    state: AccountResultState,
    onOk: () -> Unit,
    onBack: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onBack,
        title = { Text(stringResource(R.string.account)) },
        text = {
            Column {
                Text("${stringResource(R.string.total)}: ${stringResource(R.string.river_format, state.totalPrice)}")
                Text("${stringResource(R.string.deposit)}: ${stringResource(R.string.river_format, state.deposit)}")
                Text("${stringResource(R.string.change)}: ${stringResource(R.string.river_format, state.change)}")
            }
        },
        confirmButton = {
            Button(onClick = onOk) { Text(stringResource(R.string.account)) }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onBack) {
                Text(stringResource(R.string.go_back))
            }
        }
    )
}

@Composable
private fun StoreSelectionDialog(
    state: StoreSelectionState,
    onSelect: (info.nukoneko.cuc.android.kidspos.entity.Store) -> Unit,
    onReload: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.DrawerTitleChangeStore)) },
        text = {
            when {
                state.loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
                state.failed -> Text(stringResource(R.string.store_fetch_failed))
                else -> Column {
                    state.stores.forEach { store ->
                        androidx.compose.material3.TextButton(
                            onClick = { onSelect(store) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(store.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (state.failed) {
                Button(onClick = onReload) { Text(stringResource(R.string.reload)) }
            } else {
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close))
                }
            }
        },
        dismissButton = {
            if (state.failed) {
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    )
}
