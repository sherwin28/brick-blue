package net.isger.brick.blue.seal;

import net.isger.brick.blue.MethodSeal;
import net.isger.brick.blue.Marks.TYPE;

public class MethodQuiet extends MethodSeal {

    public MethodQuiet(String owner, int access, String type, String name,
            String[] argTypes) {
        super(owner, access, type, name, argTypes);
    }

    public void markConst(String alias, Object value) {
        if (value != null && value instanceof TYPE) {
            TYPE type = (TYPE) value;
            if (type.sort == TYPE.OBJECT.sort) {
                this.markConst(type.name, type.name);
                this.markCoding(alias, null, "class", type.name);
                return;
            }
        }
        super.markConst(alias, value);
    }

}
