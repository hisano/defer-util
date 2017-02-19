package jp.hisano.util.defer;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public final class Defer {
	private static final ThreadLocal<Deque<List<CheckedRunnable>>> contexts = ThreadLocal.withInitial(LinkedList::new);

	public static void tryWithDefer(CheckedRunnable block) {
		Objects.requireNonNull(block);

		Deque<List<CheckedRunnable>> context = contexts.get();

		LinkedList<CheckedRunnable> scope = new LinkedList<>();
		context.addLast(scope);
		AtomicReference<RuntimeException> exception = new AtomicReference<>();
		try {
			block.run();
		} catch (RuntimeException e) {
			exception.set(e);
		} catch (Exception e) {
			exception.set(new DeferException(e));
		} finally {
			Collections.reverse(scope);
			scope.stream().forEach(releaser -> {
				try {
					releaser.run();
				} catch (Exception e) {
					if (exception.get() == null) {
						exception.set(new DeferException(e));
					}
					exception.get().addSuppressed(e);
				}
			});

			context.removeLast();
			if (context.isEmpty()) {
				contexts.remove();
			}
		}
		if (exception.get() != null) {
			throw exception.get();
		}
	}

	public static void defer(CheckedRunnable releaser) {
		Objects.requireNonNull(releaser);

		Deque<List<CheckedRunnable>> context = contexts.get();

		if (context.isEmpty()) {
			throw new IllegalStateException("not called in tryWithDefer block");
		}

		List<CheckedRunnable> scope = context.getLast();
		scope.add(releaser);
	}

	private Defer() {
	}
}
