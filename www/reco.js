var cordova = require('cordova');

cordova.define("cordova/plugin/recognition", 
    function(require, exports, module) {
        var exec = require('cordova/exec');

        var Recognizer = function() {
          
            this.channels = {
              recoError:cordova.addWindowEventHandler("recoError"),
              recoNewMessage:cordova.addWindowEventHandler("recoNewMessage"),
              recoSetupCompleted:cordova.addWindowEventHandler("recoSetupCompleted")
            };
        };
           
        Recognizer.prototype.message = function (info) {
            if (info)  
                cordova.fireWindowEvent("recoNewMessage", info.message);
        };

        Recognizer.prototype.setupOK = function () {
                cordova.fireWindowEvent("recoSetupCompleted", info.message);
        };

        Recognizer.prototype.error = function(info) {
            cordova.fireWindowEvent("recoError", info.message);
        };

        Recognizer.prototype.setupRecognizer = function(acoustic, dictionnary){
              exec(recognizer.setupOK, recognizer.error, "Recognizer", "setupRecognizer", [acoustic, dictionnary]);

        };

        Recognizer.prototype.setupGrammar = function(name, path){
              exec(recognizer.setupOK, recognizer.error, "Recognizer", "setupGrammar", [name, path]);

        };

        Recognizer.prototype.setupKeyphrase = function(name, phrase){
              exec(recognizer.setupOK, recognizer.error, "Recognizer", "setupKeyphrase", [name, phrase]);

        };

        Recognizer.prototype.startListening = function(name){
              exec(recognizer.message, recognizer.error, "Recognizer", "startListening", [name]);

        };

        Recognizer.prototype.stopListening = function(){
              exec(recognizer.setupOK, recognizer.error, "Recognizer", "stopListening", []);

        };

        var recognizer = new Recognizer();

        module.exports = recognizer;
    }
);
