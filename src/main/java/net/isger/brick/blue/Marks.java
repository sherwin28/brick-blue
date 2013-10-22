package net.isger.brick.blue;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 标记
 * 
 * @author issing
 * 
 */
public interface Marks {

    /** 类文件魔数 */
    public static final int MAGIC = 0xcafebabe;

    /**
     * 版本标记
     * 
     * @author issing
     * 
     */
    public static enum VERSION {

        V0101(3 << 16 | 45), V0102(0 << 16 | 46), V0103(0 << 16 | 47),

        V0104(0 << 16 | 48), V0105(0 << 16 | 49), V0106(0 << 16 | 50),

        V0107(0 << 16 | 51);

        private static final Logger log = LoggerFactory.getLogger(Marks.class);

        public final int value;

        private VERSION(int value) {
            this.value = value;
        }

        /**
         * 版本号过滤
         * 
         * @param value
         * @return
         */
        public static int filter(int value) {
            // JDK 版本检查
            if (V0101.value == value || value >= V0102.value
                    && value <= V0107.value) {
                return value;
            }
            log.error("invalid version: {}", value);
            throw new IllegalArgumentException("invalid version: " + value);
        }

        /**
         * 原始版本判定
         * 
         * @param value
         * @return
         */
        public static boolean isOriginal(int value) {
            // JDK 1.4及以下为原始版本
            return V0101.value == value || value >= V0102.value
                    && value <= V0104.value;
        }

    }

    /**
     * 常量标记
     * 
     * @author issing
     * 
     */
    public static enum CONST {

        UTF8(1), INT(3), FLOAT(4), LONG(5), DOUBLE(6),

        CLASS(7), STRING(8), FIELD(9), METHOD(10), IMETHOD(11),

        NAMETYPE(12), METHTYPE(16);

        public final int value;

        private CONST(int value) {
            this.value = value;
        }

    }

    /**
     * 访问标记
     * 
     * @author issing
     * 
     */
    public static enum ACCESS {

        PUBLIC(0x0001), PRIVATE(0x0002), PROTECTED(0x0004),

        STATIC(0x0008),

        FINAL(0x0010),

        INTERFACE(0x0200), ABSTRACT(0x0400), ANNOTATION(0x2000), ENUM(0x4000);

        public final int value;

        private ACCESS(int value) {
            this.value = value;
        }

        /**
         * 静态访问
         * 
         * @param access
         * @return
         */
        public static boolean isStatic(int access) {
            return (STATIC.value & access) == STATIC.value;
        }

    }

    /**
     * 操作标记
     * 
     * @author issing
     * 
     */
    public static enum OPCODES {

        NOP(0),

        ACONST_NULL(1), ICONST_M1(2),

        ICONST_0(3), ICONST_1(4), ICONST_2(5), // -
        ICONST_3(6), ICONST_4(7), ICONST_5(8), // -
        LCONST_0(9), LCONST_1(10), // -
        FCONST_0(11), FCONST_1(12), FCONST_2(13), // -
        DCONST_0(14), DCONST_1(15), // -

        BIPUSH(16), SIPUSH(17),

        LDC(18), LDC_W(19), LDC2_W(20),

        ILOAD(21), // ILOAD_0(26), ILOAD_1(27), ILOAD_2(28), ILOAD_3(29),
        LLOAD(22), // LLOAD_0(30), LLOAD_1(31), LLOAD_2(32), LLOAD_3(33),
        FLOAD(23), // FLOAD_0(34), FLOAD_1(35), FLOAD_2(36), FLOAD_3(37),
        DLOAD(24), // DLOAD_0(38), DLOAD_1(39), DLOAD_2(40), DLOAD_3(41),
        ALOAD(25), // ALOAD_0(42), ALOAD_1(43), ALOAD_2(44), ALOAD_3(45),
        THIS(42),

        IALOAD(46), LALOAD(47), FALOAD(48), DALOAD(49), // -
        AALOAD(50), BALOAD(51), CALOAD(52), SALOAD(53), // -

        ISTORE(54), // ISTORE_0(59), ISTORE_1(60), ISTORE_2(61), ISTORE_3(62),
        LSTORE(55), // LSTORE_0(63), LSTORE_1(64), LSTORE_2(65), LSTORE_3(66),
        FSTORE(56), // FSTORE_0(67), FSTORE_1(68), FSTORE_2(69), FSTORE_3(70),
        DSTORE(57), // DSTORE_0(71), DSTORE_1(72), DSTORE_2(73), DSTORE_3(74),
        ASTORE(58), // ASTORE_0(75), ASTORE_1(76), ASTORE_2(77), ASTORE_3(78),

        IASTORE(79), LASTORE(80), FASTORE(81), DASTORE(82), // -
        AASTORE(83), BASTORE(84), CASTORE(85), SASTORE(86), // -

        POP(87), POP2(88),

        DUP(89), DUP_X1(90), DUP_X2(91), DUP2(92), DUP2_X1(93), DUP2_X2(94),

        SWAP(95),

        IADD(96), LADD(97), FADD(98), DADD(99), // -
        ISUB(100), LSUB(101), FSUB(102), DSUB(103), // -
        IMUL(104), LMUL(105), FMUL(106), DMUL(107), // -
        IDIV(108), LDIV(109), FDIV(110), DDIV(111), // -

        IREM(112), LREM(113), FREM(114), DREM(115), // -
        INEG(116), LNEG(117), FNEG(118), DNEG(119), // -
        ISHL(120), LSHL(121), ISHR(122), LSHR(123), // -
        IUSHR(124), LUSHR(125), // -
        IAND(126), LAND(127), IOR(128), LOR(129), IXOR(130), LXOR(131), // -

        IINC(132),

        I2L(133), I2F(134), I2D(135), // -
        L2I(136), L2F(137), L2D(138), // -
        F2I(139), F2L(140), F2D(141), // -
        D2I(142), D2L(143), D2F(144), // -
        I2B(145), I2C(146), I2S(147), // -

        LCMP(148), FCMPL(149), FCMPG(150), DCMPL(151), DCMPG(152),

        IFEQ(153), IFNE(154), IFLT(155), IFGE(156), IFGT(157), IFLE(158), // -
        IF_ICMPEQ(159), IF_ICMPNE(160), IF_ICMPLT(161), IF_ICMPGE(162), // -
        IF_ICMPGT(163), IF_ICMPLE(164), IF_ACMPEQ(165), IF_ACMPNE(166), // -

        GOTO(167), JSR(168), RET(169), TABLESWITCH(170), LOOKUPSWITCH(171),

        IRETURN(172), LRETURN(173), FRETURN(174), // -
        DRETURN(175), ARETURN(176), RETURN(177), // -

        GETSTATIC(178), PUTSTATIC(179), GETFIELD(180), PUTFIELD(181),

        INVOKEVIRTUAL(182), INVOKESPECIAL(183), // -
        INVOKESTATIC(184), INVOKEINTERFACE(185), // -
        INVOKEDYNAMIC(186),

        NEW(187), // -
        NEWARRAY(188), ANEWARRAY(189), ARRAYLENGTH(190), // -

        ATHROW(191), CHECKCAST(192), INSTANCEOF(193),

        MONITORENTER(194), MONITOREXIT(195),

        WIDE(196),

        MULTIANEWARRAY(197),

        IFNULL(198), IFNONNULL(199),

        GOTO_W(200), JSR_W(201);

        private static final Logger log = LoggerFactory.getLogger(Marks.class);

        public final int value;

        private OPCODES(int value) {
            this.value = value;
        }

        /**
         * 检查字段操作
         * 
         * @param access
         * @return
         */
        public static int filterField(int access) {
            // 操作检查
            if (access < GETSTATIC.value || access > PUTFIELD.value) {
                log.error("invalid operate: {}", access);
                throw new IllegalArgumentException("invalid operate: " + access);
            }
            return access;
        }

        /**
         * 检查方法操作
         * 
         * @param access
         * @return
         */
        public static int filterMethod(int access) {
            // 操作检查
            if (access < INVOKEVIRTUAL.value || access > INVOKEDYNAMIC.value) {
                log.error("invalid operate: {}", access);
                throw new IllegalArgumentException("invalid operate: " + access);
            }
            return access;
        }

        /**
         * 是否为接口操作
         * 
         * @param access
         * @return
         */
        public static boolean isInterface(int access) {
            return INVOKEINTERFACE.value == access;
        }

        /**
         * 是否为返回操作码
         * 
         * @param opcode
         * @return
         */
        public static boolean isReturn(int opcode) {
            return opcode >= IRETURN.value && opcode <= RETURN.value;
        }

        /**
         * 是否为数组操作码
         * 
         * @param opcode
         * @return
         */
        public static boolean isArray(int opcode) {
            return opcode >= NEWARRAY.value && opcode <= ARRAYLENGTH.value;
        }

        /**
         * 获取指定类型存储操作码
         * 
         * @param type
         * @return
         */
        public static int getStore(String type) {
            return getStore(TYPE.getType(type).sort);
        }

        /**
         * 获取指定类型存储操作码
         * 
         * @param sort
         * @return
         */
        public static int getStore(int sort) {
            return getOpcode(sort, ASTORE.value);
        }

        /**
         * 获取指定类型加载操作码
         * 
         * @param type
         * @return
         */
        public static int getLoad(String type) {
            return getOpcode(TYPE.getType(type).sort, ALOAD.value);
        }

        /**
         * 获取指定类型返回操作码
         * 
         * @param type
         * @return
         */
        public static int getReturn(String type) {
            return getReturn(TYPE.getType(type).sort);
        }

        /**
         * 获取指定类型返回操作码
         * 
         * @param sort
         * @return
         */
        public static int getReturn(int sort) {
            return getOpcode(sort, ARETURN.value);
        }

        /**
         * 获取操作码
         * 
         * @param sort
         * @param opcode
         * @return
         */
        private static int getOpcode(int sort, int opcode) {
            if (sort == TYPE.BOOLEAN.sort || sort == TYPE.CHAR.sort
                    || sort == TYPE.BYTE.sort || sort == TYPE.SHORT.sort
                    || sort == TYPE.INT.sort) {
                opcode -= 4;
            } else if (sort == TYPE.LONG.sort) {
                opcode -= 3;
            } else if (sort == TYPE.FLOAT.sort) {
                opcode -= 2;
            } else if (sort == TYPE.DOUBLE.sort) {
                opcode -= 1;
            } else if (sort == TYPE.VOID.sort) {
                opcode++;
            }
            return opcode;
        }
    }

    /**
     * 类型修饰符
     * 
     * @author issing
     * 
     */
    public static final class TYPE {
        private static final Map<String, TYPE> TYPES;

        // 原始数据类型
        public static final TYPE VOID;
        public static final TYPE BOOLEAN;
        public static final TYPE CHAR;
        public static final TYPE BYTE;
        public static final TYPE SHORT;
        public static final TYPE INT;
        public static final TYPE FLOAT;
        public static final TYPE LONG;
        public static final TYPE DOUBLE;

        // 引用数据类型
        public static final TYPE ARRAY;
        public static final TYPE OBJECT;
        public static final TYPE STRINGS;
        public static final TYPE STRING;
        public static final TYPE SYSTEM;
        public static final TYPE PRINTSTREAM;
        public static final TYPE CLASS;

        public final int sort;

        public final String name;

        private final String desc;

        private final String seal;

        static {
            TYPES = new HashMap<String, TYPE>();

            VOID = new TYPE(0, "void", "V", "java.lang.Void");
            BOOLEAN = new TYPE(4, "boolean", "Z", "java.lang.Boolean");
            CHAR = new TYPE(5, "char", "C", "java.lang.Character");
            BYTE = new TYPE(8, "byte", "B", "java.lang.Byte");
            SHORT = new TYPE(9, "short", "S", "java.lang.Short");
            INT = new TYPE(10, "int", "I", "java.lang.Integer");
            FLOAT = new TYPE(6, "float", "F", "java.lang.Float");
            LONG = new TYPE(11, "long", "J", "java.lang.Long");
            DOUBLE = new TYPE(7, "double", "D", "java.lang.Double");

            OBJECT = new TYPE(1, "java.lang.Object");
            ARRAY = new TYPE(2, "java.lang.Object[]");
            STRING = new TYPE(1, "java.lang.String");
            STRINGS = new TYPE(2, "java.lang.String[]");
            SYSTEM = new TYPE(1, "java.lang.System");
            PRINTSTREAM = new TYPE(1, "java.io.PrintStream");
            CLASS = new TYPE(1, "java.lang.Class");

            TYPES.put(null, VOID);
        }

        private TYPE(int sort) {
            this(sort, null, null, null);
        }

        private TYPE(int sort, String name) {
            this(sort, name, null, null);
        }

        private TYPE(int sort, String name, String desc, String seal) {
            this.sort = sort;
            this.name = name;
            this.desc = desc;
            this.seal = seal == null ? name : seal;
            TYPES.put(this.name, this);
            if (this.seal != null) {
                TYPES.put(this.seal, this);
            }
        }

        /**
         * 是否原始数据类型
         * 
         * @return
         */
        public boolean isPrimitive() {
            return isPrimitive(sort);
        }

        /**
         * 获取描述信息
         * 
         * @return
         */
        public String getDesc() {
            return getDesc(false);
        }

        /**
         * 获取描述信息
         * 
         * @param isDecor
         * @return
         */
        public String getDesc(boolean isDecor) {
            return desc == null ? getDesc(name, isDecor) : desc;
        }

        /**
         * 获取描述信息
         * 
         * @return
         */
        public String getSealDesc() {
            return getSealDesc(false);
        }

        /**
         * 获取描述信息
         * 
         * @param isDecor
         * @return
         */
        public String getSealDesc(boolean isDecor) {
            return getDesc(seal, isDecor);
        }

        /**
         * 是否原始数据类型
         * 
         * @param sort
         * @return
         */
        public static boolean isPrimitive(int sort) {
            return sort >= BOOLEAN.sort && sort <= LONG.sort
                    || sort == VOID.sort;
        }

        /**
         * 获取描述信息
         * 
         * @param value
         * @return
         */
        public static String getDesc(String value) {
            return getDesc(value, false);
        }

        /**
         * 获取描述信息
         * 
         * @param value
         * @param isDecor
         * @return
         */
        public static String getDesc(String value, boolean isDecor) {
            StringBuffer buffer = new StringBuffer(128);
            makeDesc(buffer, value.replaceAll("[.]", "/"), isDecor);
            return buffer.toString();
        }

        /**
         * 获取描述信息
         * 
         * @param typeName
         * @param argTypeNames
         * @return
         */
        public static String getMethDesc(String typeName,
                String... argTypeNames) {
            StringBuffer desc = new StringBuffer(128);
            desc.append('(');
            for (String argTypeName : argTypeNames) {
                desc.append(getDesc(argTypeName, true));
            }
            desc.append(')').append(getDesc(typeName, true));
            return desc.toString();
        }

        /**
         * 获取描述占用空间大小
         * 
         * @param desc
         * @return
         */
        public static int getDescSize(String desc) {
            int amount = 1;
            int index = 1;
            char c = 0;
            while (true) {
                c = desc.charAt(index++);
                if (c == ')') {
                    c = desc.charAt(index);
                    return amount << 2
                            | (c == VOID.desc.charAt(0) ? 0 : (c == DOUBLE.desc
                                    .charAt(0) || c == LONG.desc.charAt(0) ? 2
                                    : 1));
                } else if (c == 'L') {
                    while (desc.charAt(index++) != ';') {
                    }
                    amount += 1;
                } else if (c == '[') {
                    while ((c = desc.charAt(index)) == '[') {
                        ++index;
                    }
                    if (c == DOUBLE.desc.charAt(0) || c == LONG.desc.charAt(0)) {
                        amount -= 1;
                    }
                } else if (c == DOUBLE.desc.charAt(0)
                        || c == LONG.desc.charAt(0)) {
                    amount += 2;
                } else {
                    amount += 1;
                }
            }
        }

        /**
         * 获取类型
         * 
         * @param typeName
         * @param names
         * @return
         */
        public static TYPE getType(String typeName) {
            TYPE type = TYPES.get(typeName);
            if (type == null) {
                if (typeName.endsWith("[]")) {
                    type = new TYPE(ARRAY.sort, typeName);
                } else {
                    type = new TYPE(OBJECT.sort, typeName);
                }
            }
            return type;
        }

        /**
         * 制造描述信息
         * 
         * @param buffer
         * @param typeName
         * @param isDecor
         */
        private static void makeDesc(StringBuffer buffer, String typeName,
                boolean isDecor) {
            TYPE type = null;
            if (typeName.endsWith("[]")) {
                buffer.append('[');
                makeDesc(buffer, typeName.substring(0, typeName.length() - 2),
                        true);
            } else if ((type = TYPES.get(typeName)) != null
                    && type.isPrimitive()) {
                buffer.append(type.desc);
            } else if (isDecor) {
                buffer.append('L').append(typeName).append(';');
            } else {
                buffer.append(typeName);
            }
        }

    }

    /**
     * 杂项修饰符
     * 
     * @author issing
     * 
     */
    public static class MISC {

        public static final String THIS = "this";

        public static final String NEW = "new";

        public static final String ARRAY = "array";

        public static final String LENGTH = "length";

        public static final String RGX_NEWARRAY = "\\[\\d*\\]";

        public static final String RETURN = "return";

        public static final String OUT = "out";

        public static final String METH_PRINTLN = "println(obj)";

        public static final String METH_SUPER = "super()";

        public static final String INIT = "<init>";

        public static final String PRINTLN = "println";

        public static final String FMT_ARGS = "args[%d]";

        public static final String FMT_CID = "CID%s%d";

        private MISC() {
        }

        public static String arg(int i) {
            return String.format(FMT_ARGS, i);
        }

        public static Object[] var(int i, String type) {
            return new Object[] { i, type };
        }
    }

}
