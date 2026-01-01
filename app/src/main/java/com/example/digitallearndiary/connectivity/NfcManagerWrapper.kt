package com.example.digitallearndiary.connectivity

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import com.example.digitallearndiary.receiver.ConnectivityBroadcastReceiver

class NfcManagerWrapper(
    private val context: Context,
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


            val broadcastIntent =
                Intent(context, ConnectivityBroadcastReceiver::class.java)
            broadcastIntent.putExtra("type", "NFC_READ")
            broadcastIntent.putExtra("source", "NFC")
            context.sendBroadcast(broadcastIntent)
        }
    }
}
