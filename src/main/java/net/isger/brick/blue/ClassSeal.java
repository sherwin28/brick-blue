package net.isger.brick.blue;

import java.util.ArrayList;
import java.util.List;

import net.isger.brick.blue.seal.ClassQuiet;
import net.isger.brick.blue.seal.ClassSmile;

/**
 * 类套件
 * 
 * @author issing
 * 
 */
public class ClassSeal implements Marks {

    /** JDK版本号 */
    private int version;

    /** 访问标识 */
    private int access;

    /** 类名 */
    private String name;

    /** 父类名 */
    private String superName;

    /** 接口名 */
    private String[] interfaces;

    /** 字段集合 */
    private List<FieldSeal> fields;

    /** 方法集合 */
    private List<MethodSeal> methods;

    protected ClassSeal(int version, int access, String name, String superName,
            String... interfaces) {
        this.version = VERSION.filter(version);
        this.access = access;
        this.name = name;
        this.superName = superName;
        this.interfaces = interfaces == null ? new String[0] : interfaces;
        this.fields = new ArrayList<FieldSeal>();
        this.methods = new ArrayList<MethodSeal>();
    }

    public static ClassSeal create(int version, int access, String name,
            String superName, String... interfaces) {
        ClassSeal cs = null;
        if (VERSION.isOriginal(version)) {
            cs = new ClassQuiet(version, access, name, superName, interfaces);
        } else {
            cs = new ClassSmile(version, access, name, superName, interfaces);
        }
        return cs;
    }

    /**
     * 创建字段
     * 
     * @param access
     * @param type
     * @param name
     * @return
     */
    public FieldSeal makeField(int access, String type, String name) {
        FieldSeal field = new FieldSeal(this.name, access, type, name);
        return this.addField(field) ? field : null;
    }

    /**
     * 添加字段
     * 
     * @param field
     * @return
     */
    public boolean addField(FieldSeal field) {
        boolean result = !this.fields.contains(field);
        if (result) {
            this.fields.add(field);
        }
        return result;
    }

    /**
     * 创建方法
     * 
     * @param access
     * @param type
     * @param name
     * @param argTypes
     * @return
     */
    public MethodSeal makeMethod(int access, String type, String name,
            String... argTypes) {
        MethodSeal method = new MethodSeal(this.name, access, type, name,
                argTypes);
        return this.addMethod(method) ? method : null;
    }

    public MethodSeal makeMethod(int access, Class<?> type, String name,
            Class<?>... argTypes) {
        MethodSeal method = new MethodSeal(this.name, access, type, name,
                argTypes);
        return this.addMethod(method) ? method : null;
    }

    /**
     * 添加方法
     * 
     * @param ms
     * @return
     */
    public boolean addMethod(MethodSeal ms) {
        boolean result = !this.methods.contains(ms);
        if (result) {
            this.methods.add(ms);
            ms.markConst("class", TYPE.getType(ms.getOwner()));
        }
        return result;
    }

    public int getVersion() {
        return this.version;
    }

    public int getAccess() {
        return this.access;
    }

    public String getName() {
        return this.name;
    }

    public String getSuperName() {
        return this.superName;
    }

    public String[] getInterfaces() {
        return this.interfaces;
    }

    public List<FieldSeal> getFields() {
        return this.fields;
    }

    public FieldSeal getField(String name) {
        for (FieldSeal field : this.fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public List<MethodSeal> getMethods() {
        return this.methods;
    }

}
