package com.teksta.projectparent

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.datetime.*

private enum class ChartTab(val label: String) {
    HOURLY("Hourly Duration"),
    DAILY("Daily Duration"),
    TOTAL("Total Bottles")
}

actual @Composable
fun ChartsScreen(viewModel: BottleFeedViewModel?, onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(ChartTab.HOURLY) }
    val allFeeds = remember(viewModel?.todayFeeds, viewModel?.yesterdayFeeds, viewModel?.olderFeeds) {
        (viewModel?.todayFeeds ?: emptyList()) + (viewModel?.yesterdayFeeds ?: emptyList()) + (viewModel?.olderFeeds ?: emptyList())
    }
    val sortedFeeds = allFeeds.sortedBy { it.startTime }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
        Text("Bottle Charts", color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 16.dp))
        TabRow(selectedTabIndex = selectedTab.ordinal, containerColor = Color.DarkGray) {
            ChartTab.values().forEachIndexed { idx, tab ->
                Tab(
                    selected = selectedTab.ordinal == idx,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.label, color = Color.White) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        when (selectedTab) {
            ChartTab.HOURLY -> HourlyDurationChart(sortedFeeds)
            ChartTab.DAILY -> DailyDurationChart(sortedFeeds)
            ChartTab.TOTAL -> TotalBottlesChart(sortedFeeds)
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
            Text("Back to Home", color = Color.White)
        }
    }
}

@Composable
private fun HourlyDurationChart(feeds: List<BottleFeedUiModel>) {
    // Group by hour of day, sum durations (seconds to minutes), but also keep date for label
    val hourMap = feeds.groupBy {
        val ldt = Instant.fromEpochSeconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault())
        ldt.date to ldt.hour
    }.mapValues { entry ->
        entry.value.sumOf { it.duration } / 60.0 // minutes
    }
    val sortedHours = hourMap.entries.sortedWith(compareBy({ it.key.first }, { it.key.second }))
    val entries = sortedHours.mapIndexed { idx, (dateHour, totalMinutes) ->
        BarEntry(idx.toFloat(), totalMinutes.toFloat())
    }
    val labels = sortedHours.map { (dateHour, totalMinutes) ->
        // Format: 10 Jul at 2pm
        val (date, hour) = dateHour
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val hourInt = hour.toInt()
        val ampm = if (hourInt < 12) "am" else "pm"
        val hour12 = if (hourInt == 0 || hourInt == 12) 12 else hourInt % 12
        "$day $month at $hour12$ampm"
    }
    val dataSet = BarDataSet(entries, "Feed Duration (min)").apply {
        color = AndroidColor.CYAN
        valueTextColor = AndroidColor.WHITE
    }
    val barData = BarData(dataSet)
    ChartContainer {
        AndroidView(
            modifier = Modifier.height(300.dp).width(400.dp),
            factory = { context ->
                BarChart(context).apply {
                    this.data = barData
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    axisRight.isEnabled = false
                    axisLeft.textColor = AndroidColor.WHITE
                    xAxis.textColor = AndroidColor.WHITE
                    legend.textColor = AndroidColor.WHITE
                    setBackgroundColor(AndroidColor.BLACK)
                    xAxis.granularity = 1f
                    xAxis.labelCount = labels.size
                    xAxis.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val idx = value.toInt()
                            return labels.getOrNull(idx) ?: ""
                        }
                    }
                    invalidate()
                }
            },
            update = { chart ->
                chart.data = barData
                chart.invalidate()
            }
        )
    }
}

@Composable
private fun DailyDurationChart(feeds: List<BottleFeedUiModel>) {
    // Group by day, sum durations (seconds to minutes)
    val dayMap = feeds.groupBy {
        Instant.fromEpochSeconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }.mapValues { entry ->
        entry.value.sumOf { it.duration } / 60.0 // minutes
    }
    val sortedDays = dayMap.entries.sortedBy { it.key }
    val entries = sortedDays.mapIndexed { idx, (date, totalMinutes) ->
        BarEntry(idx.toFloat(), totalMinutes.toFloat())
    }
    val dateLabels = sortedDays.map { (date, _) ->
        val day = date.dayOfMonth.toString().padStart(2, '0')
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        val year = date.year.toString()
        "$day $month $year"
    }
    val dataSet = BarDataSet(entries, "Total Feed Duration (min)").apply {
        color = AndroidColor.MAGENTA
        valueTextColor = AndroidColor.WHITE
    }
    val barData = BarData(dataSet)
    ChartContainer {
        AndroidView(
            modifier = Modifier.height(300.dp).width(400.dp),
            factory = { context ->
                BarChart(context).apply {
                    this.data = barData
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    axisRight.isEnabled = false
                    axisLeft.textColor = AndroidColor.WHITE
                    xAxis.textColor = AndroidColor.WHITE
                    legend.textColor = AndroidColor.WHITE
                    setBackgroundColor(AndroidColor.BLACK)
                    xAxis.granularity = 1f
                    xAxis.labelCount = dateLabels.size
                    xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dateLabels)
                    axisLeft.granularity = 1f
                    axisLeft.setLabelCount(6, false)
                    axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String = value.toInt().toString()
                    }
                    invalidate()
                }
            },
            update = { chart ->
                chart.data = barData
                chart.invalidate()
            }
        )
    }
}

@Composable
private fun TotalBottlesChart(feeds: List<BottleFeedUiModel>) {
    // Group by day, count bottles
    val dayMap = feeds.groupBy {
        Instant.fromEpochSeconds(it.startTime).toLocalDateTime(TimeZone.currentSystemDefault()).date
    }.mapValues { entry ->
        entry.value.size
    }
    val sortedDays = dayMap.entries.sortedBy { it.key }
    val entries = sortedDays.mapIndexed { idx, (date, count) ->
        Entry(idx.toFloat(), count.toFloat())
    }
    val dataSet = LineDataSet(entries, "Total Bottles").apply {
        color = AndroidColor.GREEN
        setDrawCircles(true)
        setDrawValues(false)
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(AndroidColor.GREEN)
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }
    val lineData = LineData(dataSet)
    ChartContainer {
        AndroidView(
            modifier = Modifier.height(300.dp).width(400.dp),
            factory = { context ->
                LineChart(context).apply {
                    this.data = lineData
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    axisRight.isEnabled = false
                    axisLeft.textColor = AndroidColor.WHITE
                    xAxis.textColor = AndroidColor.WHITE
                    legend.textColor = AndroidColor.WHITE
                    setBackgroundColor(AndroidColor.BLACK)
                    xAxis.granularity = 1f
                    xAxis.labelCount = sortedDays.size
                    xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                        sortedDays.map { it.key.toString() }
                    )
                    axisLeft.granularity = 1f
                    axisLeft.setLabelCount(6, false)
                    axisLeft.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String = value.toInt().toString()
                    }
                    invalidate()
                }
            },
            update = { chart ->
                chart.data = lineData
                chart.invalidate()
            }
        )
    }
}

@Composable
private fun ChartContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
} 