package rpc;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";
    private String replyQueueName;

    public RPCClient()throws IOException,TimeoutException{
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connection = connectionFactory.newConnection();
        channel = connection.createChannel();
        replyQueueName = channel.queueDeclare().getQueue();
    }

    public String call(String message) throws IOException, InterruptedException{
        String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties props=new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();

        channel.basicPublish("",requestQueueName,props,message.getBytes("UTF-8"));
        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
        channel.basicConsume(replyQueueName,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if(properties.getCorrelationId().equals(corrId)){
                    response.offer(new String(body,"UTF-8"));
                }
            }
        });
        //当服务器没有返回时，由于阻塞队列为空。所以take方法会一直阻塞，直到有元素返回为止。
        return response.take();
    }

    public void close() throws IOException {
        connection.close();
    }

    public static void main(String[] args) {
        RPCClient fibonacciRpc = null;
        String response = null;

        try {
            fibonacciRpc = new RPCClient();
            System.out.println(" [x] Requesting fib(30)");
            response = fibonacciRpc.call("30");
            System.out.println(" [.] Got '" + response + "'");
        }catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            if(fibonacciRpc!=null)
            {
                try {
                    fibonacciRpc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
