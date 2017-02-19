package jp.hisano.util.defer;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public final class Defer {
	private static final ThreadLocal<Deque<List<Runnable>>> contexts = ThreadLocal.withInitial(LinkedList::new);

	public static void tryWithDefer(Runnable block) {
		Deque<List<Runnable>> context = contexts.get();

		LinkedList<Runnable> scope = new LinkedList<>();
		context.addLast(scope);
		try {
			block.run();
		} finally {
			Collections.reverse(scope);
			scope.stream().forEach(Runnable::run);

			context.removeLast();
			if (context.isEmpty()) {
				contexts.remove();
			}
		}
	}

	public static void defer(Runnable releaser) {
		Deque<List<Runnable>> context = contexts.get();

		if (context.isEmpty()) {
			throw new IllegalStateException("not called in tryWithDefer block");
		}

		List<Runnable> scope = context.getLast();
		scope.add(releaser);
	}

	private Defer() {
	}
}
