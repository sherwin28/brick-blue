package net.isger.brick.blue;

/**
 * 字段套件
 * 
 * @author issing
 * 
 */
public class FieldSeal {

    /** 所有者（类名） */
    private String owner;

    /** 访问标识 */
    private int access;

    /** 字段类名 */
    private String type;

    /** 字段名称 */
    private String name;

    public FieldSeal(String owner, int access, String type, String name) {
        this.owner = owner;
        this.access = access;
        this.type = type;
        this.name = name;
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

}
