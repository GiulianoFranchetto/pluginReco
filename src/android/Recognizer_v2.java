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
import android.os.AsyncTask;
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
import static android.widget.Toast.makeText;

public class Recognizer_v2 
    extends CordovaPlugin
    implements RecognitionListener {

    	/** ATTRIBUTES **/
    	public SpeechRecognizer recognizer = null;
    	public CallbackContext callbackContext = null;
    	public PluginResult result;


    	/** CONSTRUCTOR **/
    	public Recognizer_v2(){
    	}

    	public boolean execute(String action, final JSONArray args,
            final CallbackContext callbackId) throws JSONException {

    		if(action.equals("setupRecognizer")){
    			this.callbackContext = callbackId;
    			this.setupRecognizer();
    			
			    result = new PluginResult(PluginResult.Status.NO_RESULT);
			    result.setKeepCallback(true);
			    this.callbackContext.sendPluginResult(result);
			    return true;

    		}
    		else
    			return false;
        }

        @Override
	    public void onPartialResult(Hypothesis hypothesis) {
	        
	    }

	    @Override
	    public void onResult(Hypothesis hypothesis) {
	      
	    } 
	      @Override
	    public void onBeginningOfSpeech() {

	    }

	    @Override
	    public void onEndOfSpeech() {

	    }

	    private void setupReco(File assetsDir) {
        	File modelsDir = new File(assetsDir, "models");
	        recognizer = defaultSetup()
	                .setAcousticModel(new File(modelsDir, "hmm/lium_french_f2"))
	                .setDictionary(new File(modelsDir, "dict/frenchWords62K.dic"))
	                .setRawLogDir(assetsDir).setKeywordThreshold(1e-20f)
	                .getRecognizer();
	        recognizer.addListener(this);
    	}

		public void setupRecognizer(){
            new AsyncTask<Void, Void, Exception>() {
	            @Override
	            protected Exception doInBackground(Void... params) {
	                try {
	                    Assets assets = new Assets(Recognizer_v2.this.cordova.getActivity());
	                    File assetDir = assets.syncAssets();
	                    setupReco(assetDir);
	                } catch (IOException e) {
	                    return e;
	                }
	                return null;
	            }

	            @Override
	            protected void onPostExecute(Exception e) {
                    result = new PluginResult(e==null?PluginResult.Status.ERROR : PluginResult.Status.OK);
				    result.setKeepCallback(false);
				    callbackContext.sendPluginResult(result);
	            }
	        }.execute();
   		} 
}