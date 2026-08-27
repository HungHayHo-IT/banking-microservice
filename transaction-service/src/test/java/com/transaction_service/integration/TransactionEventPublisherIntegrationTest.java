package com.transaction_service.integration;

import com.transaction_service.config.KafkaTopicProperties;
import com.transaction_service.enums.Currency;
import com.transaction_service.enums.TransactionDirection;
import com.transaction_service.enums.TransactionStatus;
import com.transaction_service.kafka.dto.BalanceUpdateEvent;
import com.transaction_service.kafka.service.TransactionEventPublisher;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@EmbeddedKafka(
        partitions = 1,
        topics = TransactionEventPublisherIntegrationTest.TOPIC,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@Import(TransactionEventPublisherIntegrationTest.KafkaPublisherTestConfig.class)
class TransactionEventPublisherIntegrationTest {

    static final String TOPIC = "balance-update-events";

    private final EmbeddedKafkaBroker embeddedKafka;
    private Consumer<String, String> consumer;

    TransactionEventPublisherIntegrationTest(EmbeddedKafkaBroker embeddedKafka) {
        this.embeddedKafka = embeddedKafka;
    }

    @BeforeEach
    void setUpConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                embeddedKafka,
                "transaction-event-publisher-test-group",
                false
        );
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        var consumerFactory = new org.springframework.kafka.core.DefaultKafkaConsumerFactory<String, String>(consumerProps);
        consumer = consumerFactory.createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, TOPIC);
    }

    @AfterEach
    void tearDownConsumer() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void publishBalanceUpdateRequested_shouldPublishCorrectTopicKeyAndPayload() {
        BalanceUpdateEvent event = BalanceUpdateEvent.builder()
                .accountNumber("ACC-001")
                .amount(new BigDecimal("100.50"))
                .transactionDirection(TransactionDirection.CREDIT)
                .transactionStatus(TransactionStatus.SUCCESS)
                .reference("TX-001")
                .currency(Currency.VND)
                .email("hung@example.com")
                .firstName("Hung")
                .currentBalance(new BigDecimal("1000.50"))
                .description("Deposit")
                .correlationId("CORR-001")
                .build();

        producer().publishBalanceUpdateRequested(event).join();

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(
                consumer,
                TOPIC,
                Duration.ofSeconds(10)
        );

        assertThat(record.topic()).isEqualTo(TOPIC);
        assertThat(record.key()).isEqualTo("ACC-001");
        assertThat(record.value())
                .contains("\"accountNumber\":\"ACC-001\"")
                .contains("\"amount\":100.50")
                .contains("\"transactionDirection\":\"CREDIT\"")
                .contains("\"transactionStatus\":\"SUCCESS\"")
                .contains("\"reference\":\"TX-001\"")
                .contains("\"currency\":\"VND\"");
    }

    private TransactionEventPublisher producer() {
        return testConfigPublisher;
    }

    private final TransactionEventPublisher testConfigPublisher = null;

    @TestConfiguration
    static class KafkaPublisherTestConfig {

        @Bean
        KafkaTopicProperties kafkaTopicProperties() {
            KafkaTopicProperties properties = new KafkaTopicProperties();
            properties.setBalanceUpdateRequested(TOPIC);
            properties.setBalanceUpdateNotification("balance-update-notification-events");
            properties.setTransactionCompleted("banking.transaction.completed.v1");
            properties.setTransactionFailed("transaction-failed-events");
            return properties;
        }

        @Bean
        ProducerFactory<String, Object> producerFactory(EmbeddedKafkaBroker embeddedKafka) {
            Map<String, Object> props = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka));
            props.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            props.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new DefaultKafkaProducerFactory<>(props);
        }

        @Bean
        KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
        }

        @Bean
        TransactionEventPublisher transactionEventPublisher(
                KafkaTemplate<String, Object> kafkaTemplate,
                KafkaTopicProperties kafkaTopicProperties) {
            return new TransactionEventPublisher(kafkaTemplate, kafkaTopicProperties);
        }
    }
}
