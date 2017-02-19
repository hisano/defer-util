# defer-util

*defer block in Java like Swift and Go*

## Introduction

defer-util is a defer block library for releasing resources.

## Usage

Use `defer` block to release resources after `tryWithDefer` block end.

```java
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
```
## License

Copyright 2017 Koji Hisano. Released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
