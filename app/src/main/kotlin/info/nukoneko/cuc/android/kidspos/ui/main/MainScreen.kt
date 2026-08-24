package info.nukoneko.cuc.android.kidspos.ui.main

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.nukoneko.cuc.android.kidspos.R
import info.nukoneko.cuc.android.kidspos.entity.Item
import info.nukoneko.cuc.android.kidspos.entity.Store
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

    LaunchedEffect(state.toastMessageRes) {
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
                    // 閉じるアニメーションの途中で画面を離れると開いた状態が復元されるため、
                    // 閉じ終えてから遷移する
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            onNavigateToSettings()
                        }
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
                NavigationDrawerItem(
                    label = { Text(stringResource(R.string.DrawerTitleManualItemSelection)) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        viewModel.onManualItemSelectionClick()
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

    state.itemSelection?.let { selection ->
        ItemSelectionDialog(
            state = selection,
            total = state.total,
            onSelect = viewModel::onManualItemSelected,
            onReload = viewModel::onItemSelectionReload,
            onDismiss = viewModel::onItemSelectionDismiss
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
    AlertDialog(
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

internal const val CalculatorDialogTag = "calculator_dialog"
internal const val CalculatorSummaryTag = "calculator_summary"
internal const val CalculatorAccountButtonTag = "calculator_account_button"
internal const val AccountResultConfirmButtonTag = "account_result_confirm_button"
internal const val AccountResultProgressTag = "account_result_progress"

private val DialogMaxWidth = 720.dp
private val DialogPadding = 24.dp
private val NumberPadMaxWidth = 360.dp
private val NumberPadMinWidth = 156.dp
private val TwoPaneMinWidth = 600.dp
private val KeyGap = 4.dp
private val AmountSafetyMargin = 8.dp
private val SummaryGapHeight = 40.dp
private val PaneGap = 24.dp
private val ProgressIndicatorSize = 24.dp
private const val NumberPadRows = 4
private const val NumberPadColumns = 3
private const val AmountBlockCount = 3
private const val SinglePaneSummaryRatio = 0.45f

// 高さが足りない端末では横幅ではなく高さがキーの大きさを決める
private fun fittingPadWidth(availableWidth: Dp, availableHeight: Dp): Dp {
    val byWidth = min(availableWidth, NumberPadMaxWidth)
    val byHeight = availableHeight * (NumberPadColumns.toFloat() / NumberPadRows)
    return min(byWidth, byHeight).coerceAtLeast(min(NumberPadMinWidth, byWidth))
}

// タブレットでも文字が切れないよう、実測して収まる最大のスタイルを選ぶ
@Composable
private fun fittingStyle(samples: List<String>, candidates: List<TextStyle>, maxWidth: Dp): TextStyle {
    val measurer = rememberTextMeasurer()
    val maxWidthPx = with(LocalDensity.current) { maxWidth.coerceAtLeast(0.dp).toPx() }
    return candidates.firstOrNull { style ->
        samples.all { sample ->
            measurer.measure(text = sample, style = style, softWrap = false, maxLines = 1)
                .size.width <= maxWidthPx
        }
    } ?: candidates.last()
}

// 横幅だけでなく、3段の金額とラベルが縦にも収まるスタイルを選ぶ
@Composable
private fun fittingAmountStyle(
    samples: List<String>,
    candidates: List<TextStyle>,
    maxWidth: Dp,
    maxHeight: Dp
): TextStyle {
    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val maxWidthPx = with(density) { maxWidth.coerceAtLeast(0.dp).toPx() }
    val labelsHeightPx = measurer
        .measure(text = "0", style = MaterialTheme.typography.titleMedium, maxLines = 1)
        .size.height * AmountBlockCount
    val amountBudgetPx = with(density) {
        (maxHeight - SummaryGapHeight).coerceAtLeast(0.dp).toPx()
    } - labelsHeightPx
    return candidates.firstOrNull { style ->
        val measured = samples.map {
            measurer.measure(text = it, style = style, softWrap = false, maxLines = 1)
        }
        measured.all { it.size.width <= maxWidthPx } &&
            measured.maxOf { it.size.height } * AmountBlockCount <= amountBudgetPx
    } ?: candidates.last()
}

@Composable
private fun amountStyleCandidates(): List<TextStyle> {
    val typography = MaterialTheme.typography
    return listOf(
        typography.displayMedium,
        typography.displaySmall,
        typography.headlineLarge,
        typography.headlineMedium,
        typography.headlineSmall,
        typography.titleLarge
    ).map { it.copy(fontWeight = FontWeight.Bold) }
}

@Composable
private fun CalculatorDialog(
    state: CalculatorState,
    onNumber: (Int) -> Unit,
    onClear: () -> Unit,
    onOk: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
            val contentWidth = min(maxWidth, DialogMaxWidth)
            val availableHeight = maxHeight
            val innerWidth = contentWidth - DialogPadding * 2
            val twoPane = contentWidth >= TwoPaneMinWidth

            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .width(contentWidth)
                    .heightIn(max = availableHeight)
                    .testTag(CalculatorDialogTag)
            ) {
                Column(modifier = Modifier.padding(DialogPadding)) {
                    // フッターを先に測らせ、その残りの高さでキーパッドの大きさを決める
                    BoxWithConstraints(modifier = Modifier.weight(1f, fill = false)) {
                        val bodyHeight = maxHeight
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            if (twoPane) {
                                val padWidth = fittingPadWidth(innerWidth - PaneGap, bodyHeight)
                                val summaryWidth = innerWidth - padWidth - PaneGap
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    CalculatorSummary(
                                        state = state,
                                        maxWidth = summaryWidth,
                                        maxHeight = bodyHeight,
                                        modifier = Modifier.width(summaryWidth)
                                    )
                                    Spacer(modifier = Modifier.width(PaneGap))
                                    NumberPad(
                                        onNumber = onNumber,
                                        onClear = onClear,
                                        modifier = Modifier.width(padWidth)
                                    )
                                }
                            } else {
                                val summaryHeight = bodyHeight * SinglePaneSummaryRatio
                                CalculatorSummary(
                                    state = state,
                                    maxWidth = innerWidth,
                                    maxHeight = summaryHeight,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.padding(12.dp))
                                NumberPad(
                                    onNumber = onNumber,
                                    onClear = onClear,
                                    modifier = Modifier
                                        .width(
                                            fittingPadWidth(
                                                innerWidth,
                                                bodyHeight - summaryHeight - PaneGap
                                            )
                                        ).align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.padding(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.back),
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1
                            )
                        }
                        Button(
                            onClick = onOk,
                            enabled = state.accountEnabled,
                            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 16.dp),
                            modifier = Modifier.testTag(CalculatorAccountButtonTag)
                        ) {
                            Text(
                                text = stringResource(R.string.account),
                                style = MaterialTheme.typography.headlineSmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorSummary(
    state: CalculatorState,
    maxWidth: Dp,
    maxHeight: Dp,
    modifier: Modifier = Modifier
) {
    val remaining = state.deposit - state.totalPrice
    val totalText = stringResource(R.string.river_format, state.totalPrice)
    val depositText = stringResource(R.string.river_format, state.deposit)
    val remainingText = stringResource(
        R.string.river_format,
        if (remaining >= 0) remaining else -remaining
    )
    val amountStyle = fittingAmountStyle(
        samples = listOf(totalText, depositText, remainingText),
        candidates = amountStyleCandidates(),
        maxWidth = maxWidth - AmountSafetyMargin,
        maxHeight = maxHeight
    )
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(modifier = modifier.testTag(CalculatorSummaryTag)) {
        AmountBlock(
            label = stringResource(R.string.total),
            amount = totalText,
            style = amountStyle,
            color = onSurface
        )
        Spacer(modifier = Modifier.padding(6.dp))
        AmountBlock(
            label = stringResource(R.string.deposit),
            amount = depositText,
            style = amountStyle,
            color = onSurface
        )
        Spacer(modifier = Modifier.padding(6.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(6.dp))
        AmountBlock(
            label = stringResource(if (remaining >= 0) R.string.change else R.string.shortage),
            amount = remainingText,
            style = amountStyle,
            color = if (remaining >= 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )
    }
}

@Composable
private fun AmountBlock(label: String, amount: String, style: TextStyle, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = amount,
                style = style,
                color = color,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
        }
    }
}

@Composable
private fun NumberPad(onNumber: (Int) -> Unit, onClear: () -> Unit, modifier: Modifier = Modifier) {
    val rows = listOf(
        listOf(1, 2, 3),
        listOf(4, 5, 6),
        listOf(7, 8, 9)
    )
    val deleteLabel = stringResource(R.string.delete)
    BoxWithConstraints(modifier = modifier) {
        val typography = MaterialTheme.typography
        val keyWidth = maxWidth / 3 - KeyGap * 2
        val keyStyle = fittingStyle(
            samples = listOf("8", deleteLabel),
            candidates = listOf(
                typography.headlineLarge,
                typography.headlineMedium,
                typography.headlineSmall,
                typography.titleLarge
            ),
            maxWidth = keyWidth * 0.85f
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    row.forEach { number ->
                        NumberPadButton(
                            text = "$number",
                            style = keyStyle,
                            onClick = { onNumber(number) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                NumberPadButton(
                    text = "0",
                    style = keyStyle,
                    onClick = { onNumber(0) },
                    modifier = Modifier.weight(1f)
                )
                NumberPadButton(
                    text = deleteLabel,
                    style = keyStyle,
                    onClick = onClear,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NumberPadButton(
    text: String,
    style: TextStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .padding(KeyGap)
            .aspectRatio(1f)
    ) {
        Text(
            text = text,
            style = style,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun AccountResultDialog(
    state: AccountResultState,
    onOk: () -> Unit,
    onBack: () -> Unit
) {
    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(modifier = Modifier.padding(16.dp)) {
            val typography = MaterialTheme.typography
            val contentWidth = min(maxWidth, DialogMaxWidth)
            val availableHeight = maxHeight
            val changeText = stringResource(R.string.river_format, state.change)
            val changeStyle = fittingStyle(
                samples = listOf(changeText),
                candidates = listOf(
                    typography.displayLarge,
                    typography.displayMedium,
                    typography.displaySmall,
                    typography.headlineLarge,
                    typography.headlineMedium,
                    typography.headlineSmall
                ).map { it.copy(fontWeight = FontWeight.Bold) },
                maxWidth = contentWidth - 64.dp
            )

            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .width(contentWidth)
                    .heightIn(max = availableHeight)
            ) {
                Column(modifier = Modifier.padding(32.dp)) {
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.change),
                            style = typography.headlineMedium,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = changeText,
                            style = changeStyle,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip
                        )
                        Spacer(modifier = Modifier.padding(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.padding(6.dp))
                        ResultSummaryRow(
                            label = stringResource(R.string.total),
                            amount = stringResource(R.string.river_format, state.totalPrice)
                        )
                        ResultSummaryRow(
                            label = stringResource(R.string.deposit),
                            amount = stringResource(R.string.river_format, state.deposit)
                        )
                    }
                    Spacer(modifier = Modifier.padding(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = onBack,
                            enabled = !state.processing,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.go_back),
                                style = typography.titleLarge,
                                maxLines = 1
                            )
                        }
                        Button(
                            onClick = onOk,
                            enabled = !state.processing,
                            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 16.dp),
                            modifier = Modifier.testTag(AccountResultConfirmButtonTag)
                        ) {
                            if (state.processing) {
                                CircularProgressIndicator(
                                    strokeWidth = 3.dp,
                                    color = LocalContentColor.current,
                                    modifier = Modifier
                                        .size(ProgressIndicatorSize)
                                        .testTag(AccountResultProgressTag)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Text(
                                text = stringResource(R.string.account),
                                style = typography.headlineSmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultSummaryRow(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.titleLarge, maxLines = 1)
        Text(
            text = amount,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun ItemSelectionDialog(
    state: ItemSelectionState,
    total: Int,
    onSelect: (Item) -> Unit,
    onReload: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    stringResource(R.string.DrawerTitleManualItemSelection),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.padding(4.dp))
                when {
                    state.loading -> Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    state.failed -> Text(stringResource(R.string.item_fetch_failed))
                    state.items.isEmpty() -> Text(stringResource(R.string.item_selection_empty))
                    else -> LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        modifier = Modifier.heightIn(max = 360.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            ItemSelectionCell(item = item, onClick = { onSelect(item) })
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(4.dp))
                HorizontalDivider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.total)}: ${stringResource(R.string.river_format, total)}",
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        if (state.failed) {
                            TextButton(onClick = onReload) {
                                Text(stringResource(R.string.reload))
                            }
                        }
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.close))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemSelectionCell(item: Item, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(item.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(stringResource(R.string.river_format, item.price))
        }
    }
}

@Composable
private fun StoreSelectionDialog(
    state: StoreSelectionState,
    onSelect: (Store) -> Unit,
    onReload: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.DrawerTitleChangeStore)) },
        text = {
            when {
                state.loading -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                state.failed -> Text(stringResource(R.string.store_fetch_failed))
                else -> Column {
                    state.stores.forEach { store ->
                        TextButton(
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
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close))
                }
            }
        },
        dismissButton = {
            if (state.failed) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    )
}
