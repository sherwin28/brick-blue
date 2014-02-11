package net.isger.brick.blue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 蓝图编译器
 * 
 * @author issing
 * 
 */
public class Compiler implements Marks {

    public static final String ATTR_METH_CODE = "Code";

    private Compiler c;

    private ClassSeal classSeal;

    private ConstPool pool;

    private ByteVector out;

    private Compiler(ClassSeal classSeal) {
        this.c = this;
        this.classSeal = classSeal;
        this.pool = new ConstPool();
        this.out = new ByteVector();
        this.compile();
    }

    /**
     * 编译字节码
     * 
     * @param classSeal
     * @return
     */
    public static byte[] compile(ClassSeal classSeal) {
        return new Compiler(classSeal).out.toArray();
    }

    /**
     * 编译入口
     * 
     * @return
     */
    private void compile() {
        this.out.putInt(MAGIC).putInt(classSeal.getVersion());
        ByteVector mainData = compileMain();
        // 输出常量池
        byte[] poolData = pool.toArray();
        this.out.putShort(pool.count()).put(poolData, 0, poolData.length);
        // 输出类主体
        this.out.put(mainData);
    }

    /**
     * 编译类主体
     * 
     * @return
     */
    private ByteVector compileMain() {
        ByteVector out = new ByteVector();
        // 输出类定义
        String name = classSeal.getSuperName();
        out.putShort(classSeal.getAccess());
        out.putShort(pool.takeClass(TYPE.getDesc(classSeal.getName())));
        out.putShort(name == null ? 0 : pool.takeClass(TYPE.getDesc(name)));
        String[] interfaces = classSeal.getInterfaces();
        out.putShort(interfaces.length);
        for (String itf : interfaces) {
            out.putShort(pool.takeClass(TYPE.getDesc(itf)));
        }
        // 输出类所有字段
        List<FieldSeal> fields = classSeal.getFields();
        out.putShort(fields.size());
        for (FieldSeal field : fields) {
            out.put(compileField(field));
        }
        // 输出类所有方法
        List<MethodSeal> methods = classSeal.getMethods();
        out.putShort(methods.size());
        for (MethodSeal method : methods) {
            out.put(compileMethod(method));
        }
        // TODO 输出类所有属性
        out.putShort(0);
        return out;
    }

    /**
     * 编译类字段
     * 
     * @param field
     * @return
     */
    private ByteVector compileField(FieldSeal field) {
        ByteVector out = new ByteVector();
        // 输出字段定义
        out.putShort(field.getAccess());
        out.putShort(pool.takeUTF8(field.getName()));
        out.putShort(pool.takeUTF8(TYPE.getDesc(field.getType(), true)));
        // TODO 输出字段所有属性
        out.putShort(0);
        return out;
    }

    /**
     * 编译方法
     * 
     * @param method
     * @return
     */
    private ByteVector compileMethod(MethodSeal method) {
        ByteVector out = new ByteVector();
        // 输出方法定义
        out.putShort(method.getAccess());
        out.putShort(pool.takeUTF8(method.getName()));
        out.putShort(pool.takeUTF8(TYPE.getMethDesc(method.getType(),
                method.getArgTypes())));
        // TODO 输出方法所有属性
        out.putShort(1);
        // 输出方法代码块
        out.putShort(pool.takeUTF8(ATTR_METH_CODE));
        MethodCompile mc = new MethodCompile(method);
        out.putInt(mc.out.getLength()).put(mc.out);
        return out;
    }

    /**
     * 方法代码编译
     * 
     * @author issing
     * 
     */
    private class MethodCompile {

        private MethodCompile mc;

        private MethodSeal methodSeal;

        private ByteVector out;

        private int stacks;

        private int locals;

        /** 本地变量集合（元素数组组成：变量索引，变量类型） */
        private Map<String, Object[]> vs;

        public MethodCompile(MethodSeal methodSeal) {
            this.mc = this;
            this.methodSeal = methodSeal;
            this.out = new ByteVector();
            // 非静态方法，关键字this占用一个本地首空间
            if (!ACCESS.isStatic(methodSeal.getAccess())) {
                this.locals++;
            }
            // 映射方法参数至本地空间
            this.vs = new HashMap<String, Object[]>();
            String[] argTypes = methodSeal.getArgTypes();
            int size = argTypes.length;
            for (int i = 0; i < size; i++) {
                this.vs.put(MISC.arg(i), MISC.var(this.locals++, argTypes[i]));
            }
            this.compile();
        }

        /**
         * 编译入口
         * 
         */
        private void compile() {
            // 输出代码主体
            ByteVector mainData = compileMain();
            this.out.putShort(stacks).putShort(locals);
            this.out.putInt(mainData.getLength()).put(mainData);
            // TODO 输出代码所有处理
            this.out.putShort(0);
            // TODO 输出代码所有属性
            this.out.putShort(0);
        }

        /**
         * 编译方法代码主体
         * 
         * @return
         */
        private ByteVector compileMain() {
            int i = 0;
            List<CodeSeal> css = methodSeal.getCodings();
            int size = css.size();
            int limit = size - 1;
            CodeCompile cc = null;
            ByteVector out = new ByteVector();
            while (i < size) {
                cc = new CodeCompile(css.get(i++));
                if (cc.stacks > stacks) {
                    stacks = cc.stacks;
                }
                // 编译操作引用
                if (i <= limit) {
                    cc.compileRefer(i);
                }
                out.put(cc.out);
            }
            String type = cc == null ? TYPE.VOID.name : cc.type;
            if (type != null) {
                out.put(compileResult(type));
            }
            return out;
        }

        private ByteVector compileResult(String type) {
            ByteVector out = new ByteVector();
            // TODO 自动返回（默认类型转换）
            out.putByte(OPCODES.getReturn(methodSeal.getType()));
            return out;
        }

        /**
         * 执行码编译
         * 
         * @author issing
         * 
         */
        private class CodeCompile {

            private CodeCompile cc;

            private CodeSeal codeSeal;

            private int stacks;

            private ByteVector out;

            private String type;

            public CodeCompile(CodeSeal codeSeal) {
                this.cc = this;
                this.codeSeal = codeSeal;
                this.out = new ByteVector();
                this.compile();
            }

            /**
             * 编译入口
             * 
             */
            private void compile() {
                String owner = codeSeal.getOwner();
                Keyword keyword = Keyword.get(owner, c, mc, cc);
                if (keyword != null) {
                    keyword.compile();
                } else if (owner == null || this.compileArg(owner)) {
                    this.compileArgs();
                    this.compileOperate();
                } else {
                    throw new RuntimeException("Invalid coding owner: " + owner);
                }
            }

            /**
             * 编译所有参数
             * 
             */
            private void compileArgs() {
                String[] args = codeSeal.getArgs();
                int size = args.length;
                for (int i = 0; i < size; i++) {
                    if (!compileArg(args[i])) {
                        throw new RuntimeException("Invalid coding arg: "
                                + args[i]);
                    }
                }
            }

            /**
             * 编译指定参数
             * 
             * @param arg
             * @return
             */
            private boolean compileArg(String arg) {
                Object oarg = null;
                Object[] varg = null;
                FieldSeal farg = null;
                CodeSeal carg = null;
                if ((oarg = methodSeal.getConstMark(arg)) != null) {
                    compileConst(oarg);
                } else if ((varg = vs.get(arg)) != null) {
                    compileVariable((Integer) varg[0],
                            OPCODES.getLoad((String) varg[1]));
                } else if ((farg = methodSeal.getReferMark(arg)) != null) {
                    compileField(farg);
                } else if ((farg = classSeal.getField(arg)) != null) {
                    compileField(farg);
                } else if ((carg = methodSeal.getCodingMark(arg)) != null) {
                    CodeCompile cc = new CodeCompile(carg);
                    out.put(cc.out);
                    stacks += cc.stacks;
                    return true;
                } else {
                    return false;
                }
                stacks++;
                return true;
            }

            private void compileConst(Object arg) {
                // TODO 未做代码优化（BIPUSH/SIPUSH）
                int index = pool.takeConst(arg);
                if (arg instanceof Long || arg instanceof Double) {
                    out.put12(OPCODES.LDC2_W.value, index);
                    stacks++;
                } else if (index >= 256 || arg instanceof TYPE) {
                    out.put12(OPCODES.LDC_W.value, index);
                } else {
                    out.put11(OPCODES.LDC.value, index);
                }
            }

            private void compileVariable(int index, int opcode) {
                if (index < 4) {
                    out.putByte(26 + ((opcode - 21) << 2) + index); // xLOAD_n
                } else if (index >= 256) {
                    out.putByte(OPCODES.WIDE.value).put12(opcode, index);
                } else {
                    out.put11(opcode, index);
                }
            }

            private void compileField(FieldSeal refer) {
                if (ACCESS.isStatic(refer.getAccess())) {
                    out.putByte(OPCODES.GETSTATIC.value);
                } else {
                    out.put11(OPCODES.THIS.value, OPCODES.GETFIELD.value);
                }
                out.putShort(pool.takeField(TYPE.getDesc(refer.getOwner()),
                        refer.getName(), TYPE.getDesc(refer.getType(), true)));
            }

            private void compileOperate() {
                MethodSeal operate = getOperate();
                int opcode = OPCODES.filterMethod(operate.getAccess());
                type = operate.getType();
                out.putByte(opcode);
                if (OPCODES.isInterface(opcode)) {
                    String desc = TYPE.getMethDesc(type, operate.getArgTypes());
                    out.putShort(pool.takeMethod(true,
                            TYPE.getDesc(operate.getOwner()),
                            operate.getName(), desc));
                    out.put11(TYPE.getDescSize(desc) >> 2, 0);
                } else {
                    out.putShort(pool.takeMethod(false,
                            TYPE.getDesc(operate.getOwner()),
                            operate.getName(),
                            TYPE.getMethDesc(type, operate.getArgTypes())));
                }
            }

            public void compileRefer(int line) {
                String cid = codeSeal.getId();
                List<CodeSeal> css = methodSeal.getCodings();
                int size = css.size();
                int i = line;
                while (i < size) {
                    if (css.get(i++).hasRefer(cid)) {
                        // 声明本地变量
                        int opcode = OPCODES.getStore(type);
                        if (locals < 4) {
                            out.putByte(OPCODES.ASTORE.value + 1
                                    + ((opcode - OPCODES.ISTORE.value) << 2)
                                    + locals); // xSTORE_n
                        } else if (locals >= 256) {
                            out.putByte(OPCODES.WIDE.value);
                            out.put12(opcode, locals);
                        } else {
                            out.put11(opcode, locals);
                        }
                        vs.put(cid, MISC.var(locals++, type));
                        return;
                    }
                }
                if (line != size && TYPE.getType(type) != TYPE.VOID) {
                    out.putByte(OPCODES.POP.value);
                }
            }

            private MethodSeal getOperate() {
                MethodSeal operate = null;
                String operateName = codeSeal.getOperate();
                if (MISC.METH_PRINTLN.equals(operateName)) {
                    operate = new MethodSeal(TYPE.PRINTSTREAM.name,
                            OPCODES.INVOKEVIRTUAL.value, TYPE.VOID.name,
                            MISC.PRINTLN, TYPE.OBJECT.name);
                } else if (MISC.METH_SUPER.equals(operateName)) {
                    operate = new MethodSeal(classSeal.getSuperName(),
                            OPCODES.INVOKESPECIAL.value, TYPE.VOID.name,
                            MISC.INIT);
                } else if (MISC.RETURN.equals(codeSeal.getOwner())) {
                    operate = new MethodSeal(null, OPCODES.getReturn(methodSeal
                            .getType()), null, null, methodSeal.getType());
                } else if (MISC.ARRAY.equals(codeSeal.getOwner())) {
                    if (operateName.equals(MISC.LENGTH)) {
                        operate = new MethodSeal(null,
                                OPCODES.ARRAYLENGTH.value, TYPE.INT.name, null);
                    }
                } else {
                    operate = methodSeal.getOperateMark(operateName);
                }
                return operate;
            }
        }
    }

    private static class Keyword implements Cloneable {

        private static final Map<String, Keyword> keywords;

        protected Compiler c;

        protected MethodCompile mc;

        protected MethodCompile.CodeCompile cc;

        static {
            keywords = new HashMap<String, Keyword>();
            keywords.put(MISC.RETURN, new Keyword() {
                public void compile() {
                    cc.compileArgs();
                    MethodSeal operate = cc.getOperate();
                    String[] types = operate.getArgTypes();
                    if (types != null && types.length > 0) {
                        cc.type = types[0];
                    }
                }
            });
            keywords.put(MISC.THIS, new Keyword() {
                public void compile() {
                    cc.out.putByte(OPCODES.THIS.value);
                    cc.stacks++;
                    super.compile();
                }
            });
            keywords.put(MISC.NEW, new Keyword() {
                public void compile() {
                    cc.out.putByte(OPCODES.NEW.value);
                    cc.out.putShort(c.pool.takeClass(TYPE.getDesc(cc
                            .getOperate().getOwner())));
                    cc.out.putByte(OPCODES.DUP.value);
                    cc.stacks += 2;
                    super.compile();
                    cc.type = cc.getOperate().getOwner();
                }
            });
            keywords.put(MISC.OUT, new Keyword() {
                public void compile() {
                    cc.out.putByte(OPCODES.GETSTATIC.value);
                    cc.out.putShort(c.pool.takeField(TYPE.SYSTEM.getDesc(),
                            MISC.OUT, TYPE.PRINTSTREAM.getDesc(true)));
                    cc.stacks++;
                    super.compile();
                }
            });
            keywords.put(MISC.ARRAY, new Keyword() {
                public void compile() {
                    int i = 0;
                    String operate = cc.codeSeal.getOperate();
                    if (operate.matches(MISC.RGX_NEWARRAY)) {
                        operate = operate.substring(1, operate.length() - 1);
                        if (operate.length() == 0) {
                            cc.compileConst(cc.codeSeal.getArgs().length - 1);
                        } else {
                            cc.compileConst(Integer.valueOf(operate));
                        }
                        TYPE type = (TYPE) mc.methodSeal
                                .getConstMark(cc.codeSeal.getArgs()[0]);
                        if (type.isPrimitive()) {
                            cc.out.put11(OPCODES.NEWARRAY.value, type.sort);
                        } else {
                            cc.out.put12(OPCODES.ANEWARRAY.value,
                                    c.pool.takeConst(type));
                        }
                        cc.stacks++;
                        i++;
                    }
                    String[] args = cc.codeSeal.getArgs();
                    int size = args.length;
                    while (i < size) {
                        if (cc.compileArg(args[i++])) {
                            continue;
                        }
                        throw new RuntimeException("Invalid coding arg: "
                                + args[--i]);
                    }
                    if (MISC.LENGTH.equals(operate)) {
                        cc.out.putByte(OPCODES.ARRAYLENGTH.value);
                        cc.type = TYPE.INT.name;
                    }
                }
            });
        }

        public static Keyword get(String owner, Compiler c, MethodCompile mc,
                MethodCompile.CodeCompile cc) {
            Keyword keyword = null;
            synchronized (keywords) {
                keyword = keywords.get(owner);
                if (keyword != null) {
                    keyword = keyword.clone();
                    keyword.c = c;
                    keyword.mc = mc;
                    keyword.cc = cc;
                }
            }
            return keyword;
        }

        public void compile() {
            cc.compileArgs();
            cc.compileOperate();
        }

        public Keyword clone() {
            try {
                return (Keyword) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 常量池
     * 
     * @author issing
     * 
     */
    private static class ConstPool {

        private ByteVector out;

        private ConstKey key;

        private ConstIndex index;

        private int count;

        public ConstPool() {
            this.out = new ByteVector();
            this.key = new ConstKey();
            this.index = new ConstIndex();
            this.count = 1;
        }

        /**
         * 获取类
         * 
         * @param name
         * @return
         */
        public int takeClass(String name) {
            this.key.type = CONST.CLASS;
            this.key.values = new Object[] { name };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.put12(CONST.CLASS.value, takeUTF8(name));
                key.index = count++;
            }
            return key.index;
        }

        /**
         * 获取方法
         * 
         * @param isInterface
         * @param owner
         * @param name
         * @param desc
         * @return
         */
        public int takeMethod(boolean isInterface, String owner, String name,
                String desc) {
            this.key.type = isInterface ? CONST.IMETHOD : CONST.METHOD;
            this.key.values = new Object[] { owner, name, desc };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.put122(this.key.type.value, takeClass(owner),
                        takeNameType(name, desc));
                key.index = count++;
            }
            return key.index;
        }

        // /**
        // * 获取方法类型
        // *
        // * @param desc
        // * @return
        // */
        // public int takeMethodType(String desc) {
        // this.key.type = CONST.METHTYPE;
        // this.key.values = new Object[] { desc };
        // ConstKey key = this.index.take(this.key);
        // if (key.index == 0) {
        // out.put12(this.key.type.value, takeUTF8(desc));
        // key.index = count++;
        // }
        // return key.index;
        // }

        /**
         * 获取字段
         * 
         * @param owner
         * @param name
         * @param desc
         * @return
         */
        public int takeField(String owner, String name, String desc) {
            this.key.type = CONST.FIELD;
            this.key.values = new Object[] { owner, name, desc };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.put122(key.type.value, takeClass(owner),
                        takeNameType(name, desc));
                key.index = count++;
            }
            return key.index;
        }

        /**
         * 获取名称类型
         * 
         * @param name
         * @param desc
         * @return
         */
        public int takeNameType(String name, String desc) {
            this.key.type = CONST.NAMETYPE;
            this.key.values = new Object[] { name, desc };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.put122(key.type.value, takeUTF8(name), takeUTF8(desc));
                key.index = count++;
            }
            return key.index;
        }

        /**
         * 获取常量
         * 
         * @param value
         * @return
         */
        public int takeConst(Object value) {
            if (value instanceof Boolean) {
                return takeInteger(((Boolean) value).booleanValue() ? 1 : 0);
            } else if (value instanceof Character) {
                return takeInteger(((Character) value).charValue());
            } else if (value instanceof Byte) {
                return takeInteger(((Byte) value).intValue());
            } else if (value instanceof Short) {
                return takeInteger(((Short) value).intValue());
            } else if (value instanceof Integer) {
                return takeInteger(((Integer) value).intValue());
            } else if (value instanceof Float) {
                return takeFloat(((Float) value).floatValue());
            } else if (value instanceof Long) {
                return takeLong(((Long) value).longValue());
            } else if (value instanceof Double) {
                return takeDouble(((Double) value).doubleValue());
            } else if (value instanceof String) {
                return takeString((String) value);
            } else if (value instanceof TYPE) {
                TYPE type = (TYPE) value;
                int sort = type.sort;
                if (sort == TYPE.OBJECT.sort) {
                    return takeClass(type.getDesc());
                    // } else if (sort == TYPE.METHOD.sort) {
                    // return takeMethodType(type.getDesc());
                } else { // sort == primitive type or array
                    return takeClass(type.getDesc());
                }
            } else {
                throw new IllegalArgumentException("value " + value);
            }
        }

        /**
         * 获取整数
         * 
         * @param value
         * @return
         */
        public int takeInteger(int value) {
            this.key.type = CONST.INT;
            this.key.values = new Object[] { value };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.putByte(key.type.value).putInt(value);
                key.index = count++;
            }
            return key.index;
        }

        /**
         * 获取浮点数
         * 
         * @param value
         * @return
         */
        public int takeFloat(float value) {
            this.key.type = CONST.FLOAT;
            this.key.values = new Object[] { Float.floatToRawIntBits(value) };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.putByte(key.type.value)
                        .putInt((Integer) this.key.values[0]);
                key.index = count++;
            }
            return key.index;
        }

        /**
         * 获取长整形
         * 
         * @param value
         * @return
         */
        public int takeLong(long value) {
            this.key.type = CONST.LONG;
            this.key.values = new Object[] { value };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.putByte(key.type.value).putLong(value);
                key.index = count;
                count += 2;
            }
            return key.index;
        }

        /**
         * 获取双精度数
         * 
         * @param value
         * @return
         */
        public int takeDouble(double value) {
            this.key.type = CONST.DOUBLE;
            this.key.values = new Object[] { Double.doubleToRawLongBits(value) };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.putByte(key.type.value).putLong((Long) this.key.values[0]);
                key.index = count;
                count += 2;
            }
            return key.index;
        }

        /**
         * 获取字符串对象
         * 
         * @param value
         * @return
         */
        public int takeString(String value) {
            this.key.type = CONST.STRING;
            this.key.values = new Object[] { value };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.put12(key.type.value, takeUTF8(value));
                key.index = count++;
            }
            return key.index;
        }

        /**
         * 获取UTF8字符串描述
         * 
         * @param value
         * @return
         */
        public int takeUTF8(String value) {
            this.key.type = CONST.UTF8;
            this.key.values = new Object[] { value };
            ConstKey key = this.index.take(this.key);
            if (key.index == 0) {
                out.putByte(key.type.value).putUTF8(value);
                key.index = count++;
            }
            return key.index;
        }

        /**
         * 获取常量总数
         * 
         * @return
         */
        public int count() {
            return count;
        }

        /**
         * 获取常量池数据
         * 
         * @return
         */
        public byte[] toArray() {
            return out.toArray();
        }

        /**
         * 常量键
         * 
         * @author issing
         * 
         */
        private static class ConstKey implements Cloneable {

            private CONST type;

            private Object[] values;

            private int index;

            public boolean equals(Object instance) {
                boolean result = instance != null
                        && instance instanceof ConstKey;
                if (result) {
                    ConstKey key = (ConstKey) instance;
                    int length = this.values.length;
                    if (result = (this.type == key.type && length == key.values.length)) {
                        for (int i = 0; i < length; i++) {
                            if (!this.values[i].equals(key.values[i])) {
                                result = false;
                                break;
                            }
                        }
                    }
                }
                return result;
            }

            public Object clone() {
                try {
                    ConstKey key = (ConstKey) super.clone();
                    int length = this.values.length;
                    key.values = new Object[length];
                    System.arraycopy(this.values, 0, key.values, 0, length);
                    return key;
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * 常量索引
         * 
         * @author issing
         * 
         */
        private static class ConstIndex {

            private List<ConstKey> keys;

            public ConstIndex() {
                this.keys = new ArrayList<ConstKey>();
            }

            public ConstKey take(ConstKey key) {
                int index = keys.lastIndexOf(key);
                if (index == -1) {
                    key = (ConstKey) key.clone();
                    keys.add(key);
                } else {
                    key = keys.get(index);
                }
                return key;
            }
        }
    }
}
