package net.isger.brick.blue;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.isger.brick.blue.Marks.MISC;

/**
 * 代码套件
 * 
 * @author issing
 * 
 */
public class CodeSeal {

    private static final Format FORMAT = new SimpleDateFormat("yyyyMMddhhmmss");

    private static long CID_AMOUNT = 0;

    private String id;

    private String owner;

    private String operate;

    private String[] args;

    public CodeSeal(String owner, String operate, String[] args) {
        this.owner = owner;
        this.operate = operate;
        this.args = args;
        this.id = String.format(MISC.FMT_CID, FORMAT.format(new Date()),
                CID_AMOUNT++);
        if (CID_AMOUNT == Integer.MAX_VALUE) {
            CID_AMOUNT = 0;
        }
    }

    public String getId() {
        return this.id;
    }

    public String getOwner() {
        return owner;
    }

    public String getOperate() {
        return operate;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean hasRefer(String id) {
        boolean isRefer = id.equals(owner);
        if (!isRefer) {
            for (String arg : args) {
                if (id.equals(arg)) {
                    isRefer = true;
                    break;
                }
            }
        }
        return isRefer;
    }

}
