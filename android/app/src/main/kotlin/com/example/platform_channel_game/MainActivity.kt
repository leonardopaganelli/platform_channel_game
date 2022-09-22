package com.example.platform_channel_game

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.util.Log
import com.google.gson.JsonElement
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import com.pubnub.api.models.consumer.pubsub.PNSignalResult
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.util.*
import kotlin.math.log

class MainActivity: FlutterActivity() {
    private val CHANNEL_NATIVE_DART = "game/exchange"
    private var pubnub: PubNub? = null
    private var channel_pubnub: String? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())

        val pnConfiguration = PNConfiguration("myUniqueUUID")
        pnConfiguration.subscribeKey = "sub-c-6814f069-9198-4104-9976-60ba3d90afe2"
        pnConfiguration.publishKey = "pub-c-4cf17b2f-4fd4-4bd9-af97-2ec8aadd8d41"
        pubnub = PubNub(pnConfiguration)

        pubnub?.let {
            it.addListener(object: SubscribeCallback(){
                override fun message(pubnub: PubNub, message: PNMessageResult) {
                    var receivedObject: JsonElement? = null
                    var actionReceived = "sendAction"

                    if (message.message.asJsonObject["tap"] != null) {
                        receivedObject = message.message.asJsonObject["tap"]
                    }
                    Log.e("Pubnub message", "${receivedObject}");
                    handler?.let {
                        it.post {
                            MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL_NATIVE_DART)
                                    .invokeMethod(actionReceived, "${receivedObject?.asString}")
                        }
                    }
                }

                override fun status(pubnub: PubNub, pnStatus: PNStatus) {}
                override fun presence(pubnub: PubNub, pnPresenceEventResult: PNPresenceEventResult) {}
                override fun signal(pubnub: PubNub, pnSignalResult: PNSignalResult) {}
                override fun uuid(pubnub: PubNub, pnUUIDMetadataResult: PNUUIDMetadataResult) {}
                override fun channel(pubnub: PubNub, pnChannelMetadataResult: PNChannelMetadataResult) {}
                override fun membership(pubnub: PubNub, pnMembershipResult: PNMembershipResult) {}
                override fun messageAction(pubnub: PubNub, pnMessageActionResult: PNMessageActionResult) {}
                override fun file(pubnub: PubNub, pnFileEventResult: PNFileEventResult) {}
            })
        }

    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL_NATIVE_DART).setMethodCallHandler {
            call, result ->
            Log.e("Chegou no native abc","${call.method}");

            if(call.method == "subscribe"){
                subscribeChannel(call.argument("channel"))
                Log.e("Chegou no native","subscribe");
                result.success(true);
            } else if(call.method == "sendAction" || call.method == "chat") {
                pubnub!!.publish()
                    .message(call.arguments)
                    .channel(channel_pubnub)
                    .async { result, status -> Log.e("Pubnub", "teve erro? ${status.isError}") }

                Log.e("Chegou no native","enviou a mensagem");

                result.success(true);
            } else {
                result.notImplemented();
            }
        }
    }

    private fun subscribeChannel(channel: String?){
        channel_pubnub = channel
        channel_pubnub.let {
            pubnub?.subscribe()?.channels(Arrays.asList(channel))?.execute()
        }
    }
}
