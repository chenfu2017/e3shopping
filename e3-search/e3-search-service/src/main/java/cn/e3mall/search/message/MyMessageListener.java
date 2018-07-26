package cn.e3mall.search.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class MyMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        String text = null;
        try {
            text = textMessage.getText();
            System.out.println(text);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
