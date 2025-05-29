package com.kasa.adr.util;


import club.cred.push.v1.*;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.kasa.adr.model.Case;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PushClient {

    private static final Logger logger = Logger.getLogger(PushClient.class.getName());

    private final ManagedChannel channel;
    private final PushServiceGrpc.PushServiceBlockingStub blockingStub;
    private final PushServiceGrpc.PushServiceStub asyncStub;


    public PushClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.blockingStub = PushServiceGrpc.newBlockingStub(channel);
        this.asyncStub = PushServiceGrpc.newStub(channel);
    }

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 5011;
        String clientId = "test";
        String deviceId = "device";
        String topic = null;
        String action = "send-event";

        PushClient client = new PushClient(host, port);
        try {
            switch (action) {
                case "send-event":
                    client.sendMessage(clientId, deviceId, topic);
                    break;
                case "connect":
                    client.connect(clientId, topic);
                    break;
                case "list-client-devices":
                    client.listClientDevices(clientId);
                    break;
                default:
                    logger.info("Unknown action: " + action);
            }
        } finally {
            client.shutdown();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void sendMessage(String clientId, String deviceId, String topic) {
        try {
            Event event = Event.newBuilder()
                    .setName("PAYMENT_SUCCESS")
                    .setFormatType(Event.Type.TYPE_JSON_UNSPECIFIED)
                    .setData(Any.newBuilder().setValue(ByteString.copyFromUtf8(Case.builder().mobile("1234512345").build().toString())))
                    .build();

            if (topic == null || topic.isEmpty()) {
                SendEventToClientChannelRequest request = SendEventToClientChannelRequest.newBuilder()
                        .setClientId(clientId)
                        .setEvent(event)
                        .build();
                blockingStub.sendEventToClientChannel(request);

                if (deviceId != null && !deviceId.isEmpty()) {
                    SendEventToClientDeviceChannelRequest deviceRequest = SendEventToClientDeviceChannelRequest.newBuilder()
                            .setClientId(clientId)
                            .setDeviceId(deviceId)
                            .setEvent(event)
                            .build();
                    blockingStub.sendEventToClientDeviceChannel(deviceRequest);
                }
            } else {
                SendEventToTopicRequest topicRequest = SendEventToTopicRequest.newBuilder()
                        .setTopic(topic)
                        .setEvent(event)
                        .build();
                blockingStub.sendEventToTopic(topicRequest);
            }
            logger.info("Event sent successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending message", e);
        }
    }

    public void connect(String clientId, String topic) {
        StreamObserver<ChannelResponse> responseObserver = new StreamObserver<ChannelResponse>() {
            @Override
            public void onNext(ChannelResponse response) {
                if (response.hasChannelEvent()) {
                    logger.info("Received event: " + response.getChannelEvent().getEvent().getName());
                } else if (response.hasConnectAck()) {
                    logger.info("Connect acknowledged");
                }
            }

            @Override
            public void onError(Throwable t) {
                logger.log(Level.SEVERE, "Error during connection", t);
            }

            @Override
            public void onCompleted() {
                logger.info("Stream completed");
            }
        };

        StreamObserver<ChannelRequest> requestObserver = asyncStub.channel(responseObserver);
        try {
            if (topic != null && !topic.isEmpty()) {
                TopicSubscriptionRequest subscriptionRequest = TopicSubscriptionRequest.newBuilder()
                        .setTopic(topic)
                        .build();

                requestObserver.onNext(ChannelRequest.newBuilder().setTopicSubscriptionRequest(subscriptionRequest).build());
                TimeUnit.SECONDS.sleep(10);

                TopicUnsubscriptionRequest unsubscriptionRequest = TopicUnsubscriptionRequest.newBuilder()
                        .setTopic(topic)
                        .build();
                requestObserver.onNext(ChannelRequest.newBuilder().setTopicUnsubscriptionRequest(unsubscriptionRequest).build());
                logger.info("Subscribed and unsubscribed to topic: " + topic);
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Interrupted", e);
        } finally {
            requestObserver.onCompleted();
        }
    }

    public void listClientDevices(String clientId) {
        try {
            GetClientActiveDevicesRequest request = GetClientActiveDevicesRequest.newBuilder()
                    .setClientId(clientId)
                    .build();

            GetClientActiveDevicesResponse response = blockingStub.getClientActiveDevices(request);
            logger.info("Active devices: " + response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error listing devices", e);
        }
    }
}