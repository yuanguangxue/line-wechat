function getServiceUrl() {
    return document.getElementById("serviceUrl").value;
}
function getHTTPHead(){
    var env =  document.getElementById("env").value;
    if(env === 'TMP'){
        return 'https://'
    }
    return 'http://'
}

function getWebSocketHead(){
    var env =  document.getElementById("env").value;
    if(env === 'TMP'){
        return 'wss://'
    }
    return 'ws://'
}
const appId = document.getElementById("appId").value;
const appKey = document.getElementById("appKey").value;

function init(){

}

function wsConn(deviceId) {
    var ws = new WebSocket(getWebSocketHead() + getServiceUrl() + "/msgpush.ws?deviceId="+deviceId);
    var connectionLabel = document.getElementById("connectionLabel");
    var writeMsg = document.getElementById("writeMsg");

    ws.onmessage = function(evt) {
        writeMsg.value = evt.data;
    };

    ws.onclose = function(evt) {
        connectionLabel.innerHTML = "DisConnected";
    };

    ws.onopen = function(evt) {
        connectionLabel.innerHTML = "Connected";
    };

    ws.onerror = function(event) {
        alert("error ocoure");
    };
}
