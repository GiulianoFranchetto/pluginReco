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

		        cordova.getThreadPool().execute(new Runnable() {
		            public void run() {
		                Execption e = setupRecognizer();
		                if(e==null){
	                    	result = new PluginResult(PluginResult.Status.OK);
				            result.setKeepCallback(false);
				            callbackContext.success(result);
			      		}else{
			      			result = new PluginResult(PluginResult.Status.ERROR);
				            result.setKeepCallback(false);
				            callbackContext.error(result);
			      		}
		            }
		        });

       			return true;
	    	}
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
	        //recognizer.addListener(this);
    	}

		public Exception setupRecognizer(){
            try {
                Assets assets = new Assets(Recognizer_v2.this.cordova.getActivity());
                File assetDir = assets.syncAssets();
                setupReco(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        } 
}