import static org.mockito.Mockito.mock;

public class Testing {
    public static void main(String[] args) {
        MyClass mock = mock(MyClass.class);
        boolean b = mock.someMethod();
        System.out.println(b);
    }

    public static class MyClass {
        public boolean someMethod() {
            return true;
        }
    }
}
