package jp.hisano.util.defer.example;

import static jp.hisano.util.defer.Defer.*;

public class Example {
	public static void main(String[] args) {
		tryWithDefer(() -> {
			Resource resource = new Resource();
			defer(resource::dispose);

			resource.use();
		});
	}

	private static class Resource {
		void use() {
			System.out.println("use");
		}

		void dispose() {
			System.out.println("dispose");
		}
	}
}
