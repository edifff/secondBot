package bot.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static bot.model.RabbitQueue.*;

@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter(){

        return new Jackson2JsonMessageConverter();
    }
    @Bean
    public Queue textMessageConverter(){

        return new Queue(TEXT_MESSAGE_UPDATE);
    }

    @Bean
    public Queue docMessageConverter(){

        return new Queue(DOC_MESSAGE_UPDATE);
    }

    @Bean
    public Queue photoMessageConverter(){

        return new Queue(PHOTO_MESSAGE_UPDATE);
    }

    @Bean
    public Queue answerMessageConverter(){

        return new Queue(ANSWER_MESSAGE);
    }
}
