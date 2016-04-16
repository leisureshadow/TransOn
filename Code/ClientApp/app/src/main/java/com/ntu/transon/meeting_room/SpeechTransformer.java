package com.ntu.transon.meeting_room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Ian on 2015/1/2.
 */
public class SpeechTransformer {

    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    private boolean isListening = false;
    private EditText toSendEditText;
    private MeetingParticipation participation;

    public SpeechTransformer(Context context, EditText toSendEditText, MeetingParticipation participation) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.toSendEditText = toSendEditText;
        this.participation = participation;
    }

    public void startRecognize() {
        if (!isListening) {
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
//            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            speechRecognizer.setRecognitionListener(new recognitionListener());
            isListening = true;
            speechRecognizer.startListening(intent);
        }
    }

    public void stopRecognize() {
        if (isListening) {
            isListening = false;
            speechRecognizer.stopListening();
        }
    }

    public void destroy() {
        stopRecognize();
        speechRecognizer.destroy();
    }

    private class recognitionListener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params) {
            Log.v("Speach Recognition", "onReadyForSpeech");
        }
        public void onBeginningOfSpeech() {
            Log.v("Speach Recognition", "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB) {
            // this is noisy
//            Log.v("Speach Recognition", "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer) {
            Log.v("Speach Recognition", "onBufferReceived");
        }
        public void onEndOfSpeech() {
            Log.v("Speach Recognition", "onEndofSpeech");
        }
        public void onError(int error) {
            Log.v("Speach Recognition",  "error " +  error);
            if (isListening)
                speechRecognizer.startListening(intent);
        }
        public void onResults(Bundle results) {
            Log.d("Speach Recognition", "onResults " + results);
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String toSendStr = data.get(0);
            Log.d("Speach Recognition", "result " + toSendStr);
//            for (int i = 0; i < data.size(); i++)
//                Log.d("Speach Recognition", "result " + data.get(i));

            // TODO: send toSendStr
            toSendEditText.setText("(sent) " + toSendStr);
            SpeechTransformer.this.participation.sendMessage(toSendStr);
            if (isListening)
                speechRecognizer.startListening(intent);
        }
        public void onPartialResults(Bundle partialResults) {
            Log.v("Speach Recognition", "onPartialResults " + partialResults);
            ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String partialStr = data.get(0);
            if (!partialStr.equals("")) {
                Log.d("Speach Recognition", "partial result " + partialStr);
                toSendEditText.setText(partialStr);
            }
        }
        public void onEvent(int eventType, Bundle params) {
            Log.v("Speach Recognition", "onEvent " + eventType);
        }
    }
}
