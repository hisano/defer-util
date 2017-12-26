package jp.hisano.util.defer;

import static jp.hisano.util.defer.Defer.defer;
import static jp.hisano.util.defer.Defer.tryWithDefer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.junit.jupiter.api.Test;

class DeferTest {
	@Test
	void deferMethod() {
		Queue<String> messages = new LinkedList<>();

		tryWithDefer(() -> {
			defer(() -> messages.add("dispose"));
			messages.add("use");
		});

		assertEquals("use", messages.poll());
		assertEquals("dispose", messages.poll());
	}

	@Test
	void enclosedDefer() {
		Queue<String> messages = new LinkedList<>();

		tryWithDefer(() -> {
			defer(() -> messages.add("dispose"));

			tryWithDefer(() -> {
				defer(() -> messages.add("enclosedDispose"));
				messages.add("enclosedUse");
			});

			messages.add("use");
		});

		assertEquals("enclosedUse", messages.poll());
		assertEquals("enclosedDispose", messages.poll());
		assertEquals("use", messages.poll());
		assertEquals("dispose", messages.poll());
	}

	@Test
	void nullCheck() {
		assertThrows(NullPointerException.class, () -> tryWithDefer(null));

		assertThrows(NullPointerException.class, () -> {
			tryWithDefer(() -> {
				defer(null);
			});
		});
	}

	@Test
	void stateCheck() {
		assertThrows(IllegalStateException.class, () -> defer(() -> {}));
	}

	@Test
	void checkedException() {
		assertThrows(DeferException.class, () -> {
			tryWithDefer(() -> {
				throw new IOException();
			});
		});

		assertThrows(DeferException.class, () -> {
			tryWithDefer(() -> {
				defer(() -> {
					throw new IOException();
				});
			});
		});
	}
}
