'use strict'
angular.module('myApp.rabbit', ['amqp', 'ng-table', 'ui.bootstrap'])
    .controller('tableCtrl', function ($rootScope, $scope, $http, amqpUtil, amqp) {
        var amqpConfig = {
            url: 'amqp://localhost',
            virtualHost: '/',
            credentials: {
                username: 'guest',
                password: 'guest'
            },
            exchange: 'amq.topic',
            queue: 'ftn/messages'
        };
        $scope.rowCollection = [
            {message: "example", source: "crawler", url: "http://www.ftn.uns.ac.rs", fetch_time: "2017-07-05"}
        ];
        $scope.displayedCollection = [].concat($scope.rowCollection);

        $scope.greeting = {};
        amqp.connect(amqpConfig).then(function () {

            var consumeChannel = amqp.client.openChannel(function (channel) {
                consumeChannel.addEventListener('declarequeue', function () {
                    console.log(amqpConfig.queue + ' declared');
                });
                consumeChannel.addEventListener('bindqueue', function () {
                    console.log('Bound to ' + amqpConfig.exchange);
                });
                consumeChannel.addEventListener('consume', function () {
                    console.log('Consuming');
                });
                consumeChannel.addEventListener('flow', function (e) {
                    console.log('Flow: ' + e);
                });
                consumeChannel.addEventListener('close', function () {
                    console.log('Channel closed');
                });
                consumeChannel.addEventListener('message', function (event) {
                    var body = amqpUtil.getBodyAsString(event);
                    $scope.rowCollection.push(angular.fromJson(body));
                    $scope.$applyAsync();
                    setTimeout(function () {
                        event.target.ackBasic({
                            deliveryTag: event.args.deliveryTag,
                            multiple: true
                        });
                    }, 0);
                });
                consumeChannel
                    .declareQueue({queue: amqpConfig.queue, durable: true})
                    .bindQueue({queue: amqpConfig.queue, exchange: amqpConfig.exchange, routingKey: amqpConfig.routingKey})
                    .consumeBasic({queue: amqpConfig.queue, noAck: false, consumerTag: "client" + Math.floor(Math.random() * 100000000)});
                console.log('Channel initialized!');
            });
            var publishChannel = amqp.client.openChannel(function (channel) {
                publishChannel.addEventListener("declareexchange", function () {
                    console.log("EXCHANGE DECLARED: " + amqpConfig.exchange);
                });
                publishChannel.addEventListener("error", function (e) {
                    console.log("CHANNEL ERROR: Publish Channel - " + e.message);
                });
                publishChannel.addEventListener("close", function () {
                    console.log("CHANNEL CLOSED: Publish Channel");
                });
            });
            var unregister = $scope.$on('$routeChangeStart', function () {
                console.log('Route changed');
                consumeChannel.closeChannel({});
                unregister();
            });
        }, function () {
        });
    });
