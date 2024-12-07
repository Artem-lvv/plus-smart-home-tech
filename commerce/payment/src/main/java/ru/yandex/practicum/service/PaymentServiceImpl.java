package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.exception.EntityNotFoundException;
import ru.yandex.practicum.exception.MethodArgumentNotValidException;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.model.PaymentStatus;
import ru.yandex.practicum.order_api.api.OrderApiClient;
import ru.yandex.practicum.payment_api.model.OrderDto;
import ru.yandex.practicum.payment_api.model.PaymentDto;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.shopping_store_api.api.ShoppingStoreApiClient;
import ru.yandex.practicum.shopping_store_api.model.ProductDto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = false)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderApiClient orderApiClient;
    private final ShoppingStoreApiClient shoppingStoreApiClient;

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        double sumProduct = getSumProduct(orderDto);
        double feeProduct = sumProduct * 10 / (10 + 100); // налог

        return BigDecimal.valueOf(sumProduct + feeProduct + orderDto.getDeliveryPrice().doubleValue());
    }

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        if (Objects.isNull(orderDto) || Objects.isNull(orderDto.getShoppingCartId())) {
            throw new MethodArgumentNotValidException("orderId");
        }

        Payment payment = Payment.builder()
                .shoppingCartId(orderDto.getShoppingCartId().get())
                .totalPayment(orderDto.getTotalPrice())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        log.info("Payment created: {}", payment);

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentId(payment.getPaymentId());

        return paymentDto;
    }

    @Override
    public Void paymentFailed(UUID body) {
        Payment paymentByOrderId = paymentRepository.findByOrderId(body)
                .orElseThrow(() -> new EntityNotFoundException("Payment", body.toString()));

        orderApiClient.paymentFailed(body, body);

        paymentByOrderId.setPaymentStatus(PaymentStatus.FAILED);

        paymentRepository.save(paymentByOrderId);
        log.info("Payment FAILED: {}", paymentByOrderId);

        return null;
    }

    @Override
    public Void paymentSuccess(UUID body) {
        Payment paymentByOrderId = paymentRepository.findByOrderId(body)
                .orElseThrow(() -> new EntityNotFoundException("Payment", body.toString()));

        orderApiClient.paymentSuccess(body, body);

        paymentByOrderId.setPaymentStatus(PaymentStatus.SUCCESS);

        paymentRepository.save(paymentByOrderId);
        log.info("Payment SUCCESS: {}", paymentByOrderId);

        return null;
    }

    @Override
    public BigDecimal productCost(OrderDto orderDto) {
        paymentRepository.findById(orderDto.getPaymentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment", orderDto.getPaymentId().toString()));

        double sum = getSumProduct(orderDto);

        return BigDecimal.valueOf(sum);
    }

    private double getSumProduct(OrderDto orderDto) {
        double sum = 0.0;

        for (Map.Entry<String, Long> stringLongEntry : orderDto.getProducts().entrySet()) {
            ProductDto product = shoppingStoreApiClient.getProduct(UUID
                    .fromString(stringLongEntry.getKey())).getBody();

            sum += product.getPrice().doubleValue() * stringLongEntry.getValue();
        }
        return sum;
    }
}
