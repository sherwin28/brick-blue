package net.isger.brick.blue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.isger.brick.blue.Marks.TYPE;

/**
 * 方法套件
 * 
 * @author issing
 * 
 */
public class MethodSeal {

    /** 所有者（类名） */
    private String owner;

    /** 访问标识 */
    private int access;

    /** 方法返回值类名 */
    private String type;

    /** 方法名称 */
    private String name;

    /** 方法参数类名 */
    private String[] argTypes;

    /** 标记常量 */
    private Map<String, Object> constMarks;

    /** 标记引用 */
    private Map<String, FieldSeal> referMarks;

    /** 标记操作 */
    private Map<String, MethodSeal> operateMarks;

    /** 标记代码 */
    private Map<String, CodeSeal> codingMarks;

    /** 代码集合 */
    private List<CodeSeal> codings;

    public MethodSeal(String owner, int access, String type, String name,
            String... argTypes) {
        this.owner = owner;
        this.access = access;
        this.type = type;
        this.name = name;
        this.argTypes = argTypes == null ? new String[0] : argTypes;
        this.constMarks = new HashMap<String, Object>();
        this.referMarks = new HashMap<String, FieldSeal>();
        this.operateMarks = new HashMap<String, MethodSeal>();
        this.codingMarks = new HashMap<String, CodeSeal>();
        this.codings = new ArrayList<CodeSeal>();
    }

    public MethodSeal(String owner, int access, Class<?> type, String name,
            Class<?>... argTypes) {
        this(owner, access, type.getName(), name, TYPE
                .getArgTypeNames(argTypes));
    }

    public void markConst(String alias, Object value) {
        this.constMarks.put(alias, value);
    }

    public void markRefer(String alias, String owner, int referMark,
            String name, String type) {
        this.referMarks.put(alias, new FieldSeal(owner, referMark, name, type));
    }

    public void markOperate(String alias, String owner, int operateMark,
            String type, String name, String... argTypes) {
        this.operateMarks.put(alias, new MethodSeal(owner, operateMark, type,
                name, argTypes));
    }

    public void markCoding(String alias, String refer, String operate,
            String... args) {
        this.codingMarks.put(alias, new CodeSeal(refer, operate, args));
    }

    public String coding(String refer, String operate, String... args) {
        CodeSeal code = new CodeSeal(refer, operate, args);
        this.codings.add(code);
        return code.getId();
    }

    public String getOwner() {
        return this.owner;
    }

    public int getAccess() {
        return this.access;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public String[] getArgTypes() {
        return this.argTypes;
    }

    public Object getConstMark(String alias) {
        return this.constMarks.get(alias);
    }

    public FieldSeal getReferMark(String alias) {
        return this.referMarks.get(alias);
    }

    public MethodSeal getOperateMark(String alias) {
        return this.operateMarks.get(alias);
    }

    public CodeSeal getCodingMark(String alias) {
        return this.codingMarks.get(alias);
    }

    public List<CodeSeal> getCodings() {
        return this.codings;
    }

}
