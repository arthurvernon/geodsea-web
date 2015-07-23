'use strict';

geodseaApp.controller('RescueController', ['$scope',
    function ($scope) {

        // from chat
        $scope.model = {
            messages: [],
            author: ''
        };


        // keep a reference to the global atmosphere object.
        $scope.chatSocket = atmosphere;

        // the actual communication channel opened for this service
        $scope.chatSubSocket;

        // Start of using this protocol
//        $scope.chatTransport = 'websocket';

        $scope.chatRequest = { url: 'websocket/chat?monitor=4',
            contentType : "application/json",
            transport : 'websocket', //$scope.chatTransport ,
            trackMessageLength : true,
            reconnectInterval : 5000,
            enableXDR: true,
            timeout : 60000 };

        $scope.chatRequest.onOpen = function(response) {
console.log('on open');

            // from tracker
//            $scope.chatTransport = response.transport;  // same

            // from chat
            $scope.model.connected = true;
            $scope.model.content = 'Atmosphere connected using ' + response.transport;

            $scope.$apply();

        };

        $scope.chatRequest.onMessage = function(response){
console.log('on message');
            var responseText = response.responseBody;
            try{
                var message = atmosphere.util.parseJSON(responseText);
                if(!$scope.model.logged && $scope.model.name)
                    $scope.model.logged = true;
                else{
                    var date = typeof(message.time) === 'string' ? parseInt(message.time) : message.time;
                    $scope.model.messages.push({author: message.author, date: new Date(date), text: message.message});
                }
            }catch(e){
                console.error("Error parsing JSON: ", responseText);
//                throw e;
            }
            $scope.$apply();

            // added this as it appears to be missing
 //           $scope.$apply();
        };


        // only chat does this
        $scope.chatRequest.onClientTimeout = function(response){
console.log('on client timeout');
            $scope.model.content = 'Client closed the connection after a timeout. Reconnecting in ' + $scope.chatRequest.reconnectInterval;
            $scope.model.connected = false;
            $scope.chatSubSocket.push(atmosphere.util.stringifyJSON({ author: $scope.model.name, message: 'is inactive and closed the connection. Will reconnect in ' + $scope.chatRequest.reconnectInterval }));
            setTimeout(function(){
                $scope.chatSubSocket = $scope.chatSocket.subscribe($scope.chatRequest);
            }, $scope.chatRequest.reconnectInterval);
        };


        // only chat does this
        $scope.chatRequest.onReopen = function(response){
console.log('on reopen');
            $scope.model.connected = true;
            $scope.model.content = 'Atmosphere re-connected using ' + response.transport;
        };


        // only chat does this.
        $scope.chatRequest.onClose = function(response){
console.log('on close');
            $scope.model.connected = false;
            $scope.model.content = 'Server closed the connection after a timeout';
            $scope.chatSubSocket.push(atmosphere.util.stringifyJSON({ author: $scope.model.name, message: 'disconnecting' }));
        };

        // only chat does this
        $scope.chatRequest.onError = function(response){
console.log('on error');
            $scope.model.content = "Sorry, but there's some problem with your socket or the server is down";
            $scope.model.logged = false;
        };



        // only chat does this
        $scope.chatRequest.onReconnect = function(request, response){
console.log('on reconnect');
            $scope.model.content = 'Connection lost. Trying to reconnect ' + request.reconnectInterval;
            $scope.model.connected = false;
        };


        //For demonstration of how you can customize the fallbackTransport using the onTransportFailure function
        // only done by chat
        $scope.chatRequest.onTransportFailure = function(errorMsg, request){
console.log('on transport failure');
            atmosphere.util.info(errorMsg);
            request.fallbackTransport = 'long-polling';
            $scope.model.header = 'Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport;
        };

        // All setup, so now subscribe!
        $scope.chatSubSocket = $scope.chatSocket.subscribe($scope.chatRequest);

        var input = $('#input');
        input.keydown(function(event){
            var me = this;
            var msg = $(me).val();
            if(msg && msg.length > 0 && event.keyCode === 13){
                $scope.$apply(function(){
                    // First message is always the author's name
                    if(!$scope.model.name)
                        $scope.model.name = msg;

                    $scope.chatSubSocket.push(atmosphere.util.stringifyJSON({author: $scope.model.name, message: msg}));
                    $(me).val('');
                });
            }
        });
    }])