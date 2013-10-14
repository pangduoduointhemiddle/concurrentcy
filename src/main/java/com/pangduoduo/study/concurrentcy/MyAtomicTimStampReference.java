package com.pangduoduo.study.concurrentcy;



import java.util.concurrent.atomic.AtomicStampedReference;

public class MyAtomicTimStampReference<V> {

	private static class Pair<V> {
		final V valueRefence;
		final Long timeStamp;

		public Pair(V valueRefence, Long timeStamp) {
			super();
			this.valueRefence = valueRefence;
			this.timeStamp = timeStamp;
		}

		static <V> Pair<V> of(V reference, Long stamp) {
			return new Pair<V>(reference, stamp);
		}
	}

	volatile Pair<V> pair;

	public boolean compareAndSet(V expectedValue, Long expectedTime, V newValue, Long newTime){
		Pair<V> current = this.pair;
		
		return (expectedValue == pair.valueRefence 
				&& expectedTime < pair.timeStamp 
				&& casPair(current, Pair.of(newValue, newTime)));
	}

	private static final sun.misc.Unsafe UNSAFE = sun.misc.Unsafe.getUnsafe();

	private static final long pairOffset = objectFieldOffset(UNSAFE, "pair",
			AtomicStampedReference.class);

	private boolean casPair(Pair<V> cmp, Pair<V> val) {
		return UNSAFE.compareAndSwapObject(this, pairOffset, cmp, val);
	}

	static long objectFieldOffset(sun.misc.Unsafe UNSAFE, String field,
			Class<?> klazz) {
		try {
			return UNSAFE.objectFieldOffset(klazz.getDeclaredField(field));
		} catch (NoSuchFieldException e) {
			// Convert Exception to corresponding Error
			NoSuchFieldError error = new NoSuchFieldError(field);
			error.initCause(e);
			throw error;
		}
	}
}
