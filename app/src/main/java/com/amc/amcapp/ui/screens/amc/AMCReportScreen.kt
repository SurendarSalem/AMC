package com.amc.amcapp.ui.screens.amc

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amc.amcapp.R
import com.amc.amcapp.model.AMC
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale

@Composable
fun AMCReportScreen(amc: AMC) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(64.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "AMC Maintenance Report",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(16.dp))
        }

        // Basic Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Basic Information")
                    Text("AMC Name: ${amc.name}", fontWeight = FontWeight.Medium)
                    Text("Description: ${amc.description}")
                    Text("Gym Name: ${amc.gymName}")
                    Text("Assigned To: ${amc.assignedName}")
                    Text("Status: ${amc.status}")
                    Text("Created: ${formatDate(amc.createdDate)}")
                    Text("Updated: ${formatDate(amc.updatedAt)}")
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Equipments
        item {
            Card(
                modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Equipments")
                    if (amc.equipments.isEmpty()) {
                        Text("No equipment added.")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        items(amc.equipments) { eq ->
            Text("• ${eq.name}", modifier = Modifier.padding(start = 16.dp, bottom = 4.dp))
        }

        // Service Records
        item {
            Card(
                modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Service Records")
                    if (amc.recordItems.isEmpty()) {
                        Text("No records available.")
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        items(amc.recordItems) { rec ->
            Text(
                "• ${rec.equipmentName} - ${rec.addedSpares.joinToString { it.spareName }}",
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
        }

        // Footer
        item {
            FooterSection(amc)
            Spacer(Modifier.height(16.dp))
        }

        // Export PDF Button
        item {
            Button(
                onClick = {
                    scope.launch {
                        createAndShareAmcPdfFull(ctx, amc)
                    }
                },
            ) {
                Text("Export PDF")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(6.dp))
    Divider()
    Spacer(Modifier.height(8.dp))
}

@Composable
fun FooterSection(amc: AMC) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Divider()
        Spacer(Modifier.height(12.dp))
        Text("Verified by:", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(24.dp))
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("__________________", fontSize = 14.sp)
            Text("__________________", fontSize = 14.sp)
        }
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Technician Signature", fontSize = 12.sp)
            Text("Customer Signature", fontSize = 12.sp)
        }

        Spacer(Modifier.height(16.dp))
        Divider()
        Text(
            "Verified on: ${formatDate(System.currentTimeMillis())}",
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "Powered by AMCApp",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Helper
private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "-"
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


// ---------------- PDF Generation ----------------


suspend fun createAndShareAmcPdfFull(context: Context, amc: AMC) = withContext(Dispatchers.IO) {
    try {
        val pdf = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val marginLeft = 40f
        val marginRight = pageWidth - 40f
        var y = 50f

        var pageNumber = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas

        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        // --- Helpers ---
        fun newPage() {
            pdf.finishPage(page)
            pageNumber++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = 50f
        }

        fun checkPageSpace(height: Float) {
            if (y + height > pageHeight - 50) newPage()
        }

        fun drawHeader(title: String) {
            checkPageSpace(25f)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            paint.textSize = 18f
            canvas.drawText(title, marginLeft, y, paint)
            y += 6f
            canvas.drawLine(marginLeft, y, marginRight, y, paint)
            y += 20f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 14f
        }

        fun drawLine(label: String, value: String) {
            val fm = paint.fontMetrics
            val rowHeight = fm.bottom - fm.top + 8f
            checkPageSpace(rowHeight)
            canvas.drawText("$label: $value", marginLeft, y, paint)
            y += rowHeight
        }

        // --- Logo ---
        try {
            val drawable = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.gym)
            drawable?.let {
                val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
                val bitmapCanvas = Canvas(bitmap)
                it.setBounds(0, 0, bitmapCanvas.width, bitmapCanvas.height)
                it.draw(bitmapCanvas)

                val logoSize = 128f
                val rect = RectF((pageWidth - logoSize) / 2f, y, (pageWidth + logoSize) / 2f, y + logoSize)
                canvas.drawBitmap(bitmap, null, rect, paint)
                y += logoSize + 20f
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // --- Title ---
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 20f
        canvas.drawText("AMC Report", pageWidth / 2f, y, paint)
        y += 40f
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 14f

        // --- Basic Info ---
        drawLine("Gym Name", amc.gymName)
        drawLine("Technician", amc.assignedName)
        drawLine("Status", amc.status.name)
        drawLine("Created", formatDate(amc.createdDate))
        drawLine("Updated", formatDate(amc.updatedAt))
        drawLine("Total AMC Amount", "₹ ${amc.amcPackageDetails?.price}")

        // --- Equipments ---
        drawHeader("Equipments")
        if (amc.equipments.isEmpty()) {
            drawLine("", "No equipment added.")
        } else {
            val fm = paint.fontMetrics
            val rowHeight = fm.bottom - fm.top + 8f
            amc.equipments.forEach {
                checkPageSpace(rowHeight)
                canvas.drawText("• ${it.name}", marginLeft + 20f, y, paint)
                y += rowHeight
            }
        }

        // --- Service Records ---
        drawHeader("Service Records")
        if (amc.recordItems.isEmpty()) {
            drawLine("", "No records available.")
        } else {
            val col1 = marginLeft
            val col2 = marginLeft + 180f
            val col3 = marginLeft + 400f
            val fm = paint.fontMetrics
            val rowBaseHeight = fm.bottom - fm.top + 8f

            // Table Header
            paint.color = Color.LTGRAY
            canvas.drawRect(col1 - 5, y - 15, col3 + 80f, y + rowBaseHeight - 5, paint)
            paint.color = Color.BLACK
            paint.typeface = Typeface.DEFAULT_BOLD
            canvas.drawText("Equipment", col1, y, paint)
            canvas.drawText("Added Spares", col2, y, paint)
            canvas.drawText("Amount", col3, y, paint)
            paint.typeface = Typeface.DEFAULT
            y += rowBaseHeight

            amc.recordItems.forEachIndexed { index, rec ->
                val sparesText = if (rec.addedSpares.isEmpty()) "-" else rec.addedSpares.mapIndexed { i, s -> "${i + 1}. ${s.spareName}" }.joinToString(", ")
                val wrappedSpares = wrapText(sparesText, paint, col3 - col2 - 10f)
                val totalRowHeight = maxOf(rowBaseHeight, wrappedSpares.size * rowBaseHeight)
                checkPageSpace(totalRowHeight)

                // Alternating row color
                paint.color = if (index % 2 == 0) Color.parseColor("#F2F2F2") else Color.WHITE
                canvas.drawRect(col1 - 5, y - 15, col3 + 80f, y - 15 + totalRowHeight, paint)

                // Row border
                paint.style = Paint.Style.STROKE
                paint.color = Color.BLACK
                canvas.drawRect(col1 - 5, y - 15, col3 + 80f, y - 15 + totalRowHeight, paint)
                paint.style = Paint.Style.FILL

                // Row text
                paint.color = Color.BLACK
                canvas.drawText(rec.equipmentName, col1, y, paint)
                wrappedSpares.forEachIndexed { i, line ->
                    canvas.drawText(line, col2, y + i * rowBaseHeight, paint)
                }
                // Draw amount

                y += totalRowHeight
            }

            // --- AMC Total Amount Summary ---
            y += 20f
            checkPageSpace(22f)
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 16f
            canvas.drawText("Total AMC Amount: ₹ ${amc.amcPackageDetails?.price}", marginLeft, y, paint)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 14f
        }

        // --- Footer ---
        y += 40f
        checkPageSpace(50f)
        paint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText("Verified by: ____________________", marginLeft, y, paint)
        y += 20f
        canvas.drawText("Customer Signature: ____________________", marginLeft, y, paint)
        y += 40f

        paint.typeface = Typeface.DEFAULT
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 10f
        canvas.drawText("Generated on: ${formatDate(System.currentTimeMillis())}", pageWidth - marginLeft, y, paint)

        // Optional: Page number
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Page $pageNumber", pageWidth / 2f, pageHeight - 20f, paint)

        pdf.finishPage(page)

        // --- Save & Share ---
        val dir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOCUMENTS)
        if (!dir!!.exists()) dir.mkdirs()
        val safeName = amc.name.replace("[^a-zA-Z0-9_]".toRegex(), "_")
        val file = File(dir, "AMC_${safeName}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { pdf.writeTo(it) }
        pdf.close()

        val uri: Uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share AMC PDF"))

    } catch (e: Exception) {
        e.printStackTrace()
        withContext(Dispatchers.Main) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}

// --- Text Wrapping ---
private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var currentLine = ""
    for (word in words) {
        val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
        if (paint.measureText(testLine) <= maxWidth) {
            currentLine = testLine
        } else {
            if (currentLine.isNotEmpty()) lines.add(currentLine)
            currentLine = word
        }
    }
    if (currentLine.isNotEmpty()) lines.add(currentLine)
    return lines
}

