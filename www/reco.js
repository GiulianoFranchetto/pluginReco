var cordova = require('cordova');

cordova.define("cordova/plugin/recognizer", 
    function(require, exports, module) {
        var exec = require('cordova/exec');

        var Recognizer = function() {
          
          this.channels={
            recoError:cordova.addWindowEventHandler("recoError");
            recoNewMessage:cordova.addWindowEventHandler("recoNewMessage");
            recoSetupCompleted:cordova.addWindowEventHandler("recoSetupCompleted");
            test:cordova.addWindowEventHandler("test");
          }

        };
        
        Recognizer.prototype.test = function(){
          exec(recognizer.testOK, null, "Recognizer", "test", []);
        };

        Recognizer.prototype.testOK = function(){
          cordova.fireWindowEvent("test", null);
        }

        Recognizer.prototype.message = function (info) {
            if (info)  
              cordova.fireWindowEvent("recoNewMessage", info.message);
        };

        Recognizer.prototype.setupOK = function () {
          cordova.fireWindowEvent("recoSetupCompleted", null);
        };

        Recognizer.prototype.onError = function() {
            cordova.fireWindowEvent("recoError", null);
        };

        Recognizer.prototype.setupRecognizer = function(acoustic, dictionnary){
              exec(recognizer.setupOK, recognizer.onError, "Recognizer", "setupRecognizer", [acoustic, dictionnary]);

        };

        Recognizer.prototype.setupGrammar = function(name, path){
              exec(recognizer.setupOK, recognizer.onError, "Recognizer", "setupGrammar", [name, path]);

        };

        Recognizer.prototype.setupKeyphrase = function(name, phrase){
              exec(recognizer.setupOK, recognizer.onError, "Recognizer", "setupKeyphrase", [name, phrase]);

        };

        Recognizer.prototype.startListening = function(name){
              exec(recognizer.message, recognizer.onError, "Recognizer", "startListening", [name]);

        };

        Recognizer.prototype.stopListening = function(){
              exec(null, null, "Recognizer", "stopListening", []);

        };

        var recognizer = new Recognizer();

        module.exports = recognizer;
    }
);
