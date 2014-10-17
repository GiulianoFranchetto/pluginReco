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


class InfoSetup{
    public String ac;
    public String di;
    public SpeechRecognizer reco;
    public CallbackContext call;
    public Recognizer recoInfo;
    public InfoSetup(String ac, String di, SpeechRecognizer reco, CallbackContext c, Recognizer r){
        this.ac = ac;
        this.di = di;
        this.reco = reco;
        this.call = c;
        this.recoInfo = r;
    };

};

class SetupGrammarOrKeyphrase{
    public String name;
    public String keyOrPath;
    public CallbackContext call;
    public Recognizer recoGram;
    public SetupGrammarOrKeyphrase(String s1, String s2, CallbackContext c, Recognizer r){
        this.name = s1;
        this.keyOrPath = s2;
        this.call = c;
        this.recoGram = r;
    };

};


public class Recognizer 
    extends CordovaPlugin
    implements RecognitionListener {

    private SpeechRecognizer recognizer;
    private static boolean busy = false; 

    private CallbackContext recognizerCallbackContext = null;
    private PluginResult result;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        if (action.equals("setupRecognizer")) {
            this.busy = true;
            recognizerCallbackContext = callbackContext;

            String acoustic = args.getString(0);
            String dictionary = args.getString(1);

           

            InfoSetup i = new InfoSetup(acoustic, dictionary, recognizer,recognizerCallbackContext,this);

            new AsyncTask<InfoSetup, Void, Exception>() {
           
                @Override
                protected Exception doInBackground(InfoSetup... params) {
                    
                    try {
                        InfoSetup info = params[0];

                        Assets assets = new Assets(info.recoInfo.cordova.getActivity().getApplicationContext());
                        File assetDir = assets.syncAssets();

                        File modelsDir = new File(assetDir, "models");
                        recognizer = defaultSetup()
                            .setAcousticModel(new File(modelsDir, info.ac))
                            .setDictionary(new File(modelsDir, info.di))
                            .setRawLogDir(assetDir).setKeywordThreshold(1e-20f)
                            .getRecognizer();

                      info.reco.addListener(info.recoInfo);
                    } 

                    catch (IOException e) {
                        return e;
                    }

                    return null;
                }

                 @Override
                protected void onPostExecute(Exception res) {
                    busy = false;
                }
            }.execute(i);

            return true;
        }

        else if(action.equals("setGrammar")) {
            this.busy = true;

            String grammarName = args.getString(0);
            String pathToGrammar = args.getString(1);
            SetupGrammarOrKeyphrase setup1 = new SetupGrammarOrKeyphrase(grammarName,pathToGrammar,recognizerCallbackContext,this);



            new AsyncTask<SetupGrammarOrKeyphrase, Void, Exception>() {
           
                @Override
                protected Exception doInBackground(SetupGrammarOrKeyphrase... params) {
                    
                    SetupGrammarOrKeyphrase setup = params[0];
                    
                    Assets assets = new Assets(Recognizer.this);
                    File assetDir = assets.syncAssets();
                    File modelsDir = new File(assetDir, "models");

                    File menuGrammar = new File(modelsDir, setup.keyOrPath);
                    recognizer.addGrammarSearch(setup.name, menuGrammar);

                    return null;
                }

                 @Override
                protected void onPostExecute(Exception res) {
                    busy = false;
                }
            }.execute(setup1);

            return true;
        }

        else if(action.equals("setKeyphrase")) {
            this.busy = true;

            String keyname = args.getString(0);
            String keyphrase = args.getString(1);

            SetupGrammarOrKeyphrase setup2 = new SetupGrammarOrKeyphrase(keyname,keyphrase,recognizerCallbackContext);


            new AsyncTask<SetupGrammarOrKeyphrase, Void, Exception>() {
           
                @Override
                protected Exception doInBackground(SetupGrammarOrKeyphrase... params) {
            
                    SetupGrammarOrKeyphrase s2  = params[2];
                    recognizer.addKeyphraseSearch(s2.name, s2.keyOrPath);

                    return null;
                }

                 @Override
                protected void onPostExecute(Exception result) {
                    busy = false;
                }

            }.execute(setup2);

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
            result = new PluginResult(PluginResult.Status.OK, obj);
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
