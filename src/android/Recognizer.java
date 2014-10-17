package com.giulianofranchetto.plugins.speechreco;

/** CORDOVA IMPORTS **/
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** CMU SPHINX IMPORTS **/
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

/** JAVA IMPORT **/
import java.io.File;
import java.io.IOException;

/** ANDROID IMPORTS **/
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;



public class Recognizer 
    extends CordovaPlugin
    implements RecognitionListener {

    private SpeechRecognizer recognizer;
    private static boolean busy = false; 

    private CallbackContext recognizerCallbackContext = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        if (action.equals("setupRecognizer")) {
            this.busy = true;
            recognizerCallbackContext = callbackContext;

            new AsyncTask<Void, Void, Exception>() {
           
                @Override
                protected Exception doInBackground(Void... params) {
                    
                    try {
                        Assets assets = new Assets(Recognizer.this);
                        File assetDir = assets.syncAssets();

                        String acoustic = args.getString(0);
                        String dictionary = args.getString(1);

                        File modelsDir = new File(assetDir, "models");
                        recognizer = defaultSetup()
                            .setAcousticModel(new File(modelsDir, acoustic))
                            .setDictionary(new File(modelsDir, dictionary))
                            .setRawLogDir(assetDir).setKeywordThreshold(1e-20f)
                            .getRecognizer();

                      recognizer.addListener(this);
                    } 

                    catch (IOException e) {
                        return e;
                    }

                    return null;
                }

                 @Override
                protected void onPostExecute(Exception result) {
                    busy = false;
                    if(result != null) {
                        JSONObject obj = new JSONObject();
                        obj.put("message", "error during recognizer configuration");
                        PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
                        result.setKeepCallback(keepCallback);
                        this.recognizerCallbackContext.sendPluginResult(result);
                    }
                }
            }.execute();

            return true;
        }

        else if(action.equals("setGrammar")) {
            this.busy = true;

            new AsyncTask<Void, Void, Exception>() {
           
                @Override
                protected Exception doInBackground(Void... params) {
            
                    String grammarName = args.getString(0);
                    String pathToGrammar = args.getString(1);

                    File menuGrammar = new File(modelsDir, pathToGrammar);
                    recognizer.addGrammarSearch(grammarName, menuGrammar);

                    return null;
                }

                 @Override
                protected void onPostExecute(Exception result) {
                    busy = false;
                    if(result != null)
                    {
                        JSONObject obj = new JSONObject();
                        obj.put("message", "error during grammar configuration");
                        PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
                        result.setKeepCallback(keepCallback);
                        this.recognizerCallbackContext.sendPluginResult(result);
                    }
                }
            }.execute();

            return true;
        }

        else if(action.equals("setKeyphrase")) {
            this.busy = true;

            new AsyncTask<Void, Void, Exception>() {
           
                @Override
                protected Exception doInBackground(Void... params) {
            
                    String keyname = args.getString(0);
                    String keyphrase = args.getString(1);

                    recognizer.addKeyphraseSearch(keyname, keyphrase);

                    return null;
                }

                 @Override
                protected void onPostExecute(Exception result) {
                    busy = false;
                    if(result != null)
                    {
                        JSONObject obj = new JSONObject();
                        obj.put("message", "error during keyphrase configuration");
                        PluginResult result = new PluginResult(PluginResult.Status.ERROR, obj);
                        result.setKeepCallback(keepCallback);
                        this.recognizerCallbackContext.sendPluginResult(result);
                    }
                }

            }.execute();

            return true;
        }

        else if(action.equals("startListening")) {

            if(!busy) {
                String searchName = args.getString(0);
                recognizer.startListening(searchName);
                return true;
            }
            else
                return false;
        }

        else if(action.equals("stopListening")) {

            if(!busy) {
                recognizer.stop();
                return true;
            }
            else
                return false;
        }
        // A false return = MethodNotFound error
        return false;
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            JSONObject obj = new JSONObject();
            obj.put("message", text);
            PluginResult result = new PluginResult(PluginResult.Status.OK, obj);
            result.setKeepCallback(keepCallback);
            this.recognizerCallbackContext.sendPluginResult(result);
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
    }
    
}
