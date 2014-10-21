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
import java.util.ArrayList;

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
    	private SpeechRecognizer recognizer = null;
    	private CallbackContext callbackContext = null;
    	private PluginResult result;
    	private Activity activity;

    	private String accoustic = "";
    	private String dictionnary = "";
    	private ArrayList<String> grammarsName;
    	private ArrayList<String> grammarsPath;
    	private File modelsDir;

    	private JSONObject obj;

    	private boolean setup = false;


    	/** CONSTRUCTOR **/
    	public Recognizer_v2(){
    		grammarsName = new ArrayList<String>();
    		grammarsPath = new ArrayList<String>();
    	}

    	public boolean execute(String action, final JSONArray args,
            final CallbackContext callbackId) throws JSONException {
    		activity = this.cordova.getActivity();

    		if(action.equals("setupRecognizer")){
    			
    			this.callbackContext = callbackId;
    			this.accoustic = args.getString(0);
    			this.dictionnary = args.getString(1);

		        cordova.getThreadPool().execute(new Runnable() {
		            public void run() {
		                Exception e = setupRecognizer();
		                if(e==null){
		                	setup = true;
				            //callbackContext.success("Initialization completed");
				            try{
				            	obj = new JSONObject();
				            	obj.put("message", "Initialization completed" );
				            	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
						    	callbackContext.sendPluginResult(pluginResult);
						    }
						    catch(Exception e){//dummy try catch
						    }
			      		}else{
				            callbackContext.error("Fail to initialize");
			      		}
		            }
		        });

       			return true;
	    	}

	    	else if(action.equals("setupGrammar")){
	    		if(setup){
		    		for(int i = 0; i < args.length(); i++){
		    			String gName = args.getJSONObject(i).getString("name");
		    			String gPath = args.getJSONObject(i).getString("path");

			            this.grammarsName.add(gName);
			            this.grammarsPath.add(gPath);

			            try{
			            	File menuGrammar = new File(modelsDir, gPath);
	        				recognizer.addGrammarSearch(gName, menuGrammar);
			            }
			            catch(Exception e){
			            	callbackContext.error("Fail to setup grammar");
			            	/*JSONObject obj = new JSONObject();
			            	obj.put("message", "Fail to setup grammar" );
			            	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, obj);
			            	pluginResult.setKeepCallback(true);
						    callbackContext.sendPluginResult(pluginResult);*/
			            	return true;
			            }
	        		}
	        		callbackContext.success();
	        		return true;
	        	}
	        	else{
	        		callbackContext.error("Recognizer not initialized.");
	        		return true;
	        	}
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
        	modelsDir = new File(assetsDir, "models");
	        recognizer = defaultSetup()
	                .setAcousticModel(new File(modelsDir, accoustic))
	                .setDictionary(new File(modelsDir, dictionnary))
	                .setRawLogDir(assetsDir).setKeywordThreshold(1e-20f)
	                .getRecognizer();
	        recognizer.addListener(this);
    	}

		public Exception setupRecognizer(){
            try {
                Assets assets = new Assets(activity.getApplicationContext());
                File assetDir = assets.syncAssets();
                setupReco(assetDir);
            } catch (IOException e) {
                return e;
            }
            return null;
        } 
}