package jp.hisano.util.defer;

import static jp.hisano.util.defer.Defer.defer;
import static jp.hisano.util.defer.Defer.tryWithDefer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import junit.framework.TestCase;

public class DeferTest extends TestCase {
	public void testDefer() {
		Queue<String> messages = new LinkedList<>();

		tryWithDefer(() -> {
			defer(() -> messages.add("dispose"));
			messages.add("use");
		});

		assertEquals("use", messages.poll());
		assertEquals("dispose", messages.poll());
	}

	public void testEnclosedDefer() {
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

	public void testNullCheck() {
		try {
			tryWithDefer(null);
			fail();
		} catch (NullPointerException e) {
		}

		try {
			tryWithDefer(() -> {
				defer(null);
			});
			fail();
		} catch (NullPointerException e) {
		}
	}

	public void testStateCheck() {
		try {
			defer(() -> {});
			fail();
		} catch (IllegalStateException e) {
		}
	}

	public void testCheckedException() {
		try {
			tryWithDefer(() -> {
				throw new IOException();
			});
			fail();
		} catch (DeferException e) {
		}

		try {
			tryWithDefer(() -> {
				defer(() -> {
					throw new IOException();
				});
			});
			fail();
		} catch (DeferException e) {
		}
	}
}
