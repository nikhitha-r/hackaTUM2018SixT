const functions = require('firebase-functions');

var request1 = require('request');
const express = require('express');
//const cors = require('cors');
const jq =  require('jquery');
const app = express();
const veh = express();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
 exports.helloWorld = functions.https.onRequest((request, response) => {
  response.send("Hello from Firebase!");
 });


app.post('/package', (req, res) => {

  res.send("Got" + req.body);



    
  
});

veh.get('/unlock' ,(request, response) => {
  const paramsString = request.url.split('?')[1];
  const eachParamArray = paramsString.split('&');
 let params = {};
 eachParamArray.forEach((param) => {
    const key = param.split('=')[0];
    const value = param.split('=')[1];
    Object.assign(params, {[key]: value});
  });
let vin = params["vin"];

var sixturl = "https://hackatum.goorange.sixt.com/vehicle/" + vin + "/unlock";


 request1(sixturl, function (error, res, body) {
response.send(res);
    });



});



veh.get('/lock' ,(request, response) => {
  const paramsString = request.url.split('?')[1];
  const eachParamArray = paramsString.split('&');
 let params = {};
 eachParamArray.forEach((param) => {
    const key = param.split('=')[0];
    const value = param.split('=')[1];
    Object.assign(params, {[key]: value});
  });
let vin = params["vin"];

var sixturl = "https://hackatum.goorange.sixt.com/vehicle/" + vin + "/lock";


 request1(sixturl, function (error, res, body) {
response.send(res);
    });



});
exports.packages = functions.https.onRequest(app);

exports.vehicle = functions.https.onRequest(veh);
