package route;

import com.rabbitmq.client.*;

import java.io.IOException;

public class ReceiveLogsDirect  {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception{

        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection=connectionFactory.newConnection();
        Channel channel=connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        String queuename=channel.queueDeclare().getQueue();

        if (args.length < 1){
            System.err.println("Usage: ReceiveLogsDirect [info] [warning] [error]");
            System.exit(1);
        }
        // severity  is the routekeys
        for(String severity : args){
            channel.queueBind(queuename,EXCHANGE_NAME,severity);
        }
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer=new DefaultConsumer(channel){

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + envelope.getRoutingKey() + "':'" + message + "'");
            }
        };
        channel.basicConsume(queuename,true,consumer);
    }
}
