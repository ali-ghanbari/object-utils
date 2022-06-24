package edu.iastate.objectutils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class SerializationWrapperTest {
    @Test
    public void testMakeSerializable1() {
        final ObjectUtils ou = ObjectUtils.build();
        final Object[] objects1 = {
                1,
                2.718D,
                4L,
                40.21F,
                null
        };
        final Object[] objects2 = {
                1,
                2.718D,
                4L,
                40.21F,
                null
        };
        assertTrue(ou.deepEquals(ou.makeSerializable(objects1), ou.makeSerializable(objects2)));
    }

    @Test
    public void testMakeSerializable2() {
        final ObjectUtils ou = ObjectUtils.build();
        Object[] oa1 = new Object[0];
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            oa1 = Arrays.copyOf(oa1, oa1.length + 1);
            if ((oa1.length - 1) % 2 == 0) {
                oa1[oa1.length - 1] = sb.toString();
            } else {
                oa1[oa1.length - 1] = oa1;
            }
        }
        Object[] oa2 = new Object[0];
        for (int i = 0; i < 100; i++) {
            final StringBuilder sb = new StringBuilder();
            sb.append("#");
            sb.append(i);
            oa2 = Arrays.copyOf(oa2, oa2.length + 1);
            if ((oa2.length - 1) % 2 == 0) {
                oa2[oa2.length - 1] = sb.toString();
            } else {
                oa2[oa2.length - 1] = oa2;
            }
        }
        assertTrue(ou.deepEquals(ou.makeSerializable(oa1), ou.makeSerializable(oa2)));
    }

    @Test
    public void testMakeSerializable3() {
        final ObjectUtils ou = ObjectUtils.build();

        final Set<String> strings1 = new HashSet<>();
        strings1.add("hello");
        strings1.add("world!");
        strings1.add("how");
        strings1.add("are");
        strings1.add("you?");
        strings1.add("is");
        strings1.add("everything");
        strings1.add("OK?");
        strings1.add("R U sure?!");
        final Set<String> strings2 = new HashSet<>();
        strings2.add("is");
        strings2.add("hello");
        strings2.add("world!");
        strings2.add("OK?");
        strings2.add("how");
        strings2.add("are");
        strings2.add("you?");
        strings2.add("R U sure?!");
        strings2.add("everything");

        Object[] obj = {strings1, strings2, null, null};
        obj[3] = obj;

        Object original = ou.makeSerializable(obj);

        final File f = new File("test-" + System.nanoTime() + ".temp");
        try (final OutputStream fos = Files.newOutputStream(f.toPath());
             final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(ou.makeSerializable(obj));
        } catch (IOException ignored) {}

        Object loaded = null;

        try (final InputStream fis = Files.newInputStream(f.toPath());
             final ObjectInputStream ois = new ObjectInputStream(fis)) {
            loaded = ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {}

        f.delete();

        assertTrue(ou.deepEquals(original, loaded));

        strings1.remove("hello");

        original = ou.makeSerializable(obj);

        assertFalse(ou.deepEquals(original, loaded));
    }
}