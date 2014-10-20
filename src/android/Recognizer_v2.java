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
import static android.widget.Toast.makeText;

public class Recognizer_v2 
    extends CordovaPlugin
    implements RecognitionListener {

    	/** PRIVATE ATTRIBUTES **/
    	private SpeechRecognizer recognizer = null;
    	private RecognizerBuilder recognizerBuilder = null;
    	private CallbackContext callbackContext = null;
    	private PluginResult result;


    	/** CONSTRUCTOR **/
    	public Recognizer_v2(){
    		recognizerBuilder = new RecognizerBuilder();
    	}

    	public boolean execute(String action, final JSONArray args,
            final CallbackContext callbackId) throws JSONException {

    		if(action.equals("setupRecognizer")){
    			this.callbackContext = callbackId;
    			recognizerBuilder = new RecognizerBuilder();
    			recognizer = recognizerBuilder.setupRecognizer(this.cordova.getActivity(), args.getString(0), args.getString(1));
			    result = new PluginResult(recognizer==null?PluginResult.Status.ERROR:PluginResult.Status.OK);
			    result.setKeepCallback(false);
			    this.callbackContext.sendPluginResult(result);
			    return true;

    		}
    		else
    			return false;
        } 


        class RecognizerBuilder{
        	public  RecognizerBuilder(){}

    		public SpeechRecognizer setupRecognizer(Activity activity, String acoustic, String dictionnary){
    			try{
    				Assets assets = new Assets(activity.getApplicationContext());
	                File assetDir = assets.syncAssets();

	                File modelsDir = new File(assetDir, "models");
	                recognizer = defaultSetup()
	                    .setAcousticModel(new File(modelsDir, acoustic))
	                    .setDictionary(new File(modelsDir, dictionnary))
	                    .setRawLogDir(assetDir).setKeywordThreshold(1e-20f)
	                    .getRecognizer();

	                return recognizer;
    			}
    			catch(Exception e){
    				return null;
    			}

        	}

        }


}