package com.yuriikonovalov.helia.presentation.auth.signin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.yuriikonovalov.helia.designsystem.components.EmailInputField
import com.yuriikonovalov.helia.designsystem.components.Navbar
import com.yuriikonovalov.helia.designsystem.components.PrimaryButton
import com.yuriikonovalov.helia.designsystem.theme.HeliaTheme

@Composable
fun ForgotPasswordScreen(
    onNavigateClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isSent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Navbar(
            modifier = Modifier.fillMaxWidth(),
            title = "Forgot Password",
            onNavigateClick = onNavigateClick,
            actions = {}
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Enter your email address and we'll send you a link to reset your password.",
            style = HeliaTheme.typography.bodyMediumRegular,
            color = HeliaTheme.colors.greyscale600
        )

        Spacer(modifier = Modifier.height(32.dp))

        EmailInputField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            isError = false,
            supportingText = null
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isSent) {
            Text(
                text = "✅ Password reset email sent! Check your inbox.",
                style = HeliaTheme.typography.bodyMediumRegular,
                color = HeliaTheme.colors.primary500,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Send Reset Link",
            enabled = email.isNotBlank(),
            onClick = {
                FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(email.trim())
                    .addOnSuccessListener {
                        isSent = true
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            "Error: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        )
    }
}