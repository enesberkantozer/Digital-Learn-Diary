package com.example.digitallearndiary.connectivity

import android.content.Intent
import android.nfc.NfcAdapter

class NfcManagerWrapper(
    private val onEvent: (ConnectivityEvent) -> Unit
) {

    fun handleIntent(intent: Intent) {
        if (
            intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            intent.action == NfcAdapter.ACTION_TECH_DISCOVERED
        ) {
            onEvent(
                ConnectivityEvent(
                    type = "NFC_READ",
                    source = "NFC",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
