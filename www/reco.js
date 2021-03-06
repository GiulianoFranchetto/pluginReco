var cordova = require('cordova');

cordova.define("cordova/plugin/recognizer", 
    function(require, exports, module) {
        var exec = require('cordova/exec');

        var Recognizer = function() {
          
          this.channels={
            recoNewMessage:cordova.addWindowEventHandler("newmessage"),
            recoSetupCompleted:cordova.addWindowEventHandler("setupcompleted"),
            recoKeyphraseCompleted:cordova.addWindowEventHandler("keyphrasecompleted"),
            recoError:cordova.addWindowEventHandler("error"),
            recoGrammarCompleted:cordova.addWindowEventHandler("grammarcompleted")
          };

        };
        
        Recognizer.prototype.setupKeyphraseOK = function (message) {
          cordova.fireWindowEvent("keyphrasecompleted", message);
        };   

        Recognizer.prototype.setupGrammarOK = function (message) {
          cordova.fireWindowEvent("grammarcompleted", message);
        };        

        Recognizer.prototype.message = function (info) {
          console.log(info.message);
          if(info.message==="bonjour")
          {

          }
          else cordova.fireWindowEvent("newmessage", info);
        };

        Recognizer.prototype._setupok = function (message) {
          console.log(message.message);
          cordova.fireWindowEvent("setupcompleted", message);
        };

        Recognizer.prototype._error = function(err) {
          console.log(err);
          cordova.fireWindowEvent("error", err);
        };

        Recognizer.prototype.setupRecognizer = function(acoustic, dictionnary){
          exec(recognizer._setupok, recognizer._error, "Recognizer", "setupRecognizer", [acoustic, dictionnary]);
        };

        Recognizer.prototype.setupGrammar = function(gramars){
          exec(recognizer.setupGrammarOK, recognizer._error, "Recognizer", "setupGrammar", gramars);
        };

        Recognizer.prototype.setupKeyphrase = function(keys){
          exec(recognizer.setupKeyphraseOK, recognizer._error, "Recognizer", "setupKeyphrase", keys);
        };

        Recognizer.prototype.startListening = function(name){
          exec(recognizer.message, recognizer._error, "Recognizer", "startListening", [name]);
        };

        Recognizer.prototype.stopListening = function(){
          exec(null, null, "Recognizer", "stopListening", []);
        };

        var recognizer = new Recognizer();

        module.exports = recognizer;
    }
);
