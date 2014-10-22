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
import android.util.Log;
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
    	private ArrayList<String> keyName;
    	private ArrayList<String> keyPhrase;
    	private File modelsDir;

    	private JSONObject obj;

    	private boolean setup = false;


    	/** CONSTRUCTOR **/
    	public Recognizer_v2(){
    		grammarsName = new ArrayList<String>();
    		grammarsPath = new ArrayList<String>();
    		keyName = new ArrayList<String>();
    		keyPhrase = new ArrayList<String>();
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
				            try{
				            	obj = new JSONObject();
				            	obj.put("message", "Initialization completed" );
				            	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
						    	callbackContext.sendPluginResult(pluginResult);
						    }
						    catch(Exception ex){
						    	//dummy try catch
						    }
			      		}else{
				            try{
				            	obj = new JSONObject();
				            	obj.put("message", e );
				            	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, obj);
						    	callbackContext.sendPluginResult(pluginResult);
						    }
						    catch(Exception ex){
						    	//dummy try catch
						    }
			      		}
		            }
		        });

       			return true;
	    	}

	    	else if(action.equals("setupGrammar")){
    			this.callbackContext = callbackId;
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
			            	obj = new JSONObject();
			            	obj.put("message", "Fail to setup grammar" );
			            	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, obj);
						    callbackContext.sendPluginResult(pluginResult);
			            	return true;
			            }
	        		}
					obj = new JSONObject();
	            	obj.put("message", "Grammar setup completed" );
	            	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
				    callbackContext.sendPluginResult(pluginResult);	        		
				    return true;
	        	}
	        	else{
	        		obj = new JSONObject();
	            	obj.put("message", "Recognizer not initialized" );
	            	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, obj);
				    callbackContext.sendPluginResult(pluginResult);
	        		return true;
	        	}
	    	}

	    	else if(action.equals("setupKeyphrase")){
    			this.callbackContext = callbackId;
	    		if(setup){
		    		for(int i = 0; i < args.length(); i++){
		    			String kName = args.getJSONObject(i).getString("name");
		    			String kPhrase = args.getJSONObject(i).getString("phrase");

			            this.keyName.add(kName);
			            this.keyPhrase.add(kPhrase);
			            
			            recognizer.addKeyphraseSearch(kName, kPhrase);
	        		}

					obj = new JSONObject();
	            	obj.put("message", "Keyphrase setup completed" );
	            	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
				    callbackContext.sendPluginResult(pluginResult);	        		
				    return true;
	        	}
	        	else{
	        		obj = new JSONObject();
	            	obj.put("message", "Recognizer not initialized" );
	            	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, obj);
				    callbackContext.sendPluginResult(pluginResult);
	        		return true;
	        	}
	    	}

	    	else if(action.equals("startListening")){
    			this.callbackContext = callbackId;
				if(setup){
					recognizer.stop();
					String searchName = args.getString(0); 
		    		recognizer.startListening(searchName);
	    		}
	    		else{
	    			obj = new JSONObject();
	            	obj.put("message", "Recognizer not initialized" );
	            	PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, obj);
				    callbackContext.sendPluginResult(pluginResult);
	    		}
	    		return true;	
	    	}

	   		return false;
		}

        @Override
	    public void onPartialResult(Hypothesis hypothesis) {	       
        	String listenedText = hypothesis.getHypstr();
        	//if(listenedText.equals("bonjour"))
        	{
				try{
					obj = new JSONObject();
		        	obj.put("message", listenedText);
		        	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
		        	pluginResult.setKeepCallback(true);
				    callbackContext.sendPluginResult(pluginResult);
				}
				catch(Exception exe){
				}
        	}
	    }

	    @Override
	    public void onResult(Hypothesis hypothesis) {
        	try{
	        	String listenedText = hypothesis.getHypstr();
		    	obj = new JSONObject();
	        	obj.put("message", listenedText);
	        	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
			    callbackContext.sendPluginResult(pluginResult);
			    recognizer.stop();
			}
			catch(Exception ex){
				//dummy exception
			}
	    } 
	      @Override
	    public void onBeginningOfSpeech() {

	    }

	    @Override
	    public void onEndOfSpeech() {
	    	//if(recognizer.getSearchName() != "lea") recognizer.stop();
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