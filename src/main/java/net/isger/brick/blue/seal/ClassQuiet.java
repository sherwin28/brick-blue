package net.isger.brick.blue.seal;

import net.isger.brick.blue.ClassSeal;
import net.isger.brick.blue.MethodSeal;

public class ClassQuiet extends ClassSeal {

    public ClassQuiet(int version, int access, String name, String superName,
            String[] interfaces) {
        super(version, access, name, superName, interfaces);
        this.makeClassMethod();
    }

    private void makeClassMethod() {
        // static Class class$(String name) {
        MethodSeal ms = super.makeMethod(ACCESS.STATIC.value, TYPE.CLASS.name,
                "class$", TYPE.STRING.name);
        ms.markOperate("Class.forName(str)", TYPE.CLASS.name,
                OPCODES.INVOKESTATIC.value, TYPE.CLASS.name, "forName",
                TYPE.STRING.name);
        ms.markCoding("Class.forName(str)", null, "Class.forName(str)",
                MISC.arg(0));
        // return Class.forName(name);
        ms.coding("return", null, "Class.forName(str)");
        // }
    }

    public MethodSeal makeMethod(int access, String type, String name,
            String... argTypes) {
        MethodSeal ms = new MethodQuiet(this.getName(), access, type, name,
                argTypes);
        return this.addMethod(ms) ? ms : null;
    }

    public boolean addMethod(MethodSeal ms) {
        boolean result = super.addMethod(ms);
        if (result) {
            String owner = ms.getOwner();
            ms.markOperate("class", owner, OPCODES.INVOKESTATIC.value,
                    TYPE.CLASS.name, "class$", TYPE.STRING.name);
        }
        return result;
    }
}
