package com.application.realtimelanguagetranslator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    var _data:String = ""
    var appmode:String ="urdu"
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var micButton: ImageButton
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageAdapter: MessageAdapter
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    companion object {
        const val REQUEST_CODE_STT = 1
    }
    private val textToSpeechEngine: TextToSpeech by lazy {
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.ENGLISH
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageList = mutableListOf()

        recyclerView = findViewById(R.id.recycler_view)

        messageEditText = findViewById(R.id.message_edit_text)
        sendButton = findViewById(R.id.send_btn)
        micButton = findViewById(R.id.mic)

        // Setup recycler view
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter
        val llm = LinearLayoutManager(this)
        llm.stackFromEnd = true
        recyclerView.layoutManager = llm

        sendButton.setOnClickListener {
            val question = messageEditText.text.toString().trim()
            addToChat(question, Message.SENT_BY_ME)
            messageEditText.setText("")

            GlobalScope.launch(Dispatchers.IO) {
                callAPI(question)
            }
        }
        micButton.setOnClickListener {
            getInput()

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addToChat(message: String, sentBy: String) {
        runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    private suspend fun callAPI(question: String) {
        // OkHttp
        messageList.add(Message("Translating... ", Message.SENT_BY_BOT))
        var translation =""
        if (appmode == "urdu") {
            translation = translate(question, "ur", "en")
            addResponse(translation)
        } else {
            translation = translate(question, "en", "ur")
            addResponse(translation)
        }
    }

    fun switch(v: View){

        if(appmode=="urdu"){
            appmode="english"
            Toast.makeText(this,"English to Urdu",Toast.LENGTH_LONG).show()
        }else{
            appmode="urdu"
            Toast.makeText(this,"Urdu to English",Toast.LENGTH_LONG).show()
        }

    }


    fun getInputEnglish(){
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)

        try {
            startActivityForResult(sttIntent, MainActivity.REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }
    fun getInputUrdu(){
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        sttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK") // Set language to Urdu (Pakistan)
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now") // Prompt message for the user


        try {
            startActivityForResult(sttIntent, MainActivity.REQUEST_CODE_STT)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Your device does not support STT.", Toast.LENGTH_LONG).show()
        }
    }

    fun getInput() {

        if(appmode=="urdu"){
            getInputUrdu()
        }else{
            getInputEnglish()
        }

    }

    private suspend fun translate(inputText: String, sourceLang: String, targetLang: String): String {
        val url = "https://api.mymemory.translated.net/get?q=$inputText&langpair=$sourceLang|$targetLang"

        return try {
            val response = withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).execute()
            }

            if (!response.isSuccessful) {
                throw IOException("Unexpected response code: ${response.code}")
            }

            val responseBody = response.body?.string() ?: ""
            val jsonObject = JSONObject(responseBody)

            jsonObject.getJSONObject("responseData").getString("translatedText")
        } catch (e: Exception) {
            e.printStackTrace()
            "Translation failed"
        }
    }



    fun Speek(text:String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null, "tts1")
        } else {
            textToSpeechEngine.speak(text, TextToSpeech.QUEUE_ADD, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MainActivity.REQUEST_CODE_STT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    result?.let {
                        val recognizedText = it[0]
                        _data=recognizedText
                        val question = _data.trim()
                        messageEditText.setText(question)
                    }
                }
            }
        }
    }




    override fun onPause() {
        textToSpeechEngine.stop()
        super.onPause()
    }

    override fun onDestroy() {
        textToSpeechEngine.shutdown()
        super.onDestroy()
    }


}