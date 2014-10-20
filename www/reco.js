var cordova = require('cordova');

cordova.define("cordova/plugin/recognizer", 
    function(require, exports, module) {
        var exec = require('cordova/exec');

        var Recognizer = function() {
          
          this.channels={
            recoNewMessage:cordova.addWindowEventHandler("recoNewMessage"),
            recoSetupCompleted:cordova.addWindowEventHandler("recoSetupCompleted"),
            test:cordova.addWindowEventHandler("test2"),
            recoError:cordova.addWindowEventHandler("recoError")
          };

        };
        
        Recognizer.prototype.test = function(){
          exec(recognizer.testOK, null, "Recognizer", "test", []);
        };

        Recognizer.prototype.testOK = function(){
          cordova.fireWindowEvent("test2", null);
        };

        Recognizer.prototype.message = function (info) {
          cordova.fireWindowEvent("recoNewMessage", info);
        };

        Recognizer.prototype.setupOK = function () {
          cordova.fireWindowEvent("recoSetupCompleted", null);
        };

        Recognizer.prototype._error = function() {
            cordova.fireWindowEvent("recoError", null);
        };

        Recognizer.prototype.setupRecognizer = function(acoustic, dictionnary){
          exec(recognizer.setupOK, recognizer._error, "Recognizer", "setupRecognizer", [acoustic, dictionnary]);
        };

        Recognizer.prototype.setupGrammar = function(name, path){
          exec(recognizer.setupOK, recognizer._error, "Recognizer", "setupGrammar", [name, path]);
        };

        Recognizer.prototype.setupKeyphrase = function(name, phrase){
          exec(recognizer.setupOK, recognizer._error, "Recognizer", "setupKeyphrase", [name, phrase]);

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
