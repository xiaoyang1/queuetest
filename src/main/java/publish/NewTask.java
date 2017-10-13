package publish;

/*
 *这个类个worker是用来测试队列并行分发
 */
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {

    private static final String TASK_QUEUE_NAME = "task_queue";

    public  static void main(String []argv) throws  Exception{

        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection=connectionFactory.newConnection();
        Channel channel=connection.createChannel();
        //是否支持持久化，也就是保证队列和message不丢失。也就是写到磁盘。
        boolean durable=true;
        channel.queueDeclare(TASK_QUEUE_NAME,true,false,false,null);

        String message=getMessage(argv);
        channel.basicPublish("",TASK_QUEUE_NAME,MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes("UTF-8"));

        System.out.println("[x]  Sent  '"+ message +"  '");

        channel.close();
        connection.close();
    }

    public static String getMessage(String []strings){
        if(strings.length<1)
            return "hello world!";
        return joinString(strings,"  ");
    }

    public static String joinString(String[] strings,String delimiter){

        int length=strings.length;
        if(length==0) return  "";
        StringBuilder words=new StringBuilder(strings[0]);
        for (int i = 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }

}
