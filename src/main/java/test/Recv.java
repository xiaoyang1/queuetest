package test;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Recv {
    private  final  static  String QUEUE_NAME="hello";

    public static void main(String []argv) throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        System.out.println(" [ * ] waiting for messages. to exit press CTRL+C ");

        Consumer consumer=new Consumer() {
            public void handleConsumeOk(String s) {

            }

            public void handleCancelOk(String s) {

            }

            public void handleCancel(String s) throws IOException {

            }

            public void handleShutdownSignal(String s, ShutdownSignalException e) {

            }

            public void handleRecoverOk(String s) {

            }


            public void handleDelivery(String s, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
                String message=new String(bytes,"UTF-8");
                System.out.println(" [x] Received : " + message + "  !");
            }
        };
        channel.basicConsume(QUEUE_NAME,true,consumer);

    }
}
