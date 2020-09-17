package io.smallrye.mutiny.operators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.TestException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.test.AssertSubscriber;

@SuppressWarnings("ConstantConditions")
public class MultiOnItemCallTest {

    @Test
    public void testOnItemWithSupplier() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4)
                .onItem().call(() -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertCompletedSuccessfully();
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnItemWithSupplierShortcut() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4)
                .call(() -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertCompletedSuccessfully();
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnItemWithFunction() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4)
                .onItem().call(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(i);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertCompletedSuccessfully();
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnItemWithFunctionShortcut() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4)
                .call(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(i);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertCompletedSuccessfully();
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnItemWithFunctionDeprecatedShortcut() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4)
                .invokeUni(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(i);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertCompletedSuccessfully();
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnItemWithFunctionDeprecated() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4)
                .onItem().invokeUni(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(i);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertCompletedSuccessfully();
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnFailureWithSupplier() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4).onCompletion().fail()
                .onItem().call(() -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertHasFailedWith(NoSuchElementException.class, null);
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnFailureWithFunction() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().items(1, 2, 3, 4).onCompletion().fail()
                .onItem().call(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2, 3, 4).assertHasFailedWith(NoSuchElementException.class, null);
        assertThat(counter).hasValue(4);
    }

    @Test
    public void testOnCompletionWithSupplier() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().empty()
                .onItem().call(() -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertHasNotReceivedAnyItem().assertCompletedSuccessfully();
        assertThat(counter).hasValue(0);
    }

    @Test
    public void testOnCompletionWithFunction() {
        AtomicInteger counter = new AtomicInteger();
        Multi.createFrom().empty()
                .onItem().call(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertHasNotReceivedAnyItem().assertCompletedSuccessfully();
        assertThat(counter).hasValue(0);
    }

    @Test
    public void testCancellationWithSupplier() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().nothing()
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(() -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .cancel()
                .assertHasNotReceivedAnyItem().assertNotTerminated();
        assertThat(counter).hasValue(0);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testCancellationWithFunction() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().nothing()
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(5);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .cancel()
                .assertHasNotReceivedAnyItem().assertNotTerminated();
        assertThat(counter).hasValue(0);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testCancellationWithSupplierAndPendingUni() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1)
                .onItem().call(() -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().emitter(e -> e.onTermination(() -> cancelled.set(true)));
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .cancel()
                .assertHasNotReceivedAnyItem().assertNotTerminated();
        assertThat(counter).hasValue(1);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testCancellationWithFunctionAndPendingUni() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1)
                .onItem().call(i -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().emitter(e -> e.onTermination(() -> cancelled.set(true)));
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .cancel()
                .assertHasNotReceivedAnyItem().assertNotTerminated();
        assertThat(counter).hasValue(1);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testNullSupplierOrFunction() {
        assertThatThrownBy(() -> Multi.createFrom().items(1)
                .onItem().call((Function<? super Integer, Uni<?>>) null)).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Multi.createFrom().items(1)
                .onItem().call((Supplier<Uni<?>>) null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testMapperThrowingExceptionOnItemWithSupplier() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1, 2, 3, 4)
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(() -> {
                    if (counter.incrementAndGet() == 3) {
                        throw new TestException("boom");
                    }
                    return Uni.createFrom().item(0);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2).assertHasFailedWith(TestException.class, "boom");
        assertThat(counter).hasValue(3);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testMapperThrowingExceptionOnItemWithFunction() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1, 2, 3, 4)
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(i -> {
                    if (counter.incrementAndGet() == 3) {
                        throw new TestException("boom");
                    }
                    return Uni.createFrom().item(i);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2).assertHasFailedWith(TestException.class, "boom");
        assertThat(counter).hasValue(3);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testMapperReturningNullWithSupplier() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1, 2, 3, 4)
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(() -> {
                    if (counter.incrementAndGet() == 3) {
                        return null;
                    }
                    return Uni.createFrom().item(0);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2).assertHasFailedWith(NullPointerException.class, "");
        assertThat(counter).hasValue(3);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testMapperReturningNullWithFunction() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1, 2, 3, 4)
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(i -> {
                    if (counter.incrementAndGet() == 3) {
                        return null;
                    }
                    return Uni.createFrom().item(0);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2).assertHasFailedWith(NullPointerException.class, "");
        assertThat(counter).hasValue(3);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testMapperProducingFailureWithSupplier() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1, 2, 3, 4)
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(() -> {
                    if (counter.incrementAndGet() == 3) {
                        return Uni.createFrom().failure(new TestException("boom"));
                    }
                    return Uni.createFrom().item(0);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2).assertHasFailedWith(TestException.class, "boom");
        assertThat(counter).hasValue(3);
        assertThat(cancelled).isTrue();
    }

    @Test
    public void testMapperProducingFailureWithFunction() {
        AtomicInteger counter = new AtomicInteger();
        AtomicBoolean cancelled = new AtomicBoolean();
        Multi.createFrom().items(1, 2, 3, 4)
                .onCancellation().invoke(() -> cancelled.set(true))
                .onItem().call(i -> {
                    if (counter.incrementAndGet() == 3) {
                        return Uni.createFrom().failure(new TestException("boom"));
                    }
                    return Uni.createFrom().item(0);
                })
                .subscribe().withSubscriber(AssertSubscriber.create(10))
                .assertReceived(1, 2).assertHasFailedWith(TestException.class, "boom");
        assertThat(counter).hasValue(3);
        assertThat(cancelled).isTrue();
    }
}