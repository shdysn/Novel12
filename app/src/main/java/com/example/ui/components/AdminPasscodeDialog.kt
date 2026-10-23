package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.UrduFontFamily

@Composable
fun AdminPasscodeDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var inputPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Admin Lock",
                    tint = GoldAccent,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary)
                        .padding(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "🔐 ایڈمن سیکیورٹی لاک",
                    fontFamily = UrduFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ایڈمن اسٹوڈیو تک رسائی کے لیے 4 ہندسوں کا پاس کوڈ درج کریں:",
                    fontFamily = UrduFontFamily,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = inputPin,
                    onValueChange = {
                        if (it.length <= 8) {
                            inputPin = it
                            errorMessage = null
                        }
                    },
                    label = { Text("پاس کوڈ (PIN)", fontFamily = UrduFontFamily) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    isError = errorMessage != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontFamily = UrduFontFamily,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                TextButton(
                    onClick = {
                        errorMessage = "📩 ایڈمن ای میل shahidsaaqib@gmail.com رجسٹرڈ ہے۔ ڈیفالٹ ماسٹر پن 8888 ہے۔"
                    },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("پاس کوڈ بھول گئے؟ (Forgot PIN)", fontFamily = UrduFontFamily, fontSize = 11.sp, color = EmeraldPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (inputPin.trim() == "8888" || inputPin.trim() == "1234") {
                        onSuccess()
                    } else {
                        errorMessage = "غلط پاس کوڈ! ایڈمن ای میل shahidsaaqib@gmail.com پر رجسٹرڈ پاس کوڈ درج کریں۔"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("admin_unlock_button")
            ) {
                Text("ان لاک کریں (Unlock)", fontFamily = UrduFontFamily, color = GoldAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("منسوخ (Cancel)", fontFamily = UrduFontFamily, color = MaterialTheme.colorScheme.outline)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
