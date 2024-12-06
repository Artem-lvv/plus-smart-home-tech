package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderApiClient orderApiClient;
    private final ShoppingStoreApiClient shoppingStoreApiClient;

    @Override
    public BigDecimal getTotalCost(OrderDto orderDto) {
        return null;
    }

    @Override
    public PaymentDto payment(OrderDto orderDto) {
        if (Objects.isNull(orderDto) || Objects.isNull(orderDto.getShoppingCartId())) {
            throw new MethodArgumentNotValidException("orderId");
        }

        Payment payment = Payment.builder()
                .shoppingCartId(orderDto.getShoppingCartId().get())
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
        Optional<Payment> paymentByOrderId = paymentRepository.findByOrderId(body);

        if (Objects.isNull(body) || paymentByOrderId.isEmpty()) {
            throw new EntityNotFoundException("Order", body.toString());
        }

        paymentByOrderId.get().setPaymentStatus(PaymentStatus.FAILED);
        log.info("Payment failed: {}", paymentByOrderId);

        return null;
    }

    @Override
    public Void paymentSuccess(UUID body) {
        return null;
    }

    @Override
    public BigDecimal productCost(OrderDto orderDto) {
        Payment payment = paymentRepository.findById(orderDto.getPaymentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment", orderDto.getPaymentId().toString()));

        Double sum = 0.0;

        for (Map.Entry<String, Long> stringLongEntry : orderDto.getProducts().entrySet()) {
            ProductDto product = shoppingStoreApiClient.getProduct(UUID
                    .fromString(stringLongEntry.getKey())).getBody();

            sum += product.getPrice().doubleValue() * stringLongEntry.getValue();
        }




        return null;
    }
}
