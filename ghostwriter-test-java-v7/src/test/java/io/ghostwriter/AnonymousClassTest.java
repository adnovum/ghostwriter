package io.ghostwriter;

import io.ghostwriter.test.MessageSequenceAsserter;
import io.ghostwriter.test.Parameter;
import io.ghostwriter.test.TestBase;
import org.junit.Test;

import java.util.AbstractList;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class AnonymousClassTest extends TestBase {

    @Test
    public void testAnonymousClass() {
        List<String> list = new AbstractList<String>() {

            @Override
            public int size() {
                return 0;
            }

            @Override
            public String get(int i) {
                return null;
            }

        };

        // we call clear here to drop the messages generated by the test method itself
        InMemoryTracerProvider.INSTANCE.getTracer().clearMessages();
        list.size();

        MessageSequenceAsserter.messageSequence()
                .entering("size")
                .returning("size", 0)
                .exiting("size");
    }

    @Test
    public void testMultipleAnonymousClasses() {
        List<String> list1 = new AbstractList<String>() {

            @Override
            public int size() {
                return 1;
            }

            @Override
            public String get(int i) {
                return null;
            }

        };

        InMemoryTracerProvider.INSTANCE.getTracer().clearMessages();
        list1.size();
        MessageSequenceAsserter.messageSequence()
                .entering("size")
                .returning("size", 1)
                .exiting("size");

        List<String> list2 = new AbstractList<String>() {

            @Override
            public int size() {
                return 2;
            }

            @Override
            public String get(int i) {
                return null;
            }

        };

        InMemoryTracerProvider.INSTANCE.getTracer().clearMessages();
        list2.size();
        MessageSequenceAsserter.messageSequence()
                .entering("size")
                .returning("size", 2)
                .exiting("size");
    }

    @Test
    public void testAnonymousClassErrorEvent() {
        List<String> list = new AbstractList<String>() {

            @Override
            public int size() {
                return 0;
            }

            @Override
            public String get(int i) {
                if (i < 0) {
                    throw new IndexOutOfBoundsException();
                }
                return null;
            }

        };

        // we call clear here to drop the messages generated by the test method itself
        InMemoryTracerProvider.INSTANCE.getTracer().clearMessages();
        try {
            list.get(-1);
        } catch (RuntimeException e) {
            assertTrue("Exception not thrown!", e != null);
        }

        MessageSequenceAsserter.messageSequence()
                .entering("get", new Parameter<>("i", -1))
                .onError("get", IndexOutOfBoundsException.class)
                .exiting("get");
    }

    @Test
    public void testNestedAnonymousClass() {
        List<String> list = new AbstractList<String>() {

            @Override
            public int size() {
                List<Integer> intList = new AbstractList<Integer>() {

                    @Override
                    public int size() {
                        return 444;
                    }

                    @Override
                    public Integer get(int index) {
                        return null;
                    }
                };

                return intList.size();
            }

            @Override
            public String get(int i) {
                if (i < 0) {
                    throw new IndexOutOfBoundsException();
                }
                return null;
            }

        };

        // we call clear here to drop the messages generated by the test method itself
        InMemoryTracerProvider.INSTANCE.getTracer().clearMessages();
        list.size();

        MessageSequenceAsserter.messageSequence()
                .entering("size")
                .entering("size")
                .returning("size", 444)
                .exiting("size")
                .returning("size", 444)
                .exiting("size");
    }

}
