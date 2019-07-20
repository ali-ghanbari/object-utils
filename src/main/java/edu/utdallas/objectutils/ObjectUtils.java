package edu.utdallas.objectutils;

import static edu.utdallas.objectutils.Commons.strictlyImmutable;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.reflect.FieldUtils;

public final class ObjectUtils {
	public static <T> void shallowCopy(final T dest, final T src) throws Exception {
//		if (dest.getClass() != src.getClass()) {
//			final String msg = String.format("soruce type %s does not match destination type %s",
//					src.getClass().getName(), dest.getClass().getName());
//			throw new IllegalArgumentException(msg);
//		}
		final Class<?> clazz = src.getClass();
		final List<Field> fields = FieldUtils.getAllFieldsList(clazz);
        for (final Field field : fields) {
            if (strictlyImmutable(field)) {
                continue;
            }
            final Object fieldValue = FieldUtils.readField(field, src, true);
            FieldUtils.writeField(field, dest, fieldValue, true);
        }
	}
}
