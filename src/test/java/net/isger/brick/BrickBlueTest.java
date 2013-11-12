package net.isger.brick;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BrickBlueTest extends TestCase {

    public BrickBlueTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(BrickBlueTest.class);
    }

    public static interface TestA {

        public void getId();

        public void getName();

    }

    public void testBlue() throws Exception {
        ((TestA) new Standin(TestA.class).getSource()).getName();
        // byte[] code = null;
        // Class<?> exampleClass = null;
        // ClassSeal cs = null;
        // MethodSeal ms = null;
        // // public class Example
        // cs = ClassSeal.create(VERSION.V0104.value, ACCESS.PUBLIC.value,
        // "Example", TYPE.OBJECT.name);
        //
        // // public Example()
        // ms = cs.makeMethod(ACCESS.PUBLIC.value, "void", "<init>");
        // ms.markConst("greeting", "This is blue of isger(init).");
        // ms.coding("this", "super()"); // super();
        // ms.coding("out", "println(obj)", "greeting"); //
        // System.out.println(greeting);
        //
        // // public static void main(String[])
        // ms = cs.makeMethod(ACCESS.PUBLIC.value | ACCESS.STATIC.value, "void",
        // "main", "java.lang.String[]");
        // ms.markConst("greeting", "This is blue of isger(main).");
        // ms.markOperate("Example()", "Example", OPCODES.INVOKESPECIAL.value,
        // "void", "<init>");
        // ms.markOperate("println(int)", TYPE.PRINTSTREAM.name,
        // OPCODES.INVOKEVIRTUAL.value, "void", "println", TYPE.INT.name);
        // ms.markCoding("args.length", "array", "length", "args[0]");
        // ms.markCoding("new Example()", "new", "Example()");
        // String e = ms.coding("new", "Example()");// Example e = new
        // Example();
        // ms.coding("out", "println(obj)", e); // System.out.println(e);
        // ms.coding("out", "println(obj)", "class"); //
        // System.out.println(Example.class);
        //
        // code = net.isger.brick.blue.Compiler.compile(cs);
        // exampleClass = new BlueClassLoader().load("Example", code, 0,
        // code.length);
        // Method[] methods = exampleClass.getMethods();
        // methods[0].invoke(null, new Object[] { new String[] {} });
        // FileOutputStream fos = new FileOutputStream("Example.class");
        // fos.write(code);
        // fos.close();
        // System.out.println(new File("E:\\Example.class").getAbsolutePath());
    }

    // private static class BlueClassLoader extends ClassLoader {
    //
    // public Class<?> load(String name, byte[] code, int off, int len) {
    // return this.defineClass(name, code, off, len);
    // }
    //
    // }

}
