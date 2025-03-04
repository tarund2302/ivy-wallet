package com.ivy.core.ui.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.data.transaction.DueSectionUi
import com.ivy.core.ui.data.transaction.TransactionUi
import com.ivy.core.ui.data.transaction.TransactionsListUi
import com.ivy.core.ui.data.transaction.TrnListItemUi
import com.ivy.core.ui.transaction.card.Card
import com.ivy.core.ui.transaction.card.DueActions
import com.ivy.core.ui.transaction.card.dummyDueActions
import com.ivy.core.ui.transaction.handling.ExpandCollapseHandler
import com.ivy.core.ui.transaction.handling.TrnItemClickHandler
import com.ivy.core.ui.transaction.handling.defaultExpandCollapseHandler
import com.ivy.core.ui.transaction.handling.defaultTrnItemClickHandler
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.B1
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.IconRes
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.util.IvyPreview
import com.ivy.resources.R

// region EmptyState data
@Immutable
data class EmptyState(
    val title: String,
    val description: String,
)

@Composable
fun defaultEmptyState() = EmptyState(
    title = stringResource(R.string.no_transactions),
    description = stringResource(R.string.no_transactions_desc)
)
// endregion

internal fun LazyListScope.transactionsList(
    trnsList: TransactionsListUi,
    emptyState: EmptyState,

    upcomingHandler: ExpandCollapseHandler,
    overdueHandler: ExpandCollapseHandler,
    dueActions: DueActions?,
    trnClickHandler: TrnItemClickHandler
) {
    dueSection(
        section = trnsList.upcoming,
        handler = upcomingHandler,
        trnClickHandler = trnClickHandler,
        dueActions = dueActions
    )
    dueSection(
        section = trnsList.overdue,
        handler = overdueHandler,
        trnClickHandler = trnClickHandler,
        dueActions = dueActions
    )
    history(history = trnsList.history, trnClickHandler = trnClickHandler)

    val isEmpty by derivedStateOf {
        trnsList.history.isEmpty() && trnsList.upcoming == null && trnsList.overdue == null
    }
    if (isEmpty) {
        emptyState(emptyState)
    }
}


private fun LazyListScope.dueSection(
    section: DueSectionUi?,
    handler: ExpandCollapseHandler,
    trnClickHandler: TrnItemClickHandler,
    dueActions: DueActions?,
) {
    if (section != null) {
        item {
            SpacerVer(height = 24.dp)
            section.SectionDivider(
                expanded = handler.expanded,
                setExpanded = handler.setExpanded,
            )
        }
        if (handler.expanded) {
            dueTrns(
                trns = section.trns,
                dueActions = dueActions,
                trnClickHandler = trnClickHandler
            )
        }
    }
}

private fun LazyListScope.dueTrns(
    trns: List<TransactionUi>,
    trnClickHandler: TrnItemClickHandler,
    dueActions: DueActions?,
) {
    items(
        items = trns,
        key = { it.id }
    ) { trn ->
        SpacerVer(height = 12.dp)
        trn.Card(
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = trnClickHandler.onTrnClick,
            onAccountClick = trnClickHandler.onAccountClick,
            onCategoryClick = trnClickHandler.onCategoryClick,
            dueActions = dueActions
        )
    }
}

private fun LazyListScope.history(
    history: List<TrnListItemUi>,
    trnClickHandler: TrnItemClickHandler
) {
    itemsIndexed(
        items = history,
        key = { _, item ->
            when (item) {
                is TrnListItemUi.DateDivider -> item.date
                is TrnListItemUi.Trn -> item.trn.id
                is TrnListItemUi.Transfer -> item.batchId
            }
        }
    ) { index, item ->
        when (item) {
            is TrnListItemUi.DateDivider -> {
                SpacerVer(
                    // the first date divider require less margin
                    height = if (index > 0 && history[index - 1] !is TrnListItemUi.DateDivider)
                        32.dp else 24.dp
                )
                item.DateDivider()
            }
            is TrnListItemUi.Trn -> {
                SpacerVer(height = 12.dp)
                item.trn.Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = trnClickHandler.onTrnClick,
                    onAccountClick = trnClickHandler.onAccountClick,
                    onCategoryClick = trnClickHandler.onCategoryClick,
                )
            }
            is TrnListItemUi.Transfer -> {
                SpacerVer(height = 12.dp)
                item.Card(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = trnClickHandler.onTransferClick
                )
            }
        }
    }
}

private fun LazyListScope.emptyState(emptyState: EmptyState) {
    item {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconRes(icon = R.drawable.ic_notransactions, tint = UI.colors.neutral)
            SpacerVer(height = 24.dp)
            B1(
                text = emptyState.title,
                modifier = Modifier.fillMaxWidth(),
                color = UI.colors.neutral,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
            )
            SpacerVer(height = 8.dp)
            B2(
                text = emptyState.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                color = UI.colors.neutral,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// region Previews
@Preview
@Composable
private fun Preview_Full() {
    IvyPreview {
        val upcomingHandler = defaultExpandCollapseHandler()
        val overdueHandler = defaultExpandCollapseHandler()
        val emptyState = defaultEmptyState()
        val trnClickHandler = defaultTrnItemClickHandler()

        val trnsList = sampleTransactionListUi()

        LazyColumn {
            transactionsList(
                trnsList = trnsList,
                emptyState = emptyState,
                upcomingHandler = upcomingHandler,
                overdueHandler = overdueHandler,
                dueActions = dummyDueActions(),
                trnClickHandler = trnClickHandler,
            )
        }
    }
}

@Preview
@Composable
private fun Preview_EmptyState() {
    IvyPreview {
        val upcomingHandler = defaultExpandCollapseHandler()
        val overdueHandler = defaultExpandCollapseHandler()
        val emptyState = defaultEmptyState()
        val trnItemClickHandler = defaultTrnItemClickHandler()

        LazyColumn {
            val trnsList = TransactionsListUi(
                upcoming = null,
                overdue = null,
                history = emptyList()
            )

            transactionsList(
                trnsList = trnsList,
                emptyState = emptyState,
                upcomingHandler = upcomingHandler,
                overdueHandler = overdueHandler,
                dueActions = dummyDueActions(),
                trnClickHandler = trnItemClickHandler,
            )
        }
    }
}
// endregion